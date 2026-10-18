package app.justfocus.blocking

import app.justfocus.apps.AppAccessRepository
import app.justfocus.apps.FocusRules
import app.justfocus.session.Clock
import app.justfocus.session.FocusSessionManager

/** Answers the blocking services' questions: may this app open, and should this notification wait? */
class FocusGuard(
    private val sessionManager: FocusSessionManager,
    private val appAccessRepository: AppAccessRepository,
    private val clock: Clock,
    private val alwaysAllowedPackages: () -> Set<String>,
) {
    fun shouldBlockApp(packageName: String): Boolean =
        sessionManager.isFocusActive() && !currentRules().canOpen(packageName)

    /** How long to hold back a notification from this app, or null if it may be shown now. */
    fun notificationSnoozeMillis(packageName: String): Long? {
        val session = sessionManager.activeSession() ?: return null
        if (currentRules().canNotify(packageName)) return null
        return session.remainingMillis(clock.nowMillis())
    }

    private fun currentRules() =
        FocusRules(appAccessRepository.accessByPackage.value, alwaysAllowedPackages())
}
