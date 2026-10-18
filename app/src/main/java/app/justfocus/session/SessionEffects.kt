package app.justfocus.session

/** Everything the outside world must do when the session changes (alarms, notifications). */
interface SessionEffects {
    fun onSessionStarted(session: FocusSession.Running)
    fun onSessionStopped()
    fun onSessionCompleted()
}
