package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Column
import io.github.dekabrsky.androidMidSemester.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.format.DateTimeFormatter

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

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
@Composable
fun MatchDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = match.league,
                textAlign = TextAlign.Center
            )

            Text(
                text = match.round,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamColumn(
                    logoUrl = match.home.logoUrl,
                    teamName = match.home.name,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = match.homeScore?.let { "${it} ${match.awayScore}" } ?: "-",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                TeamColumn(
                    logoUrl = match.away.logoUrl,
                    teamName = match.away.name,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = match.kickoff.format(timeFormatter)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
    logoUrl: String?,
    teamName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = teamName,
            modifier = Modifier.size(56.dp),
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = teamName,
            textAlign = TextAlign.Center,
        )
    }
}
