// app/src/main/java/com/example/simplenote/ui/auth/LoginScreen.kt
package com.example.simplenote.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    onBack: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value

    if (ui.success) onLoggedIn()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Let’s Login", style = MaterialTheme.typography.headlineLarge)
        Text("And notes your idea", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = ui.username, onValueChange = vm::onUser,
            label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = ui.password, onValueChange = vm::onPass,
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = vm::login,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(if (ui.loading) "Loading…" else "Login")
        }

        ui.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            HorizontalDivider(Modifier.weight(1f))
            Text("  Or  ")
            HorizontalDivider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Don’t have any account? Register here",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().clickable { onRegister() },
            textAlign = TextAlign.Center
        )
    }
}
