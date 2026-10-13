package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate


class MatchesCache {

    private val matchesByDate = mutableMapOf<LocalDate, List<Match>>()

    /** Возвращает сохранённые матчи для даты */
    fun get(date: LocalDate): List<Match>? = matchesByDate[date]

    /** Сохраняет результат загрузки для даты */
    fun put(date: LocalDate, matches: List<Match>) {
        matchesByDate[date] = matches
    }
}
