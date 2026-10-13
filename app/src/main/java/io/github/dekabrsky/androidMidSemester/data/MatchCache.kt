package io.github.dekabrsky.androidMidSemester.data

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class MatchCache {

    private val cache = mutableMapOf<LocalDate, List<Match>>()

    @Synchronized
    fun get(date: LocalDate): List<Match>? {
        return cache[date]
    }

    @Synchronized
    fun put(date: LocalDate, matches: List<Match>) {
        cache[date] = matches
    }
}