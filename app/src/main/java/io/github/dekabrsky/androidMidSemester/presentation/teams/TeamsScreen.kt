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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

/**
 * Экран выбора команд. Отмеченные команды сохраняются сразу (без кнопки "Сохранить"),
 * а список матчей сам перезагрузится, когда пользователь вернётся назад.
 * Сначала видны 10 популярных клубов, полный список раскрывается кнопкой внизу,
 * а поиск всегда идёт по всем командам.
 */
@Composable
fun TeamsScreen(
    onBack: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedCount = (state as? TeamsUiState.Content)?.selectedIds?.size ?: 0

    Scaffold(
        topBar = {
            TeamsTopBar(
                selectedCount = selectedCount,
                onBack = onBack,
                onClear = viewModel::onClearSelection,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val current = state) {
                TeamsUiState.Loading -> LoadingState()
                is TeamsUiState.Error -> {
                    ErrorState(message = current.message)
                    TextButton(
                        onClick = viewModel::retry,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp),
                    ) {
                        Text("Повторить")
                    }
                }
                is TeamsUiState.Content -> TeamsContent(
                    state = current,
                    onQueryChange = viewModel::onQueryChange,
                    onTeamToggle = viewModel::onTeamToggle,
                    onToggleShowAll = viewModel::onToggleShowAll,
                )
            }
        }
    }
}

@Composable
private fun TeamsTopBar(
    selectedCount: Int,
    onBack: () -> Unit,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Назад",
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Команды России",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = if (selectedCount == 0) "Показываются все матчи" else "Выбрано: $selectedCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(
            onClick = onClear,
            enabled = selectedCount > 0,
        ) {
            Text("Сбросить")
        }
    }
}

@Composable
private fun TeamsContent(
    state: TeamsUiState.Content,
    onQueryChange: (String) -> Unit,
    onTeamToggle: (Long) -> Unit,
    onToggleShowAll: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            label = { Text("Поиск команды") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (state.teams.isEmpty()) {
            Text(
                text = "Ничего не найдено",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.teams, key = { it.id }) { team ->
                    TeamRow(
                        team = team,
                        checked = team.id in state.selectedIds,
                        onToggle = { onTeamToggle(team.id) },
                    )
                }
                if (state.canToggleShowAll) {
                    item(key = "show_all_toggle") {
                        TextButton(
                            onClick = onToggleShowAll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                if (state.showAll) {
                                    "Только популярные"
                                } else {
                                    "Показать все команды (${state.totalCount})"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamRow(
    team: Team,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Вся строка кликабельна и озвучивается как чекбокс.
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                onValueChange = { onToggle() },
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
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        )
        // onCheckedChange = null: клик обрабатывает строка целиком.
        Checkbox(
            checked = checked,
            onCheckedChange = null,
        )
    }
}
