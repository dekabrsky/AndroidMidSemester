package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamsScreen(onBack: () -> Unit, viewModel: TeamsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedTeamIds.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Команды России", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onBack) { Text("Готово") }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск команды") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (selectedIds.isEmpty()) "Без выбора — все матчи" else "Выбрано: ${selectedIds.size}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = viewModel::clearSelection, enabled = selectedIds.isNotEmpty()) {
                    Text("Сбросить")
                }
            }
            when (val current = state) {
                TeamsUiState.Loading -> LoadingState()
                is TeamsUiState.Error -> ErrorState(current.message)
                is TeamsUiState.Content -> {
                    val teams = current.teams.filter { it.name.contains(query.trim(), ignoreCase = true) }
                    if (teams.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Команды не найдены")
                        }
                    } else {
                        LazyColumn {
                            items(teams, key = { it.id }) { team ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .toggleable(
                                            value = team.id in selectedIds,
                                            role = Role.Checkbox,
                                            onValueChange = { viewModel.toggleTeam(team.id) },
                                        )
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncImage(
                                        model = team.logoUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        placeholder = painterResource(R.drawable.ic_team_placeholder),
                                        error = painterResource(R.drawable.ic_team_placeholder),
                                    )
                                    Text(team.name, Modifier.weight(1f).padding(horizontal = 12.dp))
                                    Checkbox(checked = team.id in selectedIds, onCheckedChange = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
