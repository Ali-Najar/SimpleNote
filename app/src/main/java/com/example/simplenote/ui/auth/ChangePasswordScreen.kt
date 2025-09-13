// app/src/main/java/com/example/simplenote/ui/auth/ChangePasswordScreen.kt
package com.example.simplenote.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    val vm: ChangePasswordViewModel = viewModel()
    val ui by vm.ui.collectAsState()

    LaunchedEffect(ui.success) { if (ui.success) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create a New Password") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text("Your new password should be different from the previous password",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(ui.current, vm::onCurrent, label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(ui.new1, vm::onNew1, label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(ui.new2, vm::onNew2, label = { Text("Retype New Password") },
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Button(onClick = vm::submit, enabled = !ui.loading, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text(if (ui.loading) "Please wait…" else "Create Password")
            }
            ui.error?.let { Spacer(Modifier.height(12.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
