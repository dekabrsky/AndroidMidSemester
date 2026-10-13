package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SStatsTeamDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)
