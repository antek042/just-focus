package app.justfocus.session

import app.justfocus.notifications.SessionNotifications

class AndroidSessionEffects(
    private val alarm: SessionEndAlarm,
    private val notifications: SessionNotifications,
) : SessionEffects {

    override fun onSessionStarted(session: FocusSession.Running) {
        alarm.schedule(session.endsAtMillis)
        notifications.showSessionInProgress(session)
    }

    override fun onSessionStopped() {
        alarm.cancel()
        notifications.cancelSessionInProgress()
    }

    override fun onSessionCompleted() {
        alarm.cancel()
        notifications.showSessionCompleted()
    }
}
