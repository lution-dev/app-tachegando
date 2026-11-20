package org.example.project.features.auth

import java.security.KeyPairGenerator
import java.security.spec.MGF1ParameterSpec
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class BiometricEnvelopeTest {
    private val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()

    @Test
    fun protectedRefreshTokenSurvivesDiskRoundTrip() {
        val token = "refresh-token-" + "x".repeat(220)
        val payload = AndroidBiometricVault.encrypt("account-a", token, keys.public).encode()
        assertFalse(payload.decodeToString().contains(token))
        val restored = BiometricEnvelope.decode(payload)

        assertEquals("account-a", restored.userId)
        assertEquals(token, AndroidBiometricVault.decrypt(restored, fixtureDecryptCipher()))
    }

    @Test
    fun replacingTheAccountIdInvalidatesTheTokenEnvelope() {
        val envelope = AndroidBiometricVault.encrypt("account-a", "test-token", keys.public)

        assertFailsWith<AEADBadTagException> {
            AndroidBiometricVault.decrypt(envelope.copy(userId = "account-b"), fixtureDecryptCipher())
        }
    }

    private fun fixtureDecryptCipher() = Cipher.getInstance("RSA/ECB/OAEPPadding").apply {
        val padding = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        init(Cipher.DECRYPT_MODE, keys.private, padding)
    }
}
