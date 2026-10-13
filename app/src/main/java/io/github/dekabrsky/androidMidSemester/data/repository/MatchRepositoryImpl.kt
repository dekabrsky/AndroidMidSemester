package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import retrofit2.Retrofit

class MatchRepositoryImpl(
    retrofit: Retrofit,
    cache: MatchCache,
    provider: MatchDataProvider = MatchDataProvider.SSTATS,
    teamRepository: TeamRepository? = null,
) : MatchRepository by createMatchRepository(retrofit, cache, provider, teamRepository)
