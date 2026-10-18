package app.justfocus.session

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockFormatTest {

    @Test
    fun `formats minutes and seconds`() {
        assertEquals("25:00", formatAsClock(25 * 60_000L))
        assertEquals("04:05", formatAsClock(245_000L))
    }

    @Test
    fun `adds hours only when needed`() {
        assertEquals("1:05:00", formatAsClock(65 * 60_000L))
    }

    @Test
    fun `rounds partial seconds up so the clock never shows zero too early`() {
        assertEquals("00:01", formatAsClock(1L))
        assertEquals("25:00", formatAsClock(25 * 60_000L - 400))
    }

    @Test
    fun `negative time shows zero`() {
        assertEquals("00:00", formatAsClock(-5_000L))
    }
}
