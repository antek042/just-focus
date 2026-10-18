package app.justfocus.apps

import org.junit.Assert.assertEquals
import org.junit.Test

class AppAccessCodecTest {

    @Test
    fun `choices survive encoding and decoding`() {
        val accessByPackage = mapOf("com.notes" to AppAccess.ALLOWED, "com.phone" to AppAccess.EMERGENCY)

        assertEquals(accessByPackage, AppAccessCodec.decode(AppAccessCodec.encode(accessByPackage)))
    }

    @Test
    fun `unknown or broken entries are skipped`() {
        val decoded = AppAccessCodec.decode(setOf("com.notes=ALLOWED", "com.old=REMOVED_MODE", "garbage"))

        assertEquals(mapOf("com.notes" to AppAccess.ALLOWED), decoded)
    }
}
