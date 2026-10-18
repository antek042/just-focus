package app.justfocus.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import app.justfocus.R
import app.justfocus.session.FocusSession
import app.justfocus.ui.MainActivity

class SessionNotifications(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createChannels() {
        notificationManager.createNotificationChannels(
            listOf(
                channel(CHANNEL_ONGOING, R.string.channel_session_ongoing, NotificationManager.IMPORTANCE_LOW),
                channel(CHANNEL_COMPLETED, R.string.channel_session_completed, NotificationManager.IMPORTANCE_HIGH),
            ),
        )
    }

    /** A silent, persistent notification with a live countdown to the end of the session. */
    fun showSessionInProgress(session: FocusSession.Running) {
        val notification = baseNotification(CHANNEL_ONGOING)
            .setContentTitle(context.getString(R.string.notification_ongoing_title))
            .setContentText(context.getString(R.string.notification_ongoing_text))
            .setOngoing(true)
            .setSilent(true)
            .setWhen(session.endsAtMillis)
            .setShowWhen(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .build()
        notificationManager.notify(ID_ONGOING, notification)
    }

    fun showSessionCompleted() {
        cancelSessionInProgress()
        val notification = baseNotification(CHANNEL_COMPLETED)
            .setContentTitle(context.getString(R.string.notification_completed_title))
            .setContentText(context.getString(R.string.notification_completed_text))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(ID_COMPLETED, notification)
    }

    fun cancelSessionInProgress() = notificationManager.cancel(ID_ONGOING)

    private fun baseNotification(channelId: String) =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(context.getColor(R.color.purple))
            .setContentIntent(openAppIntent())

    private fun openAppIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        MainActivity.bringToFrontIntent(context),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun channel(id: String, @StringRes nameRes: Int, importance: Int) =
        NotificationChannel(id, context.getString(nameRes), importance)

    private companion object {
        const val CHANNEL_ONGOING = "session_ongoing"
        const val CHANNEL_COMPLETED = "session_completed"
        const val ID_ONGOING = 1
        const val ID_COMPLETED = 2
    }
}
