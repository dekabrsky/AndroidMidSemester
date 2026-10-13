package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TITLE_DATE_FORMAT =
    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))

@Composable
fun MatchesScreen(
    onMatchClick: (Match) -> Unit,
    onTeamsClick: () -> Unit,
    viewModel: MatchesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedTeamIds by viewModel.selectedTeamIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MatchesTopBar(
                state = state,
                onPreviousDay = viewModel::previousDay,
                onNextDay = viewModel::nextDay,
                selectedTeamsCount = selectedTeamIds.size,
                onTeamsClick = onTeamsClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val current = state) {
                MatchesUiState.Loading -> LoadingState()
                is MatchesUiState.Error -> ErrorState(
                    message = current.message,
                    onRetry = viewModel::retry,
                )
                is MatchesUiState.Content -> {
                    if (current.matches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Text(
                                    text = if (selectedTeamIds.isEmpty()) {
                                        "На выбранную дату матчей нет"
                                    } else {
                                        "У выбранных команд на эту дату матчей нет"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = if (selectedTeamIds.isEmpty()) {
                                        "Попробуйте выбрать другой день"
                                    } else {
                                        "Попробуйте выбрать другой день или другие команды"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    } else {
                        LazyColumn {
                            items(current.matches, key = { it.id }) { match ->
                                MatchListItem(
                                    match = match,
                                    onClick = onMatchClick,
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
private fun MatchesTopBar(
    state: MatchesUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    selectedTeamsCount: Int,
    onTeamsClick: () -> Unit,
) {
    val date = (state as? MatchesUiState.Content)?.date

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Предыдущий день",
                )
            }
            Text(
                text = date?.format(TITLE_DATE_FORMAT) ?: "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onNextDay) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = "Следующий день",
                )
            }
        }
        TextButton(onClick = onTeamsClick) {
            Text(
                text = if (selectedTeamsCount == 0) {
                    "Команды: все"
                } else {
                    "Команды: выбрано $selectedTeamsCount"
                },
            )
        }
    }
}
