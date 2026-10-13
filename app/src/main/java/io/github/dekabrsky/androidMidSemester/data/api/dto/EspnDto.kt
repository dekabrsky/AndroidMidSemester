package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto>,
)

@Serializable
data class EspnSummaryDto(
    val header: EspnEventDto? = null,
)

@Serializable
data class EspnLeagueDto(
    val name: String = "",
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String? = null,
    val league: EspnLeagueDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnCompetitionDto(
    val date: String? = null,
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto? = null,
)

@Serializable
data class EspnCompetitorDto(
    val homeAway: String,
    val score: String? = null,
    val team: EspnTeamDto,
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String = "",
    val name: String = "",
    val shortDisplayName: String = "",
    val logo: String? = null,
    val logos: List<EspnLogoDto> = emptyList(),
)

@Serializable
data class EspnLogoDto(
    val href: String,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val completed: Boolean = false,
    val state: String = "",
)
