package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val events: List<EspnEventDto> = emptyList()
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val season: EspnSeasonDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList()
)

@Serializable
data class EspnSeasonDto(
    val year: Int? = null,
    val slug: String? = null
)

@Serializable
data class EspnCompetitionDto(
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto? = null
)

@Serializable
data class EspnCompetitorDto(
    val id: String,
    val homeAway: String,
    val score: String? = null,
    val team: EspnTeamDto
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String,
    val logo: String? = null
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto
)

@Serializable
data class EspnStatusTypeDto(
    val name: String,
    val state: String,
    val completed: Boolean = false
)