package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TeamEnvelope<T>(
    val status: String,
    val count: Int = 0,
    val data: List<T> = emptyList(),
    @SerialName("TotalCount") val totalCount: Int = 0,
)

@Serializable
data class SStatsTeamDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)
