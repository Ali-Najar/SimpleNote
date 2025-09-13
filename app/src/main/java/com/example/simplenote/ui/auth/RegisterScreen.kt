// app/src/main/java/com/example/simplenote/ui/auth/RegisterScreen.kt
package com.example.simplenote.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api   // 👈 add this

@OptIn(ExperimentalMaterial3Api::class)                  // 👈 and this
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegistered: () -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val scroll = rememberScrollState()

    LaunchedEffect(ui.success) { if (ui.success) onRegistered() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = { TextButton(onClick = onBackToLogin) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .imePadding()
        ) {
            Text("And start taking notes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            fun firstError(key: String) = ui.fieldErrors[key]?.firstOrNull()

            OutlinedTextField(
                value = ui.firstName, onValueChange = vm::onFirst,
                label = { Text("First Name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("first_name") != null
            )
            firstError("first_name")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.lastName, onValueChange = vm::onLast,
                label = { Text("Last Name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("last_name") != null
            )
            firstError("last_name")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.username, onValueChange = vm::onUser,
                label = { Text("Username") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("username") != null
            )
            firstError("username")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.email, onValueChange = vm::onEmail,
                label = { Text("Email Address") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("email") != null
            )
            firstError("email")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.password, onValueChange = vm::onPass,
                label = { Text("Password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("password") != null
            )
            firstError("password")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.confirm, onValueChange = vm::onConfirm,
                label = { Text("Retype Password") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = firstError("confirm") != null
            )
            firstError("confirm")?.let {
                Spacer(Modifier.height(4.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.register { /* nav handled by LaunchedEffect */ } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                enabled = !ui.loading
            ) {
                Text(if (ui.loading) "Please wait…" else "Register")
            }

            ui.error?.let {
                Spacer(Modifier.height(12.dp)); Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
