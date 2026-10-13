package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter
import java.util.Locale

private val KICKOFF_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
private val KICKOFF_DATE_FORMAT =
    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))

/**
 * Контент диалога деталей матча (итерация 0).
 *
 * Функция зарегистрирована как отдельный узел навигации (entry<MatchDetails>
 * с метаданными DialogSceneStrategy.dialog() в presentation/navigation/Navigation.kt):
 * androidx.navigation3 сам показывает её в диалоговом окне и закрывает по тапу
 * вне диалога (срабатывает goBack навигатора), поэтому в Dialog не оборачиваем.
 *
 * Показывает:
 * - те же данные, что элемент списка: логотипы и названия команд, счёт (если есть);
 * - детали, которых в списке нет: время старта (HH:mm), дата, лига, тур, статус;
 * - кнопку «Закрыть», которая вызывает onDismiss.
 */
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
            Text(
                text = match.league,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamColumn(team = match.home, modifier = Modifier.weight(1f))
                ScoreText(match = match)
                TeamColumn(team = match.away, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DetailRow(label = "Начало", value = match.kickoff.format(KICKOFF_TIME_FORMAT))
                DetailRow(label = "Дата", value = match.kickoff.format(KICKOFF_DATE_FORMAT))
                DetailRow(label = "Лига", value = match.league)
                DetailRow(label = "Тур", value = match.round)
                DetailRow(label = "Статус", value = match.statusLabel())
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp),
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun TeamColumn(
    team: Team,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = team.name,
            modifier = Modifier.size(64.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/** Счёт показываем, как и в списке, только если он есть (у сыгранного или идущего матча). */
@Composable
private fun ScoreText(match: Match) {
    val homeScore = match.homeScore
    val awayScore = match.awayScore
    Text(
        text = if (homeScore != null && awayScore != null) "$homeScore : $awayScore" else "– : –",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f),
        )
    }
}

private fun Match.statusLabel(): String = when (status) {
    MatchStatus.FINISHED -> "Завершён"
    MatchStatus.SCHEDULED -> if (homeScore != null) "Идёт" else "Не начался"
}
