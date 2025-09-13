package com.example.simplenote.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.simplenote.data.remote.dto.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onCreateNote: () -> Unit,
    onOpenNote: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onHome: () -> Unit,
    vm: HomeViewModel = viewModel(),
    refreshSignal: Boolean = false,
    onRefreshConsumed: () -> Unit = {}
) {
    val ui by vm.ui.collectAsState()
    val gridState = rememberLazyGridState()

    // 1) Refresh once when first shown
    LaunchedEffect(Unit) { vm.refresh() }

    // 2) Refresh when editor asks us to (via nav state)
    LaunchedEffect(refreshSignal) { if (refreshSignal) { vm.refresh(); onRefreshConsumed() } }

    // 3) Refresh when we return to this screen (onResume)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) vm.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    // Auto-load more when near the end
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { info ->
                val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                val total = info.totalItemsCount
                last >= total - 4 && total > 0
            }
            .distinctUntilChanged()
            .filter { it }
            .collectLatest { vm.loadMore() }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = onHome, // make home button actually navigate
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search
            OutlinedTextField(
                value = ui.query,
                onValueChange = vm::onQuery,
                placeholder = { Text("Search…") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (ui.notes.isEmpty() && ui.query.isBlank() && !ui.loading) {
                EmptyState(onCreateNote)
            } else {
                NotesGrid(
                    notes = ui.notes,
                    onOpenNote = onOpenNote,
                    loading = ui.loading,
                    endReached = ui.endReached,
                    gridState = gridState
                )
            }

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EmptyState(onCreateNote: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Start Your Journey", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Every big step starts with a small one.\nCreate your first idea and start your journey!",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        FilledTonalButton(onClick = onCreateNote) { Text("Create a note") }
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    onOpenNote: (Int) -> Unit,
    loading: Boolean,
    endReached: Boolean,
    gridState: LazyGridState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes, key = { it.id }) { n ->
            ElevatedCard(onClick = { onOpenNote(n.id) }) {
                Column(Modifier.padding(12.dp)) {
                    Text(n.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(n.description, style = MaterialTheme.typography.bodySmall, maxLines = 5)
                }
            }
        }
        if (!endReached || loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (loading) CircularProgressIndicator()
                }
            }
        }
    }
}
