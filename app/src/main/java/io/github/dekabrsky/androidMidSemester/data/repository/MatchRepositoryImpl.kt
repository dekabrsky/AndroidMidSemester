package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

enum class MatchDataProvider { ESPN, SSTATS }

/**
 * Совместимая точка входа для существующего DI. Для возврата к sstats
 * достаточно изменить значение provider по умолчанию на MatchDataProvider.SSTATS.
 */
class MatchRepositoryImpl(
    api: SStatsApi,
    provider: MatchDataProvider = MatchDataProvider.ESPN,
) : MatchRepository {

    private val repository: MatchRepository = when (provider) {
        MatchDataProvider.ESPN -> EspnMatchRepository(api)
        MatchDataProvider.SSTATS -> SStatsMatchRepository(api)
    }

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        repository.matchesOn(date)

    override suspend fun match(id: Long): Match =
        repository.match(id)
}
