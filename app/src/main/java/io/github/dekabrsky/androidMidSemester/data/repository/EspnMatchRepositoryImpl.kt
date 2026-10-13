package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchesCache,
) : MatchRepository {

    /** Если дату уже загружали — берём матчи из кэша, иначе идём в сеть и кэшируем результат. */
    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }
        val response = api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
        val league = response.leagues.firstOrNull()
        val matches = response.events.map {
            it.toMatch(
                leagueName = league?.name.orEmpty(),
                seasonName = league?.season?.displayName,
            )
        }
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match = api.summary(id).toMatch()
}
