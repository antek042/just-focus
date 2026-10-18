package app.justfocus

import app.justfocus.apps.AppAccess
import app.justfocus.apps.AppAccessStore
import app.justfocus.session.Clock
import app.justfocus.session.FocusSession
import app.justfocus.session.SessionEffects
import app.justfocus.session.SessionStore

class FakeClock(var nowMillis: Long = 1_000_000L) : Clock {
    override fun nowMillis(): Long = nowMillis

    fun advanceMinutes(minutes: Int) {
        nowMillis += minutes * 60_000L
    }
}

class InMemorySessionStore(
    var session: FocusSession = FocusSession.Idle,
    var lastDurationMinutes: Int? = null,
) : SessionStore {
    override fun loadSession() = session
    override fun saveSession(session: FocusSession) {
        this.session = session
    }
    override fun loadLastDurationMinutes() = lastDurationMinutes
    override fun saveLastDurationMinutes(minutes: Int) {
        lastDurationMinutes = minutes
    }
}

class RecordingSessionEffects : SessionEffects {
    val events = mutableListOf<String>()

    override fun onSessionStarted(session: FocusSession.Running) {
        events += "started until ${session.endsAtMillis}"
    }
    override fun onSessionStopped() {
        events += "stopped"
    }
    override fun onSessionCompleted() {
        events += "completed"
    }
}

class InMemoryAppAccessStore(var saved: Map<String, AppAccess> = emptyMap()) : AppAccessStore {
    override fun load() = saved
    override fun save(accessByPackage: Map<String, AppAccess>) {
        saved = accessByPackage
    }
}
