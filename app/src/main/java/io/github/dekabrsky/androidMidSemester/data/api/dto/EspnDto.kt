package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(val name: String)

@Serializable
data class EspnEventDto(
    val id: String,
    val competitions: List<EspnCompetitionDto>,
)

@Serializable
data class EspnCompetitionDto(
    val date: String,
    val competitors: List<EspnCompetitorDto>,
    val status: EspnStatusDto,
)

@Serializable
data class EspnStatusDto(val type: EspnStatusTypeDto)

@Serializable
data class EspnStatusTypeDto(
    val state: String,
    val completed: Boolean = false,
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
    val displayName: String? = null,
    val name: String? = null,
    val logo: String? = null,
    val logos: List<EspnLogoDto> = emptyList(),
)

@Serializable
data class EspnLogoDto(val href: String)

@Serializable
data class EspnSummaryDto(val header: EspnHeaderDto? = null)

@Serializable
data class EspnHeaderDto(
    val id: String,
    val competitions: List<EspnCompetitionDto>,
    val league: EspnLeagueDto? = null,
)