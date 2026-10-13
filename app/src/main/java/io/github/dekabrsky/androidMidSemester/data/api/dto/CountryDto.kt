package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(val name: String)

@Serializable
data class LeagueCountryDto(val country: CountryDto? = null)
