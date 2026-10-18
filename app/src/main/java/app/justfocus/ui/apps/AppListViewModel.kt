package app.justfocus.ui.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.justfocus.JustFocusApplication
import app.justfocus.apps.AppAccess
import app.justfocus.apps.AppAccessRepository
import app.justfocus.apps.InstalledApp
import app.justfocus.apps.InstalledAppsRepository
import app.justfocus.session.FocusSession
import app.justfocus.session.FocusSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppWithAccess(val app: InstalledApp, val access: AppAccess)

data class AppListUiState(
    val isLoading: Boolean = true,
    val apps: List<AppWithAccess> = emptyList(),
    /** Choices are locked during a session, so they cannot be changed to sneak into a blocked app. */
    val isLocked: Boolean = false,
)

class AppListViewModel(
    private val appAccessRepository: AppAccessRepository,
    private val sessionManager: FocusSessionManager,
    installedAppsRepository: InstalledAppsRepository,
) : ViewModel() {

    private val installedApps = MutableStateFlow<List<InstalledApp>?>(null)

    val uiState: StateFlow<AppListUiState> = combine(
        installedApps,
        appAccessRepository.accessByPackage,
        sessionManager.session,
    ) { apps, accessByPackage, session ->
        AppListUiState(
            isLoading = apps == null,
            apps = apps.orEmpty().map { AppWithAccess(it, accessByPackage[it.packageName] ?: AppAccess.BLOCKED) },
            isLocked = session is FocusSession.Running,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppListUiState())

    init {
        viewModelScope.launch { installedApps.value = installedAppsRepository.launchableApps() }
    }

    fun setAccess(packageName: String, access: AppAccess) {
        if (sessionManager.isFocusActive()) return
        appAccessRepository.setAccess(packageName, access)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as JustFocusApplication).container
                AppListViewModel(
                    appAccessRepository = container.appAccessRepository,
                    sessionManager = container.sessionManager,
                    installedAppsRepository = container.installedAppsRepository,
                )
            }
        }
    }
}
