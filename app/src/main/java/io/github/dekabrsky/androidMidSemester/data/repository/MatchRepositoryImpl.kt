package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.cache.InMemoryMatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class SStatsMatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    private val cache = InMemoryMatchCache()

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.matchesOn(date) {
            loadAllMatches(date).map(GameDto::toMatch)
        }

    override suspend fun match(id: Long): Match = cache.match(id) {
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
    }

    private suspend fun loadAllMatches(date: LocalDate): List<GameDto> {
        val allGames = mutableListOf<GameDto>()
        var offset = 0
        while (true) {
            val page = api.gamesOn(date.toString(), offset, PAGE_SIZE).data
            allGames += page
            if (page.size < PAGE_SIZE) break
            offset += page.size
        }
        return allGames
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}
