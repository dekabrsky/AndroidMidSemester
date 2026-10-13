
package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsTeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<MatchRepository> {
        MatchRepositoryImpl(get<SStatsApi>())
    }

    single<TeamSelectionRepository> {
        SStatsTeamSelectionRepository(get<SStatsApi>())
    }
}
