package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.espn.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val ESPN_DEFAULT_LEAGUE = "rus.1"

private val TOP_LEAGUES = listOf(
    "rus.1",          // Российская Премьер-Лига
    "eng.1",          // Английская Премьер-Лига
    "esp.1",          // Ла Лига
    "uefa.champions", // Лига Чемпионов
    "ger.1",          // Бундеслига
    "ita.1",          // Серия А
    "fra.1",          // Лига 1
)

private val EspnDateFormatter: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE // yyyyMMdd

class EspnMatchesDataSource(
    private val api: EspnApi,
    private val league: String = ESPN_DEFAULT_LEAGUE,
) : MatchesRemoteDataSource {

    override suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match> = coroutineScope {
        val dateStr = date.format(EspnDateFormatter)
        val leaguesToQuery = if (league == ESPN_DEFAULT_LEAGUE) TOP_LEAGUES else listOf(league)

        val deferreds = leaguesToQuery.map { l ->
            async {
                runCatching {
                    val response = api.scoreboard(league = l, dates = dateStr)
                    val leagueDto = response.leagues.firstOrNull()
                    response.events.map { it.toMatch(leagueDto) }
                }.getOrDefault(emptyList())
            }
        }
        deferreds.awaitAll().flatten().distinctBy { it.id }
    }

    override suspend fun match(id: Long): Match = coroutineScope {
        val leaguesToQuery = if (league == ESPN_DEFAULT_LEAGUE) TOP_LEAGUES else listOf(league)
        for (l in leaguesToQuery) {
            val match = runCatching { api.summary(league = l, eventId = id).toMatch(id) }.getOrNull()
            if (match != null) return@coroutineScope match
        }
        throw NoSuchElementException("Матч $id не найден")
    }
}

