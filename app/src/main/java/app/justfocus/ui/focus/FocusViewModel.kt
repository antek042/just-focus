package app.justfocus.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.justfocus.JustFocusApplication
import app.justfocus.apps.AppAccessRepository
import app.justfocus.apps.InstalledApp
import app.justfocus.apps.InstalledAppsRepository
import app.justfocus.session.Clock
import app.justfocus.session.FocusSession
import app.justfocus.session.FocusSessionManager
import app.justfocus.session.SessionDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface FocusUiState {

    /** No session yet: the user picks how long to focus. */
    data class Setup(val durationMinutes: Int) : FocusUiState

    data class Focusing(
        val remainingMillis: Long,
        val remainingFraction: Float,
        val allowedApps: List<InstalledApp>,
    ) : FocusUiState
}

class FocusViewModel(
    private val sessionManager: FocusSessionManager,
    appAccessRepository: AppAccessRepository,
    installedAppsRepository: InstalledAppsRepository,
    clock: Clock,
) : ViewModel() {

    private val selectedMinutes = MutableStateFlow(sessionManager.lastDurationMinutes)
    private val installedApps = MutableStateFlow(emptyList<InstalledApp>())

    private val currentTimeEverySecond = flow {
        while (true) {
            emit(clock.nowMillis())
            delay(ONE_SECOND_MILLIS)
        }
    }.onEach { sessionManager.completeIfEnded() }

    val uiState: StateFlow<FocusUiState> = combine(
        sessionManager.session,
        selectedMinutes,
        currentTimeEverySecond,
        appAccessRepository.accessByPackage,
        installedApps,
    ) { session, minutes, now, accessByPackage, apps ->
        when (session) {
            FocusSession.Idle -> FocusUiState.Setup(minutes)
            is FocusSession.Running -> FocusUiState.Focusing(
                remainingMillis = session.remainingMillis(now),
                remainingFraction = session.remainingFraction(now),
                allowedApps = apps.filter { accessByPackage[it.packageName]?.canOpen == true },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FocusUiState.Setup(selectedMinutes.value))

    init {
        viewModelScope.launch { installedApps.value = installedAppsRepository.launchableApps() }
    }

    fun selectDuration(minutes: Int) {
        selectedMinutes.value = SessionDuration.coerceInLimits(minutes)
    }

    fun increaseDuration() = selectDuration(selectedMinutes.value + SessionDuration.STEP_MINUTES)

    fun decreaseDuration() = selectDuration(selectedMinutes.value - SessionDuration.STEP_MINUTES)

    fun startSession() = sessionManager.start(selectedMinutes.value)

    fun stopSession() = sessionManager.stop()

    companion object {
        private const val ONE_SECOND_MILLIS = 1_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as JustFocusApplication).container
                FocusViewModel(
                    sessionManager = container.sessionManager,
                    appAccessRepository = container.appAccessRepository,
                    installedAppsRepository = container.installedAppsRepository,
                    clock = container.clock,
                )
            }
        }
    }
}
