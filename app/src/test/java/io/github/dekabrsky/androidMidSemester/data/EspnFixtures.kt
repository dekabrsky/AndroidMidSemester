package io.github.dekabrsky.androidMidSemester.data

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import kotlinx.serialization.json.Json

/** Сокращённые реальные ответы ESPN; неизвестные DTO поля оставлены для проверки парсинга. */
object EspnFixtures {
    val json = Json { ignoreUnknownKeys = true }

    fun text(name: String): String = checkNotNull(javaClass.getResource("/espn/$name.json"))
        .readText()

    fun scoreboard(name: String = "scoreboard"): EspnScoreboardDto = json.decodeFromString(text(name))

    fun summary(): EspnSummaryDto = json.decodeFromString(text("summary"))
}
