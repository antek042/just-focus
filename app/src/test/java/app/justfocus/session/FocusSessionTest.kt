package app.justfocus.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusSessionTest {

    private val session = FocusSession.Running(startedAtMillis = 1_000, endsAtMillis = 11_000)

    @Test
    fun `remaining time counts down and never goes below zero`() {
        assertEquals(10_000, session.remainingMillis(nowMillis = 1_000))
        assertEquals(4_000, session.remainingMillis(nowMillis = 7_000))
        assertEquals(0, session.remainingMillis(nowMillis = 20_000))
    }

    @Test
    fun `remaining fraction goes from one to zero`() {
        assertEquals(1f, session.remainingFraction(nowMillis = 1_000), 0.001f)
        assertEquals(0.5f, session.remainingFraction(nowMillis = 6_000), 0.001f)
        assertEquals(0f, session.remainingFraction(nowMillis = 11_000), 0.001f)
    }

    @Test
    fun `session ends exactly at its end time`() {
        assertFalse(session.hasEnded(nowMillis = 10_999))
        assertTrue(session.hasEnded(nowMillis = 11_000))
    }
}
