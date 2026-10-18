package app.justfocus.apps

import app.justfocus.InMemoryAppAccessStore
import org.junit.Assert.assertEquals
import org.junit.Test

class AppAccessRepositoryTest {

    private val store = InMemoryAppAccessStore(saved = mapOf("com.notes" to AppAccess.ALLOWED))
    private val repository = AppAccessRepository(store)

    @Test
    fun `starts with the saved choices`() {
        assertEquals(mapOf("com.notes" to AppAccess.ALLOWED), repository.accessByPackage.value)
    }

    @Test
    fun `setting access updates and saves the choices`() {
        repository.setAccess("com.family.chat", AppAccess.EMERGENCY)

        val expected = mapOf("com.notes" to AppAccess.ALLOWED, "com.family.chat" to AppAccess.EMERGENCY)
        assertEquals(expected, repository.accessByPackage.value)
        assertEquals(expected, store.saved)
    }

    @Test
    fun `blocking an app removes it, because blocked is the default`() {
        repository.setAccess("com.notes", AppAccess.BLOCKED)

        assertEquals(emptyMap<String, AppAccess>(), repository.accessByPackage.value)
        assertEquals(emptyMap<String, AppAccess>(), store.saved)
    }
}
