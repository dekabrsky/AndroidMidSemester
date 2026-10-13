package io.github.dekabrsky.androidMidSemester.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val ESPN_BASE_URL = "https://site.api.espn.com/"
private const val SSTATS_BASE_URL = "https://api.sstats.net/"

val EspnRetrofit = named("EspnRetrofit")
val SStatsRetrofit = named("SStatsRetrofit")

val networkModule = module {
    single { Json { ignoreUnknownKeys = true } }
    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
    single { ChuckerInterceptor.Builder(androidContext()).build() }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<ChuckerInterceptor>())
            .build()
    }

    single<Retrofit>(EspnRetrofit) {
        Retrofit.Builder()
            .baseUrl(ESPN_BASE_URL)
            .client(get())
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    single { get<Retrofit>(EspnRetrofit).create(EspnApi::class.java) }

    single<Retrofit>(SStatsRetrofit) {
        Retrofit.Builder()
            .baseUrl(SSTATS_BASE_URL)
            .client(get())
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    single { get<Retrofit>(SStatsRetrofit).create(SStatsApi::class.java) }
}