package io.github.dekabrsky.androidMidSemester.data.api.espn.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val name: String,
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnCompetitionDto(
    val date: String? = null,
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto,
    val altGameNote: String = "",
)

@Serializable
data class EspnCompetitorDto(
    val id: String,
    val homeAway: String,
    val score: String? = null,
    val team: EspnTeamDto,
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String,
    val logo: String? = null,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto,
)

@Serializable
data class EspnStatusTypeDto(
    val completed: Boolean = false,
)

@Serializable
data class EspnSummaryDto(
    val header: EspnSummaryHeaderDto,
)

@Serializable
data class EspnSummaryHeaderDto(
    val id: String,
    val league: EspnLeagueDto,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)
