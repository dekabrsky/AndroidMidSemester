package io.github.dekabrsky.androidMidSemester

import android.app.Application
import io.github.dekabrsky.androidMidSemester.di.cacheModule
import io.github.dekabrsky.androidMidSemester.di.networkModule
import io.github.dekabrsky.androidMidSemester.di.repositoryModule
import io.github.dekabrsky.androidMidSemester.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FootballDigestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FootballDigestApp)
            modules(networkModule, repositoryModule, viewModelModule, cacheModule)
        }
    }
}
