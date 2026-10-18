package app.justfocus.session

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionDurationTest {

    @Test
    fun `durations are kept within limits`() {
        assertEquals(SessionDuration.MIN_MINUTES, SessionDuration.coerceInLimits(0))
        assertEquals(30, SessionDuration.coerceInLimits(30))
        assertEquals(SessionDuration.MAX_MINUTES, SessionDuration.coerceInLimits(1_000))
    }

    @Test
    fun `presets and default are valid durations`() {
        (SessionDuration.PRESET_MINUTES + SessionDuration.DEFAULT_MINUTES).forEach { minutes ->
            assertEquals(minutes, SessionDuration.coerceInLimits(minutes))
        }
    }

    @Test
    fun `minutes convert to milliseconds`() {
        assertEquals(1_500_000L, SessionDuration.minutesToMillis(25))
    }
}
