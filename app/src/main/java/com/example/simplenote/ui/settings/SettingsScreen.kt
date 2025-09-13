// app/src/main/java/com/example/simplenote/ui/settings/SettingsScreen.kt
package com.example.simplenote.ui.settings

import androidx.compose.foundation.clickable            // ✅ add this
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onLoggedOut: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    var askLogout by remember { mutableStateOf(false) }

    LaunchedEffect(ui.loggedOut) { if (ui.loggedOut) onLoggedOut() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (ui.loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            ui.user?.let { u ->
                Text(
                    text = if (!u.first_name.isNullOrBlank() || !u.last_name.isNullOrBlank())
                        "${u.first_name.orEmpty()} ${u.last_name.orEmpty()}"
                    else u.username,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(u.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(Modifier.padding(vertical = 16.dp))
            }

            Text("APP SETTINGS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Change Password") },
                leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingContent = { Text("›") },
                modifier = Modifier.fillMaxWidth().clickable(enabled = !ui.loading) { onChangePassword() }
            )
            Divider()

            Spacer(Modifier.height(16.dp))

            ListItem(
                headlineContent = { Text("Log Out", color = MaterialTheme.colorScheme.error) },
                leadingContent = { Icon(Icons.Outlined.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth().clickable { askLogout = true }
            )

            ui.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (askLogout) {
        AlertDialog(
            onDismissRequest = { askLogout = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out from the application?") },
            confirmButton = { TextButton(onClick = { askLogout = false; vm.logout() }) { Text("Yes") } },
            dismissButton = { TextButton(onClick = { askLogout = false }) { Text("Cancel") } }
        )
    }
}
