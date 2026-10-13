package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.format.DateTimeFormatter

private val KICKOFF_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "Детали матча",
                style = MaterialTheme.typography.headlineSmall,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MatchTeam(team = match.home, modifier = Modifier.weight(1f))
                MatchTeam(team = match.away, modifier = Modifier.weight(1f))
            }
            Text(
                text = if (match.homeScore != null && match.awayScore != null) {
                    "${match.homeScore} : ${match.awayScore}"
                } else {
                    "VS"
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MatchDetail(label = "Начало", value = match.kickoff.format(KICKOFF_TIME_FORMAT))
                MatchDetail(label = "Лига", value = match.league.ifBlank { "Не указана" })
                MatchDetail(label = "Тур", value = match.round.ifBlank { "Не указан" })
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
private fun MatchTeam(team: Team, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = team.name,
            modifier = Modifier.size(56.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Text(
            text = team.name,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MatchDetail(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
        )
    }
}
