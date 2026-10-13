
package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class MatchesMemoryCache {
    private val cache = mutableMapOf<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = cache[date]

    fun put(date: LocalDate, matches: List<Match>) {
        cache[date] = matches
    }
}
