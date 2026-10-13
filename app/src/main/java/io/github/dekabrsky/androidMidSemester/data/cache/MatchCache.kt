package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

/**
 * Кэш матчей в памяти. Ключ состоит из даты и набора выбранных команд,
 * потому что один и тот же день с разными фильтрами даёт разные списки.
 */
class MatchCache {
    private data class Key(val date: LocalDate, val teamIds: Set<Long>)

    private val matches = mutableMapOf<Key, List<Match>>()

    fun get(date: LocalDate, teamIds: Set<Long>): List<Match>? = matches[Key(date, teamIds)]

    fun put(date: LocalDate, teamIds: Set<Long>, value: List<Match>) {
        matches[Key(date, teamIds)] = value
    }
}
