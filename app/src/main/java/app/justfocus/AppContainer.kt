package app.justfocus

import android.content.Context
import app.justfocus.apps.AppAccessRepository
import app.justfocus.apps.InstalledAppsRepository
import app.justfocus.apps.SharedPreferencesAppAccessStore
import app.justfocus.apps.resolveSystemAppAccess
import app.justfocus.blocking.FocusGuard
import app.justfocus.notifications.SessionNotifications
import app.justfocus.permissions.PermissionChecker
import app.justfocus.session.AndroidSessionEffects
import app.justfocus.session.Clock
import app.justfocus.session.FocusSessionManager
import app.justfocus.session.SessionEndAlarm
import app.justfocus.session.SharedPreferencesSessionStore

/** Creates and holds the app's shared objects (simple manual dependency injection). */
class AppContainer(context: Context) {

    private val preferences = context.getSharedPreferences("just_focus", Context.MODE_PRIVATE)

    val clock: Clock = Clock.System

    val sessionNotifications = SessionNotifications(context)

    val sessionEndAlarm = SessionEndAlarm(context)

    val sessionManager = FocusSessionManager(
        store = SharedPreferencesSessionStore(preferences),
        effects = AndroidSessionEffects(sessionEndAlarm, sessionNotifications),
        clock = clock,
    )

    val appAccessRepository = AppAccessRepository(SharedPreferencesAppAccessStore(preferences))

    val installedAppsRepository = InstalledAppsRepository(context.packageManager, context.packageName)

    val focusGuard = FocusGuard(sessionManager, appAccessRepository, clock) {
        resolveSystemAppAccess(context)
    }

    val permissionChecker = PermissionChecker(context, sessionEndAlarm)
}

val Context.appContainer: AppContainer
    get() = (applicationContext as JustFocusApplication).container
