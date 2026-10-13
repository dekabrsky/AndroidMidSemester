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
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val SSTATS_BASE_URL = "https://api.sstats.net/"
private const val ESPN_BASE_URL = "https://site.api.espn.com/"

val networkModule = module {
    single { Json { ignoreUnknownKeys = true } }
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
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    // --- sstats Retrofit ---
    single(named("sstatsRetrofit")) {
        Retrofit.Builder()
            .baseUrl(SSTATS_BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single<SStatsApi> {
        get<Retrofit>(named("sstatsRetrofit")).create(SStatsApi::class.java)
    }

    // --- ESPN Retrofit ---
    single(named("espnRetrofit")) {
        Retrofit.Builder()
            .baseUrl(ESPN_BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single<EspnApi> {
        get<Retrofit>(named("espnRetrofit")).create(EspnApi::class.java)
    }
}