package io.github.dekabrsky.androidMidSemester.presentation.matches

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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.CountryPickerDialog
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.errorMessage
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
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val countries by viewModel.countries.collectAsStateWithLifecycle()
    var countryPickerOpen by rememberSaveable { mutableStateOf(false) }

    if (countryPickerOpen) {
        CountryPickerDialog(
            state = countries,
            onSelect = { viewModel.selectCountry(it); countryPickerOpen = false },
            onRetry = viewModel::loadCountries,
            onDismiss = { countryPickerOpen = false },
            onClear = { viewModel.selectCountry(null); countryPickerOpen = false },
        )
    }

    Scaffold(
        topBar = {
            Column {
                MatchesTopBar(
                    date = state.date,
                    onPreviousDay = viewModel::previousDay,
                    onNextDay = viewModel::nextDay,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { viewModel.loadCountries(); countryPickerOpen = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(selectedCountry?.let { stringResource(R.string.matches_country_value, it) }
                            ?: stringResource(R.string.matches_country_open))
                    }
                    TextButton(onClick = onTeamsClick) {
                        Text(stringResource(R.string.teams_open))
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val current = state) {
                is MatchesUiState.Loading -> LoadingState()
                is MatchesUiState.Error -> ErrorState(
                    message = errorMessage(current.cause),
                    onRetry = viewModel::retry,
                )
                is MatchesUiState.Content -> {
                    if (current.matches.isEmpty()) {
                        Text(
                            text = stringResource(
                                if (current.isFilterActive) R.string.matches_empty_filtered
                                else R.string.matches_empty_day,
                            ),
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
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
    date: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
) {
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
            text = date.format(TITLE_DATE_FORMAT),
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
}
