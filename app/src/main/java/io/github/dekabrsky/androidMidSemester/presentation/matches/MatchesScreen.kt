package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.ErrorState
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
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
    val date by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedTeamIds by viewModel.selectedTeamIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MatchesTopBar(
                date = date,
                selectedTeamCount = selectedTeamIds.size,
                onPreviousDay = viewModel::previousDay,
                onNextDay = viewModel::nextDay,
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
                is MatchesUiState.Error -> ErrorState(message = current.message)
                is MatchesUiState.Content -> if (current.matches.isEmpty()) {
                    EmptyMatches(selectedTeamIds.size, onTeamsClick)
                } else {
                    LazyColumn {
                        items(current.matches, key = { it.id }) { match ->
                            MatchListItem(match = match, onClick = onMatchClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchesTopBar(
    date: LocalDate,
    selectedTeamCount: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onTeamsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Предыдущий день")
            }
            Text(
                text = date.format(TITLE_DATE_FORMAT),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onNextDay) {
                Icon(painterResource(R.drawable.ic_arrow_right), contentDescription = "Следующий день")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (selectedTeamCount == 0) stringResource(R.string.matches_all_russian_teams)
                    else stringResource(R.string.teams_selection_count, selectedTeamCount),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onTeamsClick) {
                Text(stringResource(R.string.matches_choose_teams))
            }
        }
    }
}

@Composable
private fun EmptyMatches(selectedTeamCount: Int, onTeamsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(stringResource(R.string.matches_empty_title), style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(if (selectedTeamCount > 0) R.string.matches_empty_selected else R.string.matches_empty_all),
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onTeamsClick) {
            Text(stringResource(R.string.matches_choose_teams))
        }
    }
}
