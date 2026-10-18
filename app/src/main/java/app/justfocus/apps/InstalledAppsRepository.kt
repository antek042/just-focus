package app.justfocus.apps

import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(val packageName: String, val label: String)

class InstalledAppsRepository(
    private val packageManager: PackageManager,
    private val ownPackageName: String,
) {
    /** Apps that appear in the launcher, sorted by name. */
    suspend fun launchableApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        packageManager.queryIntentActivities(launcherIntent, 0)
            .map { InstalledApp(it.activityInfo.packageName, it.loadLabel(packageManager).toString()) }
            .filter { it.packageName != ownPackageName }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
