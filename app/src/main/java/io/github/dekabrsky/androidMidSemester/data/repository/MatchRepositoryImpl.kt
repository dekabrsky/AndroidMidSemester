package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> = coroutineScope {
        cache.get(date)?.let { return@coroutineScope it }

        val page0 = async { runCatching { api.gamesOn(date = date.toString(), offset = 0, limit = 100) }.getOrNull() }
        val page1 = async { runCatching { api.gamesOn(date = date.toString(), offset = 100, limit = 100) }.getOrNull() }
        val page2 = async { runCatching { api.gamesOn(date = date.toString(), offset = 200, limit = 100) }.getOrNull() }

        val allGameDtos = listOfNotNull(
            page0.await(),
            page1.await(),
            page2.await(),
        ).flatMap { it.data }

        val russianGameDtos = allGameDtos.filter { dto ->
            dto.homeTeam.country?.name == "Russia" ||
                    dto.awayTeam.country?.name == "Russia" ||
                    dto.season.league.country?.name == "Russia" ||
                    dto.homeTeam.country?.code == "RU" ||
                    dto.awayTeam.country?.code == "RU" ||
                    dto.season.league.country?.code == "RU"
        }

        val resultDtos = if (russianGameDtos.isNotEmpty()) {
            russianGameDtos
        } else {
            allGameDtos.take(20)
        }

        val matches = resultDtos.map { it.toMatch() }
        cache.put(date, matches)
        matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
