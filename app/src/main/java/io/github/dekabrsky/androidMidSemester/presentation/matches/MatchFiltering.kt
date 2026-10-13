package io.github.dekabrsky.androidMidSemester.presentation.matches

import io.github.dekabrsky.androidMidSemester.domain.model.Match

/**
 * Оставляет только матчи, в которых участвует хотя бы одна из выбранных команд.
 *
 * Пустой [selectedTeamIds] означает «фильтр не задан» — показываем все матчи.
 * Вынесено из ViewModel отдельной чистой функцией, чтобы легко покрыть тестом.
 */
fun filterMatchesByTeams(
    matches: List<Match>,
    selectedTeamIds: Set<Long>,
): List<Match> = if (selectedTeamIds.isEmpty()) {
    matches
} else {
    matches.filter { it.home.id in selectedTeamIds || it.away.id in selectedTeamIds }
}
