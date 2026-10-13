package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamsResponseDto(
    @SerialName("status") val status: String = "",
    @SerialName("count") val count: Int = 0,
    @SerialName("data") val data: List<TeamDto> = emptyList(),
)

@Serializable
data class TeamDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("logoUrl") val logoUrl: String? = null,
    @SerialName("country") val country: CountryDto,
)

@Serializable
data class CountryDto(
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
)