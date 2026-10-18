package app.justfocus.apps

/** What an app may do during a focus session. */
enum class AppAccess {
    /** Opening it sends you back to Just Focus, its notifications wait until the session ends. */
    BLOCKED,

    /** Can be opened, its notifications wait until the session ends. */
    ALLOWED,

    /** Can be opened and its notifications come through, e.g. phone or messages from family. */
    EMERGENCY;

    val canOpen: Boolean get() = this != BLOCKED
    val canNotify: Boolean get() = this == EMERGENCY
}
