package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Long,
    val dateUtc: Long,
    val statusName: String,
    val homeResult: Int? = null,
    val awayResult: Int? = null,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val season: SeasonDto,
    val roundName: String,
)

@Serializable
data class TeamDto(
    val id: Long,
    val name: String,
    val country: CountryDto? = null,
)

@Serializable
data class CountryDto(
    val name: String = "",
)

@Serializable
data class SeasonDto(
    val league: LeagueDto,
)

@Serializable
data class LeagueDto(
    val name: String,
)
