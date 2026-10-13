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

private const val SSTATS_BASE_URL = "https://api.sstats.net/"
private const val ESPN_BASE_URL = "https://site.api.espn.com/"

private val sstatsRetrofit = named("sstats")
private val espnRetrofit = named("espn")

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

    single(sstatsRetrofit) { retrofit(SSTATS_BASE_URL, get(), get()) }
    single(espnRetrofit) { retrofit(ESPN_BASE_URL, get(), get()) }

    single<SStatsApi> { get<Retrofit>(sstatsRetrofit).create(SStatsApi::class.java) }
    single<EspnApi> { get<Retrofit>(espnRetrofit).create(EspnApi::class.java) }
}

private fun retrofit(baseUrl: String, client: OkHttpClient, json: Json): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()