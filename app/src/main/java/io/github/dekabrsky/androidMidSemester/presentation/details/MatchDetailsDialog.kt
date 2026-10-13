package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import java.time.format.DateTimeFormatter

private val KICKOFF_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TeamLogo(match.home.logoUrl, match.home.name)
                Text(
                    text = match.home.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (match.status == MatchStatus.FINISHED &&
                        match.homeScore != null && match.awayScore != null
                    ) {
                        "${match.homeScore} : ${match.awayScore}"
                    } else {
                        "—"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = match.away.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                TeamLogo(match.away.logoUrl, match.away.name)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(label = "Время начала", value = match.kickoff.format(KICKOFF_TIME_FORMAT))
                DetailRow(label = "Лига", value = match.league)
                DetailRow(label = "Тур", value = match.round)
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamLogo(logoUrl: String?, teamName: String) {
    AsyncImage(
        model = logoUrl,
        contentDescription = "Логотип команды $teamName",
        modifier = Modifier.size(48.dp),
        placeholder = painterResource(R.drawable.ic_team_placeholder),
        error = painterResource(R.drawable.ic_team_placeholder),
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
        )
    }
}
