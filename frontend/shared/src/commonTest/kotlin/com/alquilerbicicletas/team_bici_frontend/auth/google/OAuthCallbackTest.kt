package com.alquilerbicicletas.team_bici_frontend.auth.google

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OAuthCallbackTest {
    @Test
    fun parsesWebCallbackCode() {
        assertEquals(
            OAuthCallback.Success("abc-123_X"),
            OAuthCallbackParser.parse("http://localhost:3000/auth/google/callback?code=abc-123_X"),
        )
    }

    @Test
    fun parsesMobileDeepLink() {
        assertEquals(
            OAuthCallback.Success("abc"),
            OAuthCallbackParser.parse("teambici://auth/google/callback?code=abc"),
        )
    }

    @Test
    fun parsesFailure() {
        assertEquals(
            OAuthCallback.Failure("google_auth_failed"),
            OAuthCallbackParser.parse("http://localhost:3000/auth/google/callback?error=google_auth_failed"),
        )
    }

    @Test
    fun ignoresUnrelatedUrls() {
        assertNull(OAuthCallbackParser.parse("http://localhost:3000/"))
        assertNull(OAuthCallbackParser.parse("http://localhost:3000/other?code=abc"))
    }

    @Test
    fun inboxDeliversEachCallbackOnce() {
        val inbox = OAuthCallbackInbox()
        assertFalse(inbox.publish("http://localhost:3000/"))
        assertTrue(inbox.publish("http://localhost:3000/auth/google/callback?code=abc"))

        val callback = inbox.pending.value!!
        assertTrue(inbox.consume(callback))
        assertFalse(inbox.consume(callback))
        assertNull(inbox.pending.value)
    }
}
