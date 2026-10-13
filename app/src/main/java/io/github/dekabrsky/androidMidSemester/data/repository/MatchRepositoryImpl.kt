package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ESPN_LEAGUE = "rus.1"
private val ESPN_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

class MatchRepositoryImpl(
    private val espnApi: EspnApi,
    private val sstatsApi: SStatsApi,
) : MatchRepository {


    private val matchesByDateCache = mutableMapOf<LocalDate, List<Match>>()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        matchesByDateCache[date]?.let { return it }

        val matches = espnApi
            .scoreboard(
                league = ESPN_LEAGUE,
                dates = date.format(ESPN_DATE_FORMAT),
            )
            .toMatches()

        matchesByDateCache[date] = matches
        return matches
    }


    override suspend fun match(id: Long): Match {
        matchesByDateCache.values
            .firstOrNull { list -> list.any { it.id == id } }
            ?.firstOrNull { it.id == id }
            ?.let { return it }

        return sstatsApi.game(id).data?.toMatch()
            ?: throw NoSuchElementException("Матч $id не найден")
    }
}