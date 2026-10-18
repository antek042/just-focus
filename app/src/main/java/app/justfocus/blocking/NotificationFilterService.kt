package app.justfocus.blocking

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import app.justfocus.appContainer
import app.justfocus.session.FocusSession
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

/**
 * Holds back (snoozes) notifications from non-emergency apps until the session ends,
 * so nothing is lost – it simply shows up later.
 */
class NotificationFilterService : NotificationListenerService() {

    private val scope = MainScope()

    override fun onListenerConnected() {
        scope.launch {
            appContainer.sessionManager.session.collect { session ->
                if (session is FocusSession.Running) snoozeDistractingNotifications(activeNotifications)
            }
        }
    }

    override fun onListenerDisconnected() = scope.coroutineContext.cancelChildren()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(notification: StatusBarNotification) =
        snoozeDistractingNotifications(arrayOf(notification))

    private fun snoozeDistractingNotifications(notifications: Array<StatusBarNotification>?) {
        notifications.orEmpty()
            // Ongoing notifications (calls, music, timers) cannot be snoozed.
            .filterNot { it.isOngoing }
            .forEach { notification ->
                val snoozeMillis = appContainer.focusGuard.notificationSnoozeMillis(notification.packageName)
                if (snoozeMillis != null) snoozeNotification(notification.key, snoozeMillis)
            }
    }
}
