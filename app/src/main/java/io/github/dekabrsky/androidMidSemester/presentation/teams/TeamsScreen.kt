
package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamsScreen(
    onBack: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()

    var search by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Назад")
            }

            Text(
                text = "Команды России",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Количество выбранных команд и кнопка сброса
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Выбрано команд: ${selectedIds.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = {
                    selectedIds.toList().forEach { teamId ->
                        viewModel.selectTeam(teamId, false)
                    }
                },
                enabled = selectedIds.isNotEmpty()
            ) {
                Text("Сбросить выбор")
            }
        }

        Text(
            text = "Если ничего не выбрано, показываются все матчи.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Поиск команды") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        when (val current = state) {
            TeamsUiState.Loading -> {
                CircularProgressIndicator()
            }

            is TeamsUiState.Error -> {
                Text(current.message)

                TextButton(onClick = viewModel::loadTeams) {
                    Text("Повторить")
                }
            }

            is TeamsUiState.Content -> {
                val visibleTeams = current.teams.filter {
                    it.name.contains(search, ignoreCase = true)
                }

                if (visibleTeams.isEmpty()) {
                    Text("Команды не найдены")
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = visibleTeams,
                        key = { it.id }
                    ) { team ->
                        val checked = team.id in selectedIds

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectTeam(
                                        team.id,
                                        !checked
                                    )
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = null
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(team.name)
                        }
                    }
                }
            }
        }
    }
}
