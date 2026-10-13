package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private fun GameDto.toSStatsMatch(): Match = Match(
    id = id,
    home = Team(homeTeam.id, homeTeam.name, "https://sstats.net/assets/logos/${homeTeam.id}.png"),
    away = Team(awayTeam.id, awayTeam.name, "https://sstats.net/assets/logos/${awayTeam.id}.png"),
    homeScore = homeResult,
    awayScore = awayResult,
    status = if (statusName == "Finished") MatchStatus.FINISHED else MatchStatus.SCHEDULED,
    kickoff = Instant.ofEpochSecond(dateUtc).atZone(ZoneId.systemDefault()).toLocalDateTime(),
    league = season.league.name,
    round = roundName,
)

class SStatsMatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    private val matchesCache = mutableMapOf<LocalDate, List<Match>>()
    private val cacheMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match> = cacheMutex.withLock {
        matchesCache[date] ?: api.gamesOn(date.toString()).data
            .map { it.toSStatsMatch() }
            .also { matchesCache[date] = it }
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toSStatsMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
