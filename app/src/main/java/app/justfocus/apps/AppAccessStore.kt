package app.justfocus.apps

import android.content.SharedPreferences
import androidx.core.content.edit

interface AppAccessStore {
    fun load(): Map<String, AppAccess>
    fun save(accessByPackage: Map<String, AppAccess>)
}

class SharedPreferencesAppAccessStore(private val preferences: SharedPreferences) : AppAccessStore {

    override fun load(): Map<String, AppAccess> =
        AppAccessCodec.decode(preferences.getStringSet(KEY_APP_ACCESS, emptySet()).orEmpty())

    override fun save(accessByPackage: Map<String, AppAccess>) = preferences.edit {
        putStringSet(KEY_APP_ACCESS, AppAccessCodec.encode(accessByPackage))
    }

    private companion object {
        const val KEY_APP_ACCESS = "app_access"
    }
}

/** Stores each app's access as a "package=ACCESS" string, because preferences hold sets of strings. */
object AppAccessCodec {
    private const val SEPARATOR = "="

    fun encode(accessByPackage: Map<String, AppAccess>): Set<String> =
        accessByPackage.map { (packageName, access) -> "$packageName$SEPARATOR${access.name}" }.toSet()

    fun decode(entries: Set<String>): Map<String, AppAccess> =
        entries.mapNotNull { entry ->
            val packageName = entry.substringBefore(SEPARATOR)
            val access = AppAccess.entries.find { it.name == entry.substringAfter(SEPARATOR) }
            access?.let { packageName to it }
        }.toMap()
}
