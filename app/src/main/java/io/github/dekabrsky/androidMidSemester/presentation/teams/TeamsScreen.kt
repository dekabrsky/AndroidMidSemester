package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamsScreen(
    onBackClick: () -> Unit,
    viewModel: TeamsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TeamsTopBar(
                onBackClick = onBackClick,
                onClearClick = viewModel::clearSelection,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val current = state) {
                TeamsUiState.Loading -> LoadingState()
                is TeamsUiState.Error -> ErrorState(message = current.message)
                is TeamsUiState.Content -> {
                    OutlinedTextField(
                        value = current.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        label = { Text("Поиск команды") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn {
                            items(current.teams, key = { it.id }) { team ->
                                TeamItem(
                                    team = team,
                                    isSelected = team.id in current.selectedIds,
                                    onToggle = { viewModel.toggleTeam(team.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamsTopBar(
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Назад",
            )
        }
        Text(
            text = "Любимые команды",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        TextButton(onClick = onClearClick) {
            Text("Сбросить")
        }
    }
}

@Composable
private fun TeamItem(
    team: Team,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ) {
            AsyncImage(
                model = team.logoUrl,
                contentDescription = team.name,
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp),
                placeholder = painterResource(R.drawable.ic_team_placeholder),
                error = painterResource(R.drawable.ic_team_placeholder),
            )
        }
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
        )
    }
}
