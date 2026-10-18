package app.justfocus.blocking

import app.justfocus.FakeClock
import app.justfocus.InMemoryAppAccessStore
import app.justfocus.InMemorySessionStore
import app.justfocus.RecordingSessionEffects
import app.justfocus.apps.AppAccess
import app.justfocus.apps.AppAccessRepository
import app.justfocus.session.FocusSessionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusGuardTest {

    private val clock = FakeClock(nowMillis = 0)
    private val sessionManager = FocusSessionManager(InMemorySessionStore(), RecordingSessionEffects(), clock)
    private val appAccessRepository = AppAccessRepository(
        InMemoryAppAccessStore(
            saved = mapOf("com.notes" to AppAccess.ALLOWED, "com.family.chat" to AppAccess.EMERGENCY),
        ),
    )
    private val guard = FocusGuard(sessionManager, appAccessRepository, clock) {
        mapOf("com.phone" to AppAccess.EMERGENCY, "com.launcher" to AppAccess.ALLOWED)
    }

    @Test
    fun `nothing is blocked or held back without a session`() {
        assertFalse(guard.shouldBlockApp("com.game"))
        assertNull(guard.notificationSnoozeMillis("com.game"))
    }

    @Test
    fun `during a session only blocked apps are sent back`() {
        sessionManager.start(durationMinutes = 25)

        assertTrue(guard.shouldBlockApp("com.game"))
        assertFalse(guard.shouldBlockApp("com.notes"))
        assertFalse(guard.shouldBlockApp("com.family.chat"))
        assertFalse(guard.shouldBlockApp("com.phone"))
    }

    @Test
    fun `during a session you can go back to the home screen`() {
        sessionManager.start(durationMinutes = 25)

        assertFalse(guard.shouldBlockApp("com.launcher"))
        assertEquals(25 * 60_000L, guard.notificationSnoozeMillis("com.launcher"))
    }

    @Test
    fun `during a session non-emergency notifications wait until the session ends`() {
        sessionManager.start(durationMinutes = 25)
        clock.advanceMinutes(10)

        assertEquals(15 * 60_000L, guard.notificationSnoozeMillis("com.game"))
        assertEquals(15 * 60_000L, guard.notificationSnoozeMillis("com.notes"))
        assertNull(guard.notificationSnoozeMillis("com.family.chat"))
        assertNull(guard.notificationSnoozeMillis("com.phone"))
    }

    @Test
    fun `blocking stops as soon as the time is up`() {
        sessionManager.start(durationMinutes = 25)
        clock.advanceMinutes(25)

        assertFalse(guard.shouldBlockApp("com.game"))
        assertNull(guard.notificationSnoozeMillis("com.game"))
    }
}
