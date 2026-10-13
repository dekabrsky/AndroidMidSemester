package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory кэш для хранения списка матчей по датам.
 */
class MatchMemoryCache {

    private val cache = ConcurrentHashMap<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = cache[date]

    fun put(date: LocalDate, matches: List<Match>) {
        cache[date] = matches
    }
}
