package org.example.project.features.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BiometricSessionLockTest {
    @Test
    fun manualLockRequiresTheOptedInAccount() {
        val lock = BiometricSessionLock()
        lock.lock("seller")
        assertFalse(lock.isLocked("seller"))

        lock.setEnabled("seller", enabled = true, available = true)
        lock.lock("other-account")
        assertFalse(lock.isLocked("seller"))

        lock.lock("seller")
        assertTrue(lock.isLocked("seller"))
        assertNotNull(lock.beginUnlock("seller"))
    }

    @Test
    fun unavailableBiometricsLeavesNormalSignInAvailable() {
        val lock = BiometricSessionLock()
        lock.setEnabled("user", enabled = true, available = false)
        lock.onBackground()

        assertFalse(lock.isEnabled("user"))
        assertFalse(lock.isLocked("user"))
    }

    @Test
    fun unlockRequiresBiometricsAndBackendValidation() {
        val lock = BiometricSessionLock()
        lock.setEnabled("user", enabled = true, available = true)
        lock.onBackground()
        val attempt = assertNotNull(lock.beginUnlock("user"))

        lock.completeUnlock("user", attempt, biometricConfirmed = false, sessionConfirmed = true)
        assertTrue(lock.isLocked("user"))
        lock.completeUnlock("user", attempt, biometricConfirmed = true, sessionConfirmed = false)
        assertTrue(lock.isLocked("user"))
        lock.completeUnlock("user", attempt, biometricConfirmed = true, sessionConfirmed = true)
        assertFalse(lock.isLocked("user"))
    }

    @Test
    fun backgroundingAgainInvalidatesAnUnlockInProgress() {
        val lock = BiometricSessionLock()
        lock.setEnabled("user", enabled = true, available = true)
        lock.onBackground()
        val attempt = assertNotNull(lock.beginUnlock("user"))

        lock.onBackground()
        lock.completeUnlock("user", attempt, biometricConfirmed = true, sessionConfirmed = true)

        assertTrue(lock.isLocked("user"))
    }

    @Test
    fun signingOutForFallbackClearsTheLockForTheNextLogin() {
        val lock = BiometricSessionLock()
        lock.setEnabled("user", enabled = true, available = true)
        lock.onBackground()

        lock.onAccountChanged(null)

        assertFalse(lock.isEnabled("user"))
        assertFalse(lock.isLocked("user"))
    }
}
