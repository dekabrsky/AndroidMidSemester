package io.github.dekabrsky.androidMidSemester.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import java.time.format.DateTimeFormatter
import java.util.Locale

private val KICKOFF_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru"))

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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Детали матча",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            TeamLogo(
                                logoUrl = match.home.logoUrl,
                                teamName = match.home.name,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.home.name,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            if (match.homeScore != null && match.awayScore != null) {
                                Text(
                                    text = "${match.homeScore} : ${match.awayScore}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                Text(
                                    text = "— : —",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            TeamLogo(
                                logoUrl = match.away.logoUrl,
                                teamName = match.away.name,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.away.name,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                HorizontalDivider()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DetailRow(
                        label = "Дата и время:",
                        value = match.kickoff.format(KICKOFF_FORMATTER),
                    )
                    DetailRow(
                        label = "Лига:",
                        value = match.league,
                    )
                    DetailRow(
                        label = "Тур:",
                        value = match.round,
                    )
                    DetailRow(
                        label = "Статус:",
                        value = when (match.status) {
                            MatchStatus.FINISHED -> "Завершён"
                            MatchStatus.SCHEDULED -> "Запланирован"
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Закрыть")
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
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
        contentDescription = teamName,
        modifier = modifier.size(48.dp),
        placeholder = painterResource(R.drawable.ic_team_placeholder),
        error = painterResource(R.drawable.ic_team_placeholder),
    )
}