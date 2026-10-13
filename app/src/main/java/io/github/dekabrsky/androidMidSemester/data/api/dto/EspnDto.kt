package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnSummaryDto(val header: EspnEventDto? = null)

@Serializable
data class EspnLeagueDto(val name: String)

@Serializable
data class EspnEventDto(
    val id: String,
    val competitions: List<EspnCompetitionDto> = emptyList(),
    val status: EspnStatusDto? = null,
    val league: EspnLeagueDto? = null,
)

@Serializable
data class EspnCompetitionDto(
    val date: String,
    val competitors: List<EspnCompetitorDto>,
    val status: EspnStatusDto? = null,
)

@Serializable
data class EspnCompetitorDto(
    val homeAway: String,
    val team: EspnTeamDto,
    val score: String? = null,
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String,
    val logo: String? = null,
    val logos: List<EspnLogoDto> = emptyList(),
)

@Serializable
data class EspnLogoDto(val href: String)

@Serializable
data class EspnStatusDto(val type: EspnStatusTypeDto)

@Serializable
data class EspnStatusTypeDto(
    val state: String,
    val completed: Boolean = false,
)
