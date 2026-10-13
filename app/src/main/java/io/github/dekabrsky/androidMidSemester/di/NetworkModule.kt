package io.github.dekabrsky.androidMidSemester.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import org.koin.core.qualifier.named
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://api.sstats.net/"

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
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    single<SStatsApi> { get<Retrofit>().create(SStatsApi::class.java) }
    single(named("espn")) {
        Retrofit.Builder()
            .baseUrl("https://site.api.espn.com/")
            .client(get())
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
    single { get<Retrofit>(named("espn")).create(EspnApi::class.java) }
}
