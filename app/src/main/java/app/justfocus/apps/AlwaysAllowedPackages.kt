package app.justfocus.apps

import android.content.Context
import android.telecom.TelecomManager
import android.view.inputmethod.InputMethodManager

/** System parts that are never blocked, so calls (including 112), the keyboard and the status bar keep working. */
private val SYSTEM_PACKAGES = setOf(
    "android",
    "com.android.systemui",
    "com.android.phone",
    "com.android.server.telecom",
    "com.android.emergency",
    "com.samsung.android.incallui",
)

fun resolveAlwaysAllowedPackages(context: Context): Set<String> = buildSet {
    add(context.packageName)
    addAll(SYSTEM_PACKAGES)
    context.getSystemService(TelecomManager::class.java)?.defaultDialerPackage?.let(::add)
    context.getSystemService(InputMethodManager::class.java)
        ?.enabledInputMethodList
        ?.mapTo(this) { it.packageName }
}
