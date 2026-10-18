package app.justfocus.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.justfocus.R
import app.justfocus.ui.apps.AppListScreen
import app.justfocus.ui.components.ChoiceRow
import app.justfocus.ui.focus.FocusScreen

private enum class Tab(@StringRes val titleRes: Int) {
    FOCUS(R.string.tab_focus),
    APPS(R.string.tab_apps),
}

@Composable
fun JustFocusApp() {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.FOCUS) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            )
            ChoiceRow(
                options = Tab.entries,
                selected = selectedTab,
                label = { stringResource(it.titleRes) },
                onSelect = { selectedTab = it },
            )
            Spacer(Modifier.height(8.dp))
            when (selectedTab) {
                Tab.FOCUS -> FocusScreen()
                Tab.APPS -> AppListScreen()
            }
        }
    }
}
