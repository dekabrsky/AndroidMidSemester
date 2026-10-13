package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList()
)

@Serializable
data class EspnLeagueDto(val name: String)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val competitions: List<EspnCompetitionDto> = emptyList()
)

@Serializable
data class EspnCompetitionDto(
    val status: EspnStatusDto,
    val competitors: List<EspnCompetitorDto> = emptyList()
)

@Serializable
data class EspnStatusDto(val type: EspnStatusTypeDto)

@Serializable
data class EspnStatusTypeDto(
    val state: String,              // "pre" | "in" | "post"
    val completed: Boolean = false
)

@Serializable
data class EspnCompetitorDto(
    val homeAway: String,           // "home" | "away"
    val score: String? = null,
    val team: EspnTeamDto
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String,
    val logo: String? = null
)