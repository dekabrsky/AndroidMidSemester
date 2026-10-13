package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val espnApi: EspnApi,
    private val sstatsApi: SStatsApi,
) : MatchRepository {

    private val matchesByDateCache = mutableMapOf<LocalDate, List<Match>>()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        matchesByDateCache[date]?.let { return it }

        val matches = sstatsApi.gamesOn(date.toString()).data.map { it.toMatch() }

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

    override suspend fun teamsByCountry(country: String): List<Team> =
        sstatsApi.teamsByCountry(country).data.map { it.toTeam() }
}