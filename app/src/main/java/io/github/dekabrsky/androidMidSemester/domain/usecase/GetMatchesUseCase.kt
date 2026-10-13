package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import android.util.Log

class GetMatchesUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(date: LocalDate): List<Match> {
        Log.d("USECASE", "Запрос матчей на дату: $date")
        val matches = repository.matchesOn(date)
        Log.d("USECASE", "Получено матчей: ${matches.size}")
        return matches
    }
}