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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.util.Locale
/**
 * ЗАДАНИЕ СТУДЕНТАМ (итерация 0): контент диалога деталей матча.
 *
 * Функция уже зарегистрирована как отдельный узел навигации (entry<MatchDetails>
 * с метаданными DialogSceneStrategy.dialog() в presentation/navigation/Navigation.kt):
 * androidx.navigation3 сам показывает её в диалоговом окне и закрывает по тапу
 * вне диалога (срабатывает goBack навигатора).
 *
 * Что нужно сделать:
 * 1. Карточка (Card или Surface) с теми же данными, что в элементе списка:
 *    логотипы и названия команд, счёт сыгранного матча.
 * 2. Добавить минимум три детали, которых в списке нет: время старта
 *    (match.kickoff, формат "HH:mm"), лига (match.league), тур (match.round).
 * 3. Предусмотреть кнопку закрытия — по клику вызвать onDismiss.
 */
@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = match.league,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TeamInfo(match.home, Modifier.weight(1f))
                if (match.status == MatchStatus.FINISHED &&
                    match.homeScore != null && match.awayScore != null
                ) {
                    Text(
                        text = "${match.homeScore} : ${match.awayScore}",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                TeamInfo(match.away, Modifier.weight(1f))
            }
            HorizontalDivider()
            listOf(
                "Начало" to match.kickoff.format(DateTimeFormatter.ofPattern("HH:mm")),
                "Дата" to match.kickoff.format(
                    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
                ),
                "Лига" to match.league,
                "Тур" to match.round,
                "Статус" to if (match.status == MatchStatus.FINISHED) "Завершён" else "Не сыгран",
            ).forEach { (label, value) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(label, modifier = Modifier.weight(1f))
                    Text(value, modifier = Modifier.weight(2f), textAlign = TextAlign.End)
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Закрыть")
            }
        }
    }
}
@Composable
private fun TeamInfo(team: Team, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}