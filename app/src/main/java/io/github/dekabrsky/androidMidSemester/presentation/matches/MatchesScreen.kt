package io.github.dekabrsky.androidMidSemester.presentation.matches

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

    Scaffold(
        topBar = {
            MatchesTopBar(
                state = state,
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
                is MatchesUiState.Content ->
                    if (current.matches.isEmpty()) {
                        EmptyMatches(isFiltered = current.isFiltered)
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

@Composable
private fun EmptyMatches(isFiltered: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isFiltered) {
                "У выбранных команд нет матчей в этот день"
            } else {
                "В этот день матчей нет"
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MatchesTopBar(
    state: MatchesUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onTeamsClick: () -> Unit,
) {
    val content = state as? MatchesUiState.Content
    val isFiltered = content?.isFiltered == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Предыдущий день",
            )
        }
        Text(
            text = content?.date?.format(TITLE_DATE_FORMAT) ?: "",
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
        IconButton(onClick = onTeamsClick) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Выбрать команды",
                // Подсвечиваем иконку, когда фильтр по командам включён.
                tint = if (isFiltered) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            )
        }
    }
}
