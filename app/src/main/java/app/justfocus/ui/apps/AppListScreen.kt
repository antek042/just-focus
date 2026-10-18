package app.justfocus.ui.apps

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.justfocus.R
import app.justfocus.apps.AppAccess
import app.justfocus.ui.components.AppIcon
import app.justfocus.ui.components.ChoiceRow

@get:StringRes
private val AppAccess.labelRes: Int
    get() = when (this) {
        AppAccess.BLOCKED -> R.string.access_blocked
        AppAccess.ALLOWED -> R.string.access_allowed
        AppAccess.EMERGENCY -> R.string.access_emergency
    }

@Composable
fun AppListScreen(viewModel: AppListViewModel = viewModel(factory = AppListViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            val explanationRes = if (uiState.isLocked) R.string.apps_locked_during_session else R.string.apps_explanation
            Text(
                text = stringResource(explanationRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        items(uiState.apps, key = { it.app.packageName }) { item ->
            AppAccessCard(
                item = item,
                enabled = !uiState.isLocked,
                onAccessChange = { viewModel.setAccess(item.app.packageName, it) },
            )
        }
    }
}

@Composable
private fun AppAccessCard(item: AppWithAccess, enabled: Boolean, onAccessChange: (AppAccess) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppIcon(item.app.packageName)
                Text(item.app.label, style = MaterialTheme.typography.bodyLarge)
            }
            ChoiceRow(
                options = AppAccess.entries,
                selected = item.access,
                label = { stringResource(it.labelRes) },
                onSelect = onAccessChange,
                enabled = enabled,
            )
        }
    }
}
