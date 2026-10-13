package io.github.dekabrsky.androidMidSemester.di

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named // <-- ВАЖНО: импорт для named()
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext

val networkModule = module {
    single {
        Json { ignoreUnknownKeys = true }
    }
    single {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }
    single {
        ChuckerInterceptor.Builder(androidContext()).build()
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<ChuckerInterceptor>())
            .build()
    }

    // 1. Ретрофит для SStats (с явным указанием типа и named-квалификатором)
    single<Retrofit>(named("sstatsRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://api.sstats.net/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // 2. Ретрофит для ESPN (с явным указанием типа и named-квалификатором)
    single<Retrofit>(named("espnRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://site.api.espn.com/") // <-- Базовый URL для ESPN
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // 3. Создаем API интерфейсы, явно указывая, какой Retrofit брать через named()
    single<SStatsApi> {
        get<Retrofit>(named("sstatsRetrofit")).create(SStatsApi::class.java)
    }

    single<EspnApi> {
        get<Retrofit>(named("espnRetrofit")).create(EspnApi::class.java)
    }
}