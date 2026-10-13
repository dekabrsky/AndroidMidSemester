package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchMemoryCache,
) : MatchRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        // Проверяем наличия данных в кэше
        cache.get(date)?.let { return it }

        // Форматируем дату в YYYYMMDD для ESPN API
        val formattedDate = date.format(dateFormatter)
        val scoreboard = api.scoreboard(date = formattedDate)
        val matches = scoreboard.toMatches()

        // Сохраняем в кэш и возвращаем
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match {
        // Ищем матч среди уже закэшированных по всем датам или выдаем ошибку
        return cache.get(LocalDate.now())?.find { it.id == id }
            ?: throw NoSuchElementException("Матч $id не найден")
    }
}
