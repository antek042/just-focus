package app.justfocus.session

sealed interface FocusSession {

    data object Idle : FocusSession

    data class Running(val startedAtMillis: Long, val endsAtMillis: Long) : FocusSession {
        val totalMillis: Long get() = endsAtMillis - startedAtMillis

        fun remainingMillis(nowMillis: Long): Long = (endsAtMillis - nowMillis).coerceAtLeast(0)

        fun hasEnded(nowMillis: Long): Boolean = nowMillis >= endsAtMillis

        /** Fraction of the session still left: 1.0 at the start, 0.0 at the end. */
        fun remainingFraction(nowMillis: Long): Float =
            remainingMillis(nowMillis).toFloat() / totalMillis
    }
}
