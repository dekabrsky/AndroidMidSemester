
package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList()
)

@Serializable
data class EspnLeagueDto(
    val name: String = "Russian Premier League"
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val competitions: List<EspnCompetitionDto> = emptyList(),
    val status: EspnStatusDto? = null
)

@Serializable
data class EspnCompetitionDto(
    val date: String? = null,
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto? = null
)

@Serializable
data class EspnCompetitorDto(
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
    val completed: Boolean = false,
    val state: String = "pre"
)

@Serializable
data class EspnSummaryDto(
    val header: EspnHeaderDto
)

@Serializable
data class EspnHeaderDto(
    val competitions: List<EspnCompetitionDto> = emptyList(),
    val league: EspnLeagueDto? = null
)
