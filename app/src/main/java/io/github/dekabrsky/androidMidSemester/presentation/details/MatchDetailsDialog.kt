package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import java.time.format.DateTimeFormatter


@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = match.home.logoUrl,
                    contentDescription = match.home.name,
                    modifier = Modifier.size(48.dp),
                    placeholder = painterResource(R.drawable.ic_team_placeholder),
                    error = painterResource(R.drawable.ic_team_placeholder),
                )
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(match.home.name)
                    Text(match.away.name)
                }
                AsyncImage(
                    model = match.away.logoUrl,
                    contentDescription = match.away.name,
                    modifier = Modifier.size(48.dp),
                    placeholder = painterResource(R.drawable.ic_team_placeholder),
                    error = painterResource(R.drawable.ic_team_placeholder),
                )
            }
            if (match.status == MatchStatus.FINISHED &&
                match.homeScore != null && match.awayScore != null
            ) {
                Text(
                    text = "${match.homeScore} : ${match.awayScore}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text("Время: ${match.kickoff.format(DateTimeFormatter.ofPattern("HH:mm"))}")
            Text("Лига: ${match.league}")
            Text("Тур: ${match.round}")
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Закрыть")
            }
        }
    }
}