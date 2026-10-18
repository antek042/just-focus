package app.justfocus.session

import android.content.SharedPreferences
import androidx.core.content.edit

/** Persists the session so it survives the app process being killed. */
interface SessionStore {
    fun loadSession(): FocusSession
    fun saveSession(session: FocusSession)
    fun loadLastDurationMinutes(): Int?
    fun saveLastDurationMinutes(minutes: Int)
}

class SharedPreferencesSessionStore(private val preferences: SharedPreferences) : SessionStore {

    override fun loadSession(): FocusSession {
        val endsAt = preferences.getLong(KEY_ENDS_AT, NOT_SET)
        if (endsAt == NOT_SET) return FocusSession.Idle
        return FocusSession.Running(
            startedAtMillis = preferences.getLong(KEY_STARTED_AT, endsAt),
            endsAtMillis = endsAt,
        )
    }

    override fun saveSession(session: FocusSession) = preferences.edit {
        when (session) {
            FocusSession.Idle -> remove(KEY_STARTED_AT).remove(KEY_ENDS_AT)
            is FocusSession.Running -> putLong(KEY_STARTED_AT, session.startedAtMillis)
                .putLong(KEY_ENDS_AT, session.endsAtMillis)
        }
    }

    override fun loadLastDurationMinutes(): Int? =
        preferences.getInt(KEY_LAST_DURATION, 0).takeIf { it > 0 }

    override fun saveLastDurationMinutes(minutes: Int) = preferences.edit {
        putInt(KEY_LAST_DURATION, minutes)
    }

    private companion object {
        const val NOT_SET = -1L
        const val KEY_STARTED_AT = "session_started_at"
        const val KEY_ENDS_AT = "session_ends_at"
        const val KEY_LAST_DURATION = "last_duration_minutes"
    }
}
