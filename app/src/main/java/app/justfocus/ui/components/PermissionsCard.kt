package app.justfocus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import app.justfocus.R
import app.justfocus.appContainer
import app.justfocus.permissions.FocusPermission

/** Missing permissions, re-checked every time the user comes back from the system settings. */
@Composable
fun rememberMissingPermissions(): List<FocusPermission> {
    val permissionChecker = LocalContext.current.appContainer.permissionChecker
    var missingPermissions by remember { mutableStateOf(permissionChecker.missingPermissions()) }
    LifecycleResumeEffect(permissionChecker) {
        missingPermissions = permissionChecker.missingPermissions()
        onPauseOrDispose {}
    }
    return missingPermissions
}

@Composable
fun PermissionsCard(missingPermissions: List<FocusPermission>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val permissionChecker = context.appContainer.permissionChecker

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.permissions_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            missingPermissions.forEach { permission ->
                PermissionRow(
                    permission = permission,
                    onGrant = { context.startActivity(permissionChecker.settingsIntent(permission)) },
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(permission: FocusPermission, onGrant: () -> Unit) {
    val requirementRes = if (permission.isRequired) R.string.permission_required else R.string.permission_recommended
    Column {
        Text(
            text = stringResource(permission.titleRes) + " · " + stringResource(requirementRes),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(permission.descriptionRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onGrant) {
            Text(stringResource(R.string.permission_grant))
        }
    }
}
