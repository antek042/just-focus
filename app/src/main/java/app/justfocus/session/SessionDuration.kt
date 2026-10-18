package app.justfocus.session

object SessionDuration {
    const val MIN_MINUTES = 5
    const val MAX_MINUTES = 240
    const val STEP_MINUTES = 5
    const val DEFAULT_MINUTES = 25
    val PRESET_MINUTES = listOf(15, 25, 45, 60, 90)

    fun coerceInLimits(minutes: Int): Int = minutes.coerceIn(MIN_MINUTES, MAX_MINUTES)

    fun minutesToMillis(minutes: Int): Long = minutes * 60_000L
}
