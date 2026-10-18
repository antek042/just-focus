package app.justfocus.session

import app.justfocus.FakeClock
import app.justfocus.InMemorySessionStore
import app.justfocus.RecordingSessionEffects
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusSessionManagerTest {

    private val clock = FakeClock(nowMillis = 0)
    private val store = InMemorySessionStore()
    private val effects = RecordingSessionEffects()

    private fun createManager() = FocusSessionManager(store, effects, clock)

    @Test
    fun `starting a session makes focus active until the time is up`() {
        val manager = createManager()

        manager.start(durationMinutes = 25)

        assertEquals(FocusSession.Running(startedAtMillis = 0, endsAtMillis = 25 * 60_000L), manager.session.value)
        assertTrue(manager.isFocusActive())
        assertEquals(listOf("started until 1500000"), effects.events)
    }

    @Test
    fun `starting a session saves it and remembers the chosen duration`() {
        createManager().start(durationMinutes = 45)

        assertTrue(store.session is FocusSession.Running)
        assertEquals(45, store.lastDurationMinutes)
        assertEquals(45, createManager().lastDurationMinutes)
    }

    @Test
    fun `last duration falls back to the default when nothing was saved`() {
        assertEquals(SessionDuration.DEFAULT_MINUTES, createManager().lastDurationMinutes)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a session must last longer than zero minutes`() {
        createManager().start(durationMinutes = 0)
    }

    @Test
    fun `stopping ends the session early without the completed notification`() {
        val manager = createManager()
        manager.start(durationMinutes = 25)

        manager.stop()

        assertEquals(FocusSession.Idle, manager.session.value)
        assertEquals("stopped", effects.events.last())
    }

    @Test
    fun `stopping when idle does nothing`() {
        createManager().stop()

        assertTrue(effects.events.isEmpty())
    }

    @Test
    fun `focus is no longer active once the time is up, even before completion`() {
        val manager = createManager()
        manager.start(durationMinutes = 25)

        clock.advanceMinutes(25)

        assertFalse(manager.isFocusActive())
        assertNull(manager.activeSession())
    }

    @Test
    fun `completing before the time is up does nothing`() {
        val manager = createManager()
        manager.start(durationMinutes = 25)
        clock.advanceMinutes(24)

        manager.completeIfEnded()

        assertTrue(manager.session.value is FocusSession.Running)
        assertFalse("completed" in effects.events)
    }

    @Test
    fun `completing after the time is up ends the session exactly once`() {
        val manager = createManager()
        manager.start(durationMinutes = 25)
        clock.advanceMinutes(30)

        manager.completeIfEnded()
        manager.completeIfEnded()

        assertEquals(FocusSession.Idle, manager.session.value)
        assertEquals(1, effects.events.count { it == "completed" })
    }

    @Test
    fun `restoring a saved session that is still running reschedules it`() {
        store.session = FocusSession.Running(startedAtMillis = 0, endsAtMillis = 60_000)

        createManager().restore()

        assertEquals(listOf("started until 60000"), effects.events)
    }

    @Test
    fun `restoring a saved session that already ended completes it`() {
        store.session = FocusSession.Running(startedAtMillis = 0, endsAtMillis = 60_000)
        clock.advanceMinutes(5)

        val manager = createManager()
        manager.restore()

        assertEquals(FocusSession.Idle, manager.session.value)
        assertEquals(listOf("completed"), effects.events)
    }
}
