package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter

private val KICKOFF_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = stringResource(R.string.match_details_title),
                style = MaterialTheme.typography.titleLarge,
            )
            MatchSummary(match = match)
            HorizontalDivider()
            MatchDetail(
                label = stringResource(R.string.match_details_kickoff),
                value = match.kickoff.format(KICKOFF_TIME_FORMAT),
            )
            MatchDetail(
                label = stringResource(R.string.match_details_league),
                value = match.league,
            )
            MatchDetail(
                label = stringResource(R.string.match_details_round),
                value = match.round,
            )
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.End)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        ) {
            Text(stringResource(R.string.match_details_close))
        }
    }
}

@Composable
private fun MatchSummary(match: Match, modifier: Modifier = Modifier) {
    val fontScale = LocalDensity.current.fontScale
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth / fontScale < 260.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MatchTeam(team = match.home, modifier = Modifier.fillMaxWidth())
                MatchScore(match = match)
                MatchTeam(team = match.away, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MatchTeam(team = match.home, modifier = Modifier.weight(1f))
                MatchScore(match = match)
                MatchTeam(team = match.away, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MatchScore(match: Match) {
    Text(
        text = if (match.homeScore != null && match.awayScore != null) {
            stringResource(R.string.match_details_score, match.homeScore, match.awayScore)
        } else {
            stringResource(R.string.match_details_no_score)
        },
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun MatchTeam(team: Team, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
            fallback = painterResource(R.drawable.ic_team_placeholder),
        )
        Text(
            text = team.name.ifBlank { stringResource(R.string.match_details_unknown_team) },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MatchDetail(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.ifBlank { stringResource(R.string.match_details_unknown) },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
