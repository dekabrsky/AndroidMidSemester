package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val games = mutableListOf<GameDto>()
        var offset = 0
        while (true) {
            val page = api.gamesOn(date = date.toString(), offset = offset, limit = PAGE_SIZE)
            games += page.data
            if (page.data.size < PAGE_SIZE) break
            offset += page.data.size
        }
        return games.map { it.toMatch() }
    }

    override suspend fun match(id: Long): Match {
        return api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
    }
}

private const val PAGE_SIZE = 100

//package io.github.dekabrsky.androidMidSemester.data.repository
//
//import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
//import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
//import io.github.dekabrsky.androidMidSemester.domain.model.Match
//import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
//import java.time.LocalDate
//
//class MatchRepositoryImpl(
//    private val api: SStatsApi,
//) : MatchRepository {
//
//    override suspend fun matchesOn(date: LocalDate): List<Match> =
//        api.gamesOn(date.toString()).data.map { it.toMatch() }
//
//    override suspend fun match(id: Long): Match =
//        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
//}
