package io.github.dekabrsky.androidMidSemester.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.espn.EspnApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val SSTATS_BASE_URL = "https://api.sstats.net/"
private const val ESPN_BASE_URL = "https://site.api.espn.com/"

private val SStatsRetrofit = named("sstats")
private val EspnRetrofit = named("espn")

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
    single(SStatsRetrofit) { retrofit(SSTATS_BASE_URL) }
    single(EspnRetrofit) { retrofit(ESPN_BASE_URL) }

    single<SStatsApi> { get<Retrofit>(SStatsRetrofit).create(SStatsApi::class.java) }
    single<EspnApi> { get<Retrofit>(EspnRetrofit).create(EspnApi::class.java) }
}

private fun Scope.retrofit(baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(get())
        .addConverterFactory(
            get<Json>().asConverterFactory("application/json".toMediaType())
        )
        .build()
