package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class MatchesCache {

    private val cache = ConcurrentHashMap<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = cache[date]

    fun put(date: LocalDate, matches: List<Match>) {
        cache[date] = matches
    }

    fun contains(date: LocalDate): Boolean = cache.containsKey(date)

    fun clear() = cache.clear()
}