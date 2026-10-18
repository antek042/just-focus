package app.justfocus.permissions

import androidx.annotation.StringRes
import app.justfocus.R

enum class FocusPermission(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    /** Required permissions must be granted before a session can start. */
    val isRequired: Boolean,
) {
    APP_BLOCKING(R.string.permission_app_blocking_title, R.string.permission_app_blocking_description, true),
    NOTIFICATION_FILTERING(R.string.permission_notification_filtering_title, R.string.permission_notification_filtering_description, true),
    NOTIFICATIONS(R.string.permission_notifications_title, R.string.permission_notifications_description, false),
    EXACT_ALARM(R.string.permission_exact_alarm_title, R.string.permission_exact_alarm_description, false),
}
