package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.ui.theme.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val kickoffTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Содержимое диалогового узла. Само модальное окно создаёт DialogSceneStrategy. */
@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = match.league,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = match.round,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamSummary(team = match.home, modifier = Modifier.weight(1f))
                Text(
                    text = match.scoreText(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                TeamSummary(team = match.away, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()
            DetailRow(
                label = "Время начала",
                value = match.kickoff.format(kickoffTimeFormatter),
            )
            DetailRow(label = "Лига", value = match.league)
            DetailRow(label = "Тур", value = match.round)

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamSummary(
    team: Team,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = "Логотип ${team.name}",
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Text(
            text = team.name,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}

private fun Match.scoreText(): String =
    if (status == MatchStatus.FINISHED && homeScore != null && awayScore != null) {
        "$homeScore : $awayScore"
    } else {
        "— : —"
    }

@Preview(showBackground = true)
@Composable
private fun MatchDetailsDialogPreview() {
    AppTheme(darkTheme = false) {
        MatchDetailsDialog(
            match = Match(
                id = 1,
                home = Team(id = 1, name = "Арсенал", logoUrl = null),
                away = Team(id = 2, name = "Челси", logoUrl = null),
                homeScore = 2,
                awayScore = 1,
                status = MatchStatus.FINISHED,
                kickoff = LocalDateTime.of(2026, 10, 13, 19, 30),
                league = "Премьер-лига",
                round = "8 тур",
            ),
            onDismiss = {},
        )
    }
}
