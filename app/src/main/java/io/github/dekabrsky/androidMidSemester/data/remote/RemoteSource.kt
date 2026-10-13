package io.github.dekabrsky.androidMidSemester.data.remote

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

interface RemoteSource {
    suspend fun matchesOn(date: LocalDate): List<Match>
    suspend fun match(id: Long): Match
}