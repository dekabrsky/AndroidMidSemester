package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.TeamLogo
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamsScreen(
    onBack: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TeamsTopBar(
                state = state,
                onBack = onBack,
                onClear = viewModel::clear,
                onDone = {
                    viewModel.save()
                    onBack()
                },
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
                is TeamsUiState.Error -> ErrorState(message = current.message)
                is TeamsUiState.Content -> LazyColumn {
                    items(current.teams, key = { it.id }) { team ->
                        TeamListItem(
                            team = team,
                            selected = team.id in current.selectedIds,
                            onClick = viewModel::toggle,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamsTopBar(
    state: TeamsUiState,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
) {
    val content = state as? TeamsUiState.Content

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
        Text(
            text = content?.let { "Выбрано: ${it.selectedIds.size}" } ?: "Команды",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        TextButton(
            onClick = onClear,
            enabled = content?.selectedIds?.isNotEmpty() == true,
        ) {
            Text("Сбросить")
        }
        TextButton(
            onClick = onDone,
            enabled = content != null,
        ) {
            Text("Готово")
        }
    }
}

@Composable
private fun TeamListItem(
    team: Team,
    selected: Boolean,
    onClick: (Team) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(team) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TeamLogo(
            logoUrl = team.logoUrl,
            teamName = team.name,
            size = 40.dp,
        )
        Text(
            text = team.name,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Checkbox(
            checked = selected,
            onCheckedChange = null,
        )
    }
}
