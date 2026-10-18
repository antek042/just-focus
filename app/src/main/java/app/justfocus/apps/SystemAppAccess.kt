package app.justfocus.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.TelecomManager
import android.view.inputmethod.InputMethodManager

/** System parts that must fully work, so calls (including 112), the keyboard and the status bar keep working. */
private val ESSENTIAL_SYSTEM_PACKAGES = setOf(
    "android",
    "com.android.systemui",
    "com.android.phone",
    "com.android.server.telecom",
    "com.android.emergency",
    "com.samsung.android.incallui",
)

/**
 * Access that the system decides, overriding the user's choices:
 * essential system apps work fully, and the home screen stays usable so you can leave Just Focus.
 */
fun resolveSystemAppAccess(context: Context): Map<String, AppAccess> = buildMap {
    homeScreenPackage(context)?.let { put(it, AppAccess.ALLOWED) }

    val essentialPackages = buildSet {
        add(context.packageName)
        addAll(ESSENTIAL_SYSTEM_PACKAGES)
        context.getSystemService(TelecomManager::class.java)?.defaultDialerPackage?.let(::add)
        context.getSystemService(InputMethodManager::class.java)
            ?.enabledInputMethodList
            ?.mapTo(this) { it.packageName }
    }
    essentialPackages.forEach { put(it, AppAccess.EMERGENCY) }
}

/** The launcher, which on most phones also shows the recent apps screen. */
private fun homeScreenPackage(context: Context): String? {
    val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
    return context.packageManager
        .resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo
        ?.packageName
}
