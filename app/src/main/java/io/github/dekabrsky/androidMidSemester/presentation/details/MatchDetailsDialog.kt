package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
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
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Детали матча",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            // Команды и счёт
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TeamLogo(
                    logoUrl = match.home.logoUrl,
                    teamName = match.home.name,
                    modifier = Modifier.size(56.dp),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = match.home.name,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "—",
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Text(
                        text = match.away.name,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                TeamLogo(
                    logoUrl = match.away.logoUrl,
                    teamName = match.away.name,
                    modifier = Modifier.size(56.dp),
                )
            }

            // Счёт показывается только при наличии обоих значений
            if (match.homeScore != null && match.awayScore != null) {
                Text(
                    text = "${match.homeScore} : ${match.awayScore}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Дополнительная информация о матче
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MatchDetailRow(
                    label = "Время начала",
                    value = match.kickoff.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                )

                MatchDetailRow(
                    label = "Лига",
                    value = match.league,
                )

                MatchDetailRow(
                    label = "Тур",
                    value = match.round,
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamLogo(
    logoUrl: String?,
    teamName: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = logoUrl,
        contentDescription = "Логотип команды $teamName",
        modifier = modifier,
        placeholder = painterResource(R.drawable.ic_team_placeholder),
        error = painterResource(R.drawable.ic_team_placeholder),
    )
}

@Composable
private fun MatchDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}