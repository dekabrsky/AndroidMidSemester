package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class MatchesCache {

    private val matchesByDate = ConcurrentHashMap<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = matchesByDate[date]

    fun put(date: LocalDate, matches: List<Match>) {
        matchesByDate[date] = matches
    }
}
