package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

/**
 * Команда из ответа `/Teams/list`. В отличие от [TeamDto] внутри матча,
 * здесь сервер сам присылает logoUrl. Остальные поля (flashId, country) игнорируем.
 */
@Serializable
data class TeamListItemDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)
