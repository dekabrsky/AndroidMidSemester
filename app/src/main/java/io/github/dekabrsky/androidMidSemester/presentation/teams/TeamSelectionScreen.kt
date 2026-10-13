package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamSelectionScreen(onBack: () -> Unit, viewModel: TeamSelectionViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selected by viewModel.selectedIds.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("Назад") }
            Text("Российские команды", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { viewModel.save(); onBack() }) { Text("Готово") }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Text("Выбрано: ${selected.size}. Если ничего не выбрано, показываются все матчи.", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.bodySmall)
            when (val current = state) {
                TeamsUiState.Loading -> LoadingState()
                is TeamsUiState.Error -> ErrorState(current.message)
                is TeamsUiState.Content -> LazyColumn {
                    items(current.teams, key = Team::id) { team ->
                        TeamRow(team, team.id in selected) { viewModel.toggle(team.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamRow(team: Team, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = team.logoUrl, contentDescription = null, modifier = Modifier.size(40.dp))
        Text(team.name, Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Checkbox(checked = selected, onCheckedChange = { onClick() })
    }
}
