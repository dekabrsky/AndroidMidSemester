package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    onBack: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selected by viewModel.selectedTeamNames.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Команды России") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::clearSelection) {
                        Text("Сбросить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Выбрано команд: ${selected.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            when (val current = state) {
                TeamsUiState.Loading -> {
                    Text("Загрузка команд…")
                }

                is TeamsUiState.Error -> {
                    Text(current.message)
                    Button(onClick = viewModel::retry) {
                        Text("Повторить")
                    }
                }

                is TeamsUiState.Content -> {
                    LazyColumn {
                        items(current.teams, key = { it.id }) { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.toggleTeam(team.name)
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = team.name,
                                    modifier = Modifier.weight(1f)
                                )
                                Checkbox(
                                    checked = team.name in selected,
                                    onCheckedChange = {
                                        viewModel.toggleTeam(team.name)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}