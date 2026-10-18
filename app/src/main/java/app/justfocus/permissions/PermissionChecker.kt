package app.justfocus.permissions

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import app.justfocus.blocking.AppBlockerService
import app.justfocus.session.SessionEndAlarm

class PermissionChecker(
    private val context: Context,
    private val sessionEndAlarm: SessionEndAlarm,
) {
    fun missingPermissions(): List<FocusPermission> = FocusPermission.entries.filterNot(::isGranted)

    fun isGranted(permission: FocusPermission): Boolean = when (permission) {
        FocusPermission.APP_BLOCKING -> isAppBlockerEnabled()
        FocusPermission.NOTIFICATION_FILTERING ->
            context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)
        FocusPermission.NOTIFICATIONS -> NotificationManagerCompat.from(context).areNotificationsEnabled()
        FocusPermission.EXACT_ALARM -> sessionEndAlarm.canScheduleExactAlarms()
    }

    /** The system settings screen where the user can grant the permission. */
    // EXACT_ALARM is only ever missing on Android 12+, where its settings screen exists.
    @SuppressLint("InlinedApi")
    fun settingsIntent(permission: FocusPermission): Intent = when (permission) {
        FocusPermission.APP_BLOCKING -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        FocusPermission.NOTIFICATION_FILTERING -> Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        FocusPermission.NOTIFICATIONS -> Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        FocusPermission.EXACT_ALARM -> Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            "package:${context.packageName}".toUri(),
        )
    }

    private fun isAppBlockerEnabled(): Boolean {
        val appBlocker = ComponentName(context, AppBlockerService::class.java)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ).orEmpty()
        return enabledServices.split(':').any { ComponentName.unflattenFromString(it) == appBlocker }
    }
}
