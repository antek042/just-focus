package app.justfocus.apps

/**
 * Decides what each app may do during a focus session.
 *
 * @param accessByPackage the user's choices; apps not listed are blocked.
 * @param systemAccessByPackage access decided by the system, e.g. the phone dialer; it overrides the user's choices.
 */
class FocusRules(
    private val accessByPackage: Map<String, AppAccess>,
    private val systemAccessByPackage: Map<String, AppAccess>,
) {
    fun accessOf(packageName: String): AppAccess =
        systemAccessByPackage[packageName] ?: accessByPackage[packageName] ?: AppAccess.BLOCKED

    fun canOpen(packageName: String): Boolean = accessOf(packageName).canOpen

    fun canNotify(packageName: String): Boolean = accessOf(packageName).canNotify
}
