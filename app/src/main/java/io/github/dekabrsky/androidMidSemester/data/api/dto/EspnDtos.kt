package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val name: String,
    val season: EspnLeagueSeasonDto? = null,
)

@Serializable
data class EspnLeagueSeasonDto(
    val type: EspnSeasonTypeDto? = null,
)

@Serializable
data class EspnSeasonTypeDto(
    val name: String,
)

@Serializable
data class EspnEventDto(
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnCompetitionDto(
    val id: String,
    val date: String,
    val status: EspnStatusDto,
    val competitors: List<EspnCompetitorDto>,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto,
)

@Serializable
data class EspnStatusTypeDto(
    val state: String,
    val completed: Boolean = false,
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
    val displayName: String,
    val logo: String? = null,
    val logos: List<EspnLogoDto> = emptyList(),
)

@Serializable
data class EspnLogoDto(
    val href: String,
)

@Serializable
data class EspnSummaryDto(
    val header: EspnHeaderDto,
)

@Serializable
data class EspnHeaderDto(
    val competitions: List<EspnCompetitionDto> = emptyList(),
    val league: EspnHeaderLeagueDto? = null,
    val season: EspnHeaderSeasonDto? = null,
)

@Serializable
data class EspnHeaderLeagueDto(
    val name: String,
)

@Serializable
data class EspnHeaderSeasonDto(
    val name: String? = null,
)