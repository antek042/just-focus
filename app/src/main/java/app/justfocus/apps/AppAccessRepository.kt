package app.justfocus.apps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppAccessRepository(private val store: AppAccessStore) {

    private val mutableAccessByPackage = MutableStateFlow(store.load())
    val accessByPackage: StateFlow<Map<String, AppAccess>> = mutableAccessByPackage.asStateFlow()

    fun setAccess(packageName: String, access: AppAccess) {
        val current = accessByPackage.value
        // Blocked is the default, so it is not stored.
        val updated = if (access == AppAccess.BLOCKED) {
            current - packageName
        } else {
            current + (packageName to access)
        }
        store.save(updated)
        mutableAccessByPackage.value = updated
    }
}
