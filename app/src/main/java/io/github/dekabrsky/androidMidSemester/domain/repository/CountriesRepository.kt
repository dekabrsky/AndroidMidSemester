package io.github.dekabrsky.androidMidSemester.domain.repository

interface CountriesRepository {
    suspend fun countries(): List<String>
}