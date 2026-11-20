package org.example.project.core.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GoogleLoginTest {
    @Test
    fun acceptsOnlyTheRegisteredCodeCallback() {
        assertEquals(GoogleCallback.Code("test-code"), parseGoogleCallback("$GOOGLE_REDIRECT_URL?code=test-code"))
        assertNull(parseGoogleCallback("https://example.org/auth?code=test-code"))
        assertNull(parseGoogleCallback("br.com.tachegandoapp://unrelated?code=test-code"))
        assertNull(parseGoogleCallback("$GOOGLE_REDIRECT_URL/other?code=test-code"))
    }

    @Test
    fun rejectsMissingOrAmbiguousCodesAndNeverImportsFragmentTokens() {
        val failure = GoogleCallback.Failed(cancelled = false)
        assertEquals(failure, parseGoogleCallback("$GOOGLE_REDIRECT_URL?code="))
        assertEquals(failure, parseGoogleCallback("$GOOGLE_REDIRECT_URL?code=one&code=two"))
        assertEquals(failure, parseGoogleCallback("$GOOGLE_REDIRECT_URL#access_token=test-token"))
    }

    @Test
    fun reportsCancellationEvenIfCallbackContainsACode() {
        val cancelled = GoogleCallback.Failed(cancelled = true)
        assertEquals(cancelled, parseGoogleCallback("$GOOGLE_REDIRECT_URL?error=access_denied&code=ignored"))
        assertEquals(cancelled, parseGoogleCallback("$GOOGLE_REDIRECT_URL#error=access_denied"))
    }
}
