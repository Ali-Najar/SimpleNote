package com.example.simplenote.ui.note

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int?,              // null for new note
    onBack: () -> Unit
) {
    val vm: NoteEditorViewModel = viewModel()
    val ui by vm.ui.collectAsState()

    LaunchedEffect(noteId) { vm.load(noteId) }
    LaunchedEffect(ui.deleted) { if (ui.deleted) onBack() }   // leave after delete
    LaunchedEffect(ui.saved) { if (ui.saved) onBack() }       // leave after save

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ui.id == null) "New Note" else "Edit Note") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = { TextButton(onClick = vm::save, enabled = !ui.loading) { Text("Save") } }
            )
        },
        bottomBar = {
            if (ui.id != null) {
                BottomAppBar(actions = {}, floatingActionButton = {
                    FloatingActionButton(onClick = vm::delete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                })
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = ui.title,
                onValueChange = vm::onTitle,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = ui.description,
                onValueChange = vm::onDesc,
                label = { Text("Feel Free to Write Here…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 8
            )
            Spacer(Modifier.height(8.dp))
            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            ui.lastEdited?.let { Text("Last edited on $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}
