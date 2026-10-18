package app.justfocus.apps

/**
 * Decides what each app may do during a focus session.
 *
 * @param accessByPackage the user's choices; apps not listed are blocked.
 * @param alwaysAllowedPackages system apps that must keep working, e.g. the phone dialer.
 */
class FocusRules(
    private val accessByPackage: Map<String, AppAccess>,
    private val alwaysAllowedPackages: Set<String>,
) {
    fun accessOf(packageName: String): AppAccess =
        if (packageName in alwaysAllowedPackages) {
            AppAccess.EMERGENCY
        } else {
            accessByPackage[packageName] ?: AppAccess.BLOCKED
        }

    fun canOpen(packageName: String): Boolean = accessOf(packageName).canOpen

    fun canNotify(packageName: String): Boolean = accessOf(packageName).canNotify
}
