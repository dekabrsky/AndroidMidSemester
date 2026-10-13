package io.github.dekabrsky.androidMidSemester.data.api.dto.espn

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val events: List<EspnEventDto> = emptyList(),
    val leagues: List<EspnLeagueDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val name: String = "",
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val name: String = "",
    val season: EspnSeasonDto? = null,
    val week: EspnWeekDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
    val status: EspnStatusDto? = null,
)

@Serializable
data class EspnSeasonDto(
    val displayName: String? = null,
)

@Serializable
data class EspnWeekDto(
    val number: Int? = null,
)

@Serializable
data class EspnCompetitionDto(
    val competitors: List<EspnCompetitorDto> = emptyList(),
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
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val completed: Boolean = false,
    val state: String = "",
)