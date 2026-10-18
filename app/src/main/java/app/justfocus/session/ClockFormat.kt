package app.justfocus.session

/**
 * Formats a duration like a countdown clock: "25:00" or "1:05:00".
 * Partial seconds are rounded up, so the clock shows "00:01" until the very last moment.
 */
fun formatAsClock(millis: Long): String {
    val totalSeconds = (millis.coerceAtLeast(0) + 999) / 1000
    val hours = totalSeconds / 3600
    val minutes = totalSeconds % 3600 / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
