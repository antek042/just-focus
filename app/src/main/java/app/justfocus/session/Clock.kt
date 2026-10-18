package app.justfocus.session

/** Source of the current time, replaceable in tests. */
fun interface Clock {
    fun nowMillis(): Long

    companion object {
        val System = Clock { java.lang.System.currentTimeMillis() }
    }
}
