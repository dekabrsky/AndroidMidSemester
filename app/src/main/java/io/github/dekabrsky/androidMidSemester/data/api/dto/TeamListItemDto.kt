package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeamListItemDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
    val country: CountryDto? = null,
)

@Serializable
data class CountryDto(
    val code: String? = null,
    val name: String? = null,
)
