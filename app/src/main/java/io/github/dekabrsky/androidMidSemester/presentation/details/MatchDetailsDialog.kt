package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun MatchDetailsDialog(match: Match, onDismiss: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamColumn(team = match.home, modifier = Modifier.weight(1f))

                Text(
                    text = scoreText(match),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                TeamColumn(team = match.away, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            DetailRow(label = "Время старта", value = match.kickoff.format(timeFormatter))
            DetailRow(label = "Лига", value = match.league)
            DetailRow(label = "Тур", value = match.round)

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamColumn(team: Team, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = team.name,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun scoreText(match: Match): String =
    if (match.status == MatchStatus.FINISHED &&
        match.homeScore != null && match.awayScore != null
    ) {
        "${match.homeScore} : ${match.awayScore}"
    } else {
        "vs"
    }