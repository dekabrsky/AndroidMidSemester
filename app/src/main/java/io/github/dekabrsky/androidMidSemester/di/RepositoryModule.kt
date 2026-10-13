package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.SettingsRepository
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesInMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    // Теперь передаем два аргумента: api (get()) и cache (get())
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<SettingsRepository> { SettingsRepository(androidContext()) }
}

/*val repositoryModule = module {
    // Старая реализация SStats (оставлена в проекте, но закомментирована)
    // single<MatchRepository> { MatchRepositoryImpl(get(), get()) }

    // Новая реализация ESPN
    single<MatchRepository> { EspnMatchRepositoryImpl(get(), get()) }
}*/

val cacheModule = module {
    single { MatchesInMemoryCache() } // <-- ДОБАВЛЕНО
}
