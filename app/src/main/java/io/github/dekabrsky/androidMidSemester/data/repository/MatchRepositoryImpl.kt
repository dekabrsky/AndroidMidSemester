package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: EspnApi,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val dateString = date.toString().replace("-", "")

        Log.d(
            "ESPN_API",
            "GET https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/scoreboard?dates=$dateString"
        )

        return api.gamesOn(dateString)
            .events
            .map { it.toMatch() }
    }

    override suspend fun match(id: Long): Match {
        val event = api.game(id)
            .events
            .firstOrNull()
            ?: throw NoSuchElementException("Матч $id не найден")

        return event.toMatch()
    }
}

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