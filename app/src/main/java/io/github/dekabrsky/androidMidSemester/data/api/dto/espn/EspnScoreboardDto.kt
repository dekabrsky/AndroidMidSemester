package io.github.dekabrsky.androidMidSemester.data.api.dto.espn

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val id: String? = null,
    val name: String? = null,
    val abbreviation: String? = null,
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val name: String? = null,
    val season: EspnSeasonDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnSeasonDto(
    val year: Int? = null,
    val slug: String? = null,
)

@Serializable
data class EspnCompetitionDto(
    val id: String,
    val date: String? = null,
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto? = null,
    val altGameNote: String? = null,
)

@Serializable
data class EspnCompetitorDto(
    val id: String,
    val homeAway: String, // "home" или "away"
    val score: String? = null,
    val team: EspnTeamDto,
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val name: String? = null,
    val displayName: String? = null,
    val logo: String? = null,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val id: String? = null,
    val name: String? = null,
    val state: String? = null, // "post", "in", "pre"
    val completed: Boolean? = null,
    val description: String? = null,
)
