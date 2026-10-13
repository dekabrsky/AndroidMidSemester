package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
    val country: CountryDto? = null,
)
