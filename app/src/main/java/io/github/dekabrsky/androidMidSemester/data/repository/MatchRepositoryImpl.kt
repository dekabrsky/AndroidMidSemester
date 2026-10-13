package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchMemoryCache

/**
 * Алиас для SStatsMatchRepositoryImpl (оставлен для обратной совместимости).
 */
class MatchRepositoryImpl(
    api: SStatsApi,
    cache: MatchMemoryCache,
) : SStatsMatchRepositoryImpl(api, cache)
