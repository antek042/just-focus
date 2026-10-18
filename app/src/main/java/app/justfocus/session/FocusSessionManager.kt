package app.justfocus.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for the focus session.
 *
 * "Stopped" means the user ended the session early, "completed" means the timer ran out.
 */
class FocusSessionManager(
    private val store: SessionStore,
    private val effects: SessionEffects,
    private val clock: Clock,
) {
    private val mutableSession = MutableStateFlow(store.loadSession())
    val session: StateFlow<FocusSession> = mutableSession.asStateFlow()

    val lastDurationMinutes: Int
        get() = store.loadLastDurationMinutes() ?: SessionDuration.DEFAULT_MINUTES

    /** The running session, or null when idle or when its time is already up. */
    fun activeSession(): FocusSession.Running? =
        (session.value as? FocusSession.Running)?.takeUnless { it.hasEnded(clock.nowMillis()) }

    fun isFocusActive(): Boolean = activeSession() != null

    fun start(durationMinutes: Int) {
        require(durationMinutes > 0) { "Session duration must be positive, was $durationMinutes" }
        val now = clock.nowMillis()
        val session = FocusSession.Running(
            startedAtMillis = now,
            endsAtMillis = now + SessionDuration.minutesToMillis(durationMinutes),
        )
        store.saveLastDurationMinutes(durationMinutes)
        updateSession(session)
        effects.onSessionStarted(session)
    }

    fun stop() {
        if (session.value == FocusSession.Idle) return
        updateSession(FocusSession.Idle)
        effects.onSessionStopped()
    }

    /** Safe to call any number of times; completes the session only once its time is up. */
    fun completeIfEnded() {
        val running = session.value as? FocusSession.Running ?: return
        if (!running.hasEnded(clock.nowMillis())) return
        updateSession(FocusSession.Idle)
        effects.onSessionCompleted()
    }

    /** Brings alarms and notifications back in line with the saved session, e.g. after a reboot. */
    fun restore() {
        completeIfEnded()
        activeSession()?.let(effects::onSessionStarted)
    }

    private fun updateSession(session: FocusSession) {
        store.saveSession(session)
        mutableSession.value = session
    }
}
