package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val events: List<EspnEventDto> = emptyList(),
    val leagues: List<EspnLeagueDto> = emptyList(),
)

@Serializable
data class EspnSummaryDto(
    val header: EspnEventDto? = null,
)

@Serializable
data class EspnLeagueDto(
    val name: String? = null,
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val season: EspnSeasonDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnSeasonDto(
    val year: Int? = null,
)

@Serializable
data class EspnCompetitionDto(
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
    @SerialName("displayName") val displayName: String,
    val logo: String? = null,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val state: String? = null,
)
