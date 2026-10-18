package app.justfocus.blocking

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import app.justfocus.appContainer
import app.justfocus.ui.MainActivity

/** Watches which app comes to the foreground and sends the user back to Just Focus if it is blocked. */
class AppBlockerService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (appContainer.focusGuard.shouldBlockApp(packageName)) {
            startActivity(MainActivity.bringToFrontIntent(this))
        }
    }

    override fun onInterrupt() = Unit
}
