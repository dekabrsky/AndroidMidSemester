package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TIME_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

/**
 * Контент диалога деталей матча.
 *
 * Показывается как отдельный узел навигации (см. entry<MatchDetails> с
 * DialogSceneStrategy.dialog() в presentation/navigation/Navigation.kt).
 * androidx.navigation3 сам оборачивает этот Composable в диалоговое окно
 * и закрывает по тапу вне диалога — вызывая onDismiss (он же goBack навигатора).
 */
@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Детали матча",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(Modifier.height(20.dp))

            // Логотипы, названия команд и счёт (у сыгранных — цифры, у остальных — «—»)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamColumn(
                    name = match.home.name,
                    logoUrl = match.home.logoUrl,
                    modifier = Modifier.weight(1f),
                )

                val scoreText = if (match.homeScore != null && match.awayScore != null) {
                    "${match.homeScore} : ${match.awayScore}"
                } else {
                    "—"
                }
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                TeamColumn(
                    name = match.away.name,
                    logoUrl = match.away.logoUrl,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Детали, которых нет в списке
            DetailRow(label = "Начало", value = match.kickoff.format(TIME_FORMAT))
            DetailRow(label = "Лига", value = match.league)
            DetailRow(label = "Тур", value = match.round)

            Spacer(Modifier.height(20.dp))

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
private fun TeamColumn(
    name: String,
    logoUrl: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = name,
            modifier = Modifier.size(56.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}