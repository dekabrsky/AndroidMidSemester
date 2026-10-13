package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.format.DateTimeFormatter

private val KickoffFormatter = DateTimeFormatter.ofPattern("HH:mm")

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
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamInfo(
                    name = match.home.name,
                    logoUrl = match.home.logoUrl,
                )

                Text(
                    text = if (match.homeScore != null && match.awayScore != null) {
                        "${match.homeScore} : ${match.awayScore}"
                    } else {
                        "- : -"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                )

                TeamInfo(
                    name = match.away.name,
                    logoUrl = match.away.logoUrl,
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            MatchDetail(
                label = "Время начала",
                value = match.kickoff.format(KickoffFormatter),
            )

            MatchDetail(
                label = "Лига",
                value = match.league,
            )

            MatchDetail(
                label = "Тур",
                value = match.round,
            )

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamInfo(
    name: String,
    logoUrl: String?,
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = name,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MatchDetail(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}