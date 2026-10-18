package app.justfocus.ui.focus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.justfocus.R
import app.justfocus.apps.InstalledApp
import app.justfocus.session.SessionDuration
import app.justfocus.session.formatAsClock
import app.justfocus.ui.components.AppIcon
import app.justfocus.ui.components.PermissionsCard
import app.justfocus.ui.components.TimerDial
import app.justfocus.ui.components.rememberMissingPermissions

private val WideButtonShape = RoundedCornerShape(16.dp)

@Composable
fun FocusScreen(viewModel: FocusViewModel = viewModel(factory = FocusViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        when (val state = uiState) {
            is FocusUiState.Setup -> SessionSetup(
                durationMinutes = state.durationMinutes,
                onSelectDuration = viewModel::selectDuration,
                onIncrease = viewModel::increaseDuration,
                onDecrease = viewModel::decreaseDuration,
                onStart = viewModel::startSession,
            )
            is FocusUiState.Focusing -> SessionInProgress(
                state = state,
                onStop = viewModel::stopSession,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SessionSetup(
    durationMinutes: Int,
    onSelectDuration: (Int) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onStart: () -> Unit,
) {
    val missingPermissions = rememberMissingPermissions()
    val canStart = missingPermissions.none { it.isRequired }

    if (missingPermissions.isNotEmpty()) {
        PermissionsCard(missingPermissions)
    }

    TimerDial(
        timeText = formatAsClock(SessionDuration.minutesToMillis(durationMinutes)),
        remainingFraction = 1f,
    )

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        FilledTonalButton(onClick = onDecrease, enabled = durationMinutes > SessionDuration.MIN_MINUTES) {
            Text(stringResource(R.string.duration_decrease, SessionDuration.STEP_MINUTES))
        }
        FilledTonalButton(onClick = onIncrease, enabled = durationMinutes < SessionDuration.MAX_MINUTES) {
            Text(stringResource(R.string.duration_increase, SessionDuration.STEP_MINUTES))
        }
    }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
        SessionDuration.PRESET_MINUTES.forEach { minutes ->
            FilterChip(
                selected = minutes == durationMinutes,
                onClick = { onSelectDuration(minutes) },
                label = { Text(stringResource(R.string.duration_minutes, minutes)) },
            )
        }
    }

    Button(
        onClick = onStart,
        enabled = canStart,
        shape = WideButtonShape,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Text(stringResource(R.string.session_start), style = MaterialTheme.typography.titleMedium)
    }

    if (!canStart) {
        HintText(stringResource(R.string.session_start_needs_permissions))
    }
}

@Composable
private fun SessionInProgress(state: FocusUiState.Focusing, onStop: () -> Unit) {
    Spacer(Modifier.height(8.dp))
    TimerDial(timeText = formatAsClock(state.remainingMillis), remainingFraction = state.remainingFraction)
    HintText(stringResource(R.string.session_in_progress_hint))

    if (state.allowedApps.isNotEmpty()) {
        AllowedAppsLauncher(state.allowedApps)
    }

    OutlinedButton(
        onClick = onStop,
        shape = WideButtonShape,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Text(stringResource(R.string.session_stop), style = MaterialTheme.typography.titleMedium)
    }
}

/** Shortcuts to the allowed apps, so they are one tap away during a session. */
@Composable
private fun AllowedAppsLauncher(apps: List<InstalledApp>) {
    val context = LocalContext.current

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.allowed_apps_header),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(apps, key = { it.packageName }) { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(64.dp)
                        .clickable {
                            context.packageManager.getLaunchIntentForPackage(app.packageName)
                                ?.let(context::startActivity)
                        },
                ) {
                    AppIcon(app.packageName, size = 48.dp)
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun HintText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
