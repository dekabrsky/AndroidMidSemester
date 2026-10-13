package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.ESPNApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.EspnMapper
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MatchRepositoryImpl(
    private val api: ESPNApi,
    private val espnMapper: EspnMapper,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    private val espnDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }

        val formattedDate = date.format(espnDateFormatter)
        val response = api.getScoreboard(formattedDate)
        val matches = espnMapper.map(response)

        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match {
        return matchesOn(LocalDate.now()).firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Матч $id не найден")
    }

    //    override suspend fun matchesOn(date: LocalDate): List<Match> {
    //        // проверяем кэш
    //        cache.get(date)?.let { cacheMatches ->
    //            return cacheMatches
    //        }
    //
    //        val response = api.gamesOn(date.toString())
    //        val matches = response.data.map { it.toMatch() }
    //
    //        cache.put(date,matches)
    //
    //        return matches
    //    }
    //
    //    override suspend fun match(id: Long): Match =
    //        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
