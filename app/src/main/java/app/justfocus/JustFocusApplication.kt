package app.justfocus

import android.app.Application

class JustFocusApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.sessionNotifications.createChannels()
        container.sessionManager.restore()
    }
}
