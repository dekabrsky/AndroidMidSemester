package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class MatchesCache {

    private data class Key(val date: LocalDate, val teamIds: Set<Long>)

    private val matchesByKey = ConcurrentHashMap<Key, List<Match>>()

    fun get(date: LocalDate, teamIds: Set<Long>): List<Match>? = matchesByKey[Key(date, teamIds)]

    fun put(date: LocalDate, teamIds: Set<Long>, matches: List<Match>) {
        matchesByKey[Key(date, teamIds)] = matches
    }
}
