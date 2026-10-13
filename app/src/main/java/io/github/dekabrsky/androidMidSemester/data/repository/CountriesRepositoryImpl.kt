package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.repository.CountriesRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Collections

class CountriesRepositoryImpl(private val api: SStatsApi) : CountriesRepository {
    private val mutex = Mutex()
    private var cached: List<String>? = null

    override suspend fun countries(): List<String> = mutex.withLock {
        cached?.let { return@withLock it }
        val response = api.leagues()
        check(response.status == "OK") {
            response.message ?: "Не удалось загрузить страны"
        }
        val countries = response.data.mapNotNull { it.country?.name?.takeIf(String::isNotBlank) }
            .distinct().sorted()
        check(countries.isNotEmpty()) { "SStats вернул пустой список стран" }
        Collections.unmodifiableList(countries).also { cached = it }
    }
}
