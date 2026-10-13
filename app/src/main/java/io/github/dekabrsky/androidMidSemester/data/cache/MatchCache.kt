package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class MatchCache {
    private val matches = mutableMapOf<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = matches[date]

    fun put(date: LocalDate, value: List<Match>) {
        matches[date] = value
    }
}
