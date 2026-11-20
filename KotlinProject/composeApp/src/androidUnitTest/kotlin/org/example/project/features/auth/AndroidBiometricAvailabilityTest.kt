package org.example.project.features.auth

import androidx.biometric.BiometricManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AndroidBiometricAvailabilityTest {
    @Test
    fun unknownStatusStillAllowsTheStrongSystemPrompt() {
        assertEquals(
            BiometricAvailability.Available,
            androidBiometricAvailability(BiometricManager.BIOMETRIC_STATUS_UNKNOWN)
        )
    }

    @Test
    fun unavailableBiometricsExplainWhyTheOptionIsDisabled() {
        val expectedByStatus = mapOf(
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED to BiometricAvailability.NotEnrolled,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE to BiometricAvailability.SensorUnavailable,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED to BiometricAvailability.SecurityUpdateRequired,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE to BiometricAvailability.Unsupported
        )
        expectedByStatus.forEach { (status, expected) ->
            val availability = androidBiometricAvailability(status)
            assertEquals(expected, availability)
            assertNotNull(availability.message)
        }
    }
}
