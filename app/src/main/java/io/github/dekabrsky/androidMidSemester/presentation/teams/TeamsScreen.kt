package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamsScreen(
    onBack: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedTeamIds.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Назад")
                }
                Text(
                    text = "Команды России",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                TextButton(
                    onClick = viewModel::clearSelection,
                    enabled = selectedIds.isNotEmpty(),
                ) {
                    Text("Сбросить")
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (selectedIds.isEmpty()) {
                        "Без выбора показываются матчи всех команд из списка."
                    } else {
                        "Показываются матчи выбранных команд."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state is TeamsUiState.Content,
                ) {
                    Text("Показать матчи")
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск команды") },
                singleLine = true,
                enabled = state is TeamsUiState.Content,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            val total = (state as? TeamsUiState.Content)?.teams?.size
            Text(
                text = if (total != null) {
                    "Выбрано: ${selectedIds.size} из $total"
                } else {
                    "Выбрано: ${selectedIds.size}"
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val current = state) {
                    TeamsUiState.Loading -> LoadingState()
                    is TeamsUiState.Error -> Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Не удалось загрузить команды", style = MaterialTheme.typography.titleMedium)
                        Text(
                            current.message,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                        Button(onClick = viewModel::load) { Text("Повторить") }
                    }
                    is TeamsUiState.Content -> {
                        val teams = current.teams.filter {
                            it.name.contains(query.trim(), ignoreCase = true)
                        }
                        if (teams.isEmpty()) {
                            Text(
                                text = "Команды не найдены",
                                modifier = Modifier.align(Alignment.Center),
                            )
                        } else {
                            LazyColumn {
                                items(teams, key = { it.id }) { team ->
                                    TeamSelectionItem(
                                        team = team,
                                        selected = team.id in selectedIds,
                                        onToggle = { viewModel.toggleTeam(team.id) },
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamSelectionItem(team: Team, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
            modifier = Modifier.size(48.dp),
        )
        Text(team.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Checkbox(checked = selected, onCheckedChange = null)
    }
}
