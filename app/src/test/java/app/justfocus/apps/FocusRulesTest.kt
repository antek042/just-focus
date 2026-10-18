package app.justfocus.apps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusRulesTest {

    private val rules = FocusRules(
        accessByPackage = mapOf(
            "com.notes" to AppAccess.ALLOWED,
            "com.family.chat" to AppAccess.EMERGENCY,
        ),
        alwaysAllowedPackages = setOf("com.phone"),
    )

    @Test
    fun `apps without a choice are blocked`() {
        assertEquals(AppAccess.BLOCKED, rules.accessOf("com.game"))
        assertFalse(rules.canOpen("com.game"))
        assertFalse(rules.canNotify("com.game"))
    }

    @Test
    fun `allowed apps can be opened but stay silent`() {
        assertTrue(rules.canOpen("com.notes"))
        assertFalse(rules.canNotify("com.notes"))
    }

    @Test
    fun `emergency apps can be opened and can notify`() {
        assertTrue(rules.canOpen("com.family.chat"))
        assertTrue(rules.canNotify("com.family.chat"))
    }

    @Test
    fun `always allowed system apps win over the user's choice`() {
        val rulesWithBlockedPhone = FocusRules(
            accessByPackage = mapOf("com.phone" to AppAccess.BLOCKED),
            alwaysAllowedPackages = setOf("com.phone"),
        )

        assertTrue(rulesWithBlockedPhone.canOpen("com.phone"))
        assertTrue(rulesWithBlockedPhone.canNotify("com.phone"))
    }
}
