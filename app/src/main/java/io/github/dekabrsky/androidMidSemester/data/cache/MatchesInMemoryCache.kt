package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class MatchesInMemoryCache {
    // Храним данные в памяти: ключ - дата, значение - список матчей
    private val cache = mutableMapOf<LocalDate, List<Match>>()

    fun get(date: LocalDate): List<Match>? = cache[date]

    fun put(date: LocalDate, matches: List<Match>) {
        cache[date] = matches
    }

    // Опционально: метод для очистки кэша, если понадобится в тестах
    fun clear() = cache.clear()
}