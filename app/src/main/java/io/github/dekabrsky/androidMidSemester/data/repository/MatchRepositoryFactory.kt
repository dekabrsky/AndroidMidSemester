package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import retrofit2.Retrofit

enum class MatchDataProvider { ESPN, SSTATS }

fun createMatchRepository(
    retrofit: Retrofit,
    cache: MatchCache,
    provider: MatchDataProvider,
    teamRepository: TeamRepository? = null,
): MatchRepository = when (provider) {
    MatchDataProvider.ESPN -> EspnMatchRepositoryImpl(
        api = retrofit.newBuilder()
            .baseUrl("https://site.api.espn.com/")
            .build()
            .create(EspnApi::class.java),
        cache = cache,
    )
    MatchDataProvider.SSTATS -> {
        val api = retrofit.newBuilder()
            .baseUrl("https://api.sstats.net/")
            .build()
            .create(SStatsApi::class.java)
        SStatsMatchRepositoryImpl(
            api = api,
            cache = cache,
            teamRepository = teamRepository ?: SStatsTeamRepositoryImpl(api),
        )
    }
}
