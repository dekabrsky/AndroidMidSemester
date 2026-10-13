package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter

@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ---- Основная информация: команды и счёт ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamInfo(team = match.home)
                Text(
                    text = if (match.homeScore != null && match.awayScore != null) {
                        "${match.homeScore} : ${match.awayScore}"
                    } else {
                        "vs"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                TeamInfo(team = match.away)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Дополнительные детали (минимум 3 новые) ----
            DetailRow(
                label = "Время старта",
                value = match.kickoff.format(DateTimeFormatter.ofPattern("HH:mm")),
            )
            DetailRow(label = "Лига", value = match.league)
            DetailRow(label = "Тур", value = match.round)

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Кнопка закрытия ----
            Button(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamInfo(team: Team) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = team.name,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}