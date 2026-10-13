package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

/**
 * Элемент ответа `/Teams/list` — в отличие от [TeamDto] из матчей,
 * здесь sstats отдаёт готовый `logoUrl` (и кучу лишних полей, которые игнорируются).
 */
@Serializable
data class SStatsTeamDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)
