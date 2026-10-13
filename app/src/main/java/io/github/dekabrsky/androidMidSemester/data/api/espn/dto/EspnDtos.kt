package io.github.dekabrsky.androidMidSemester.data.api.espn.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val name: String? = null,
    val season: EspnLeagueSeasonDto? = null,
)

@Serializable
data class EspnLeagueSeasonDto(
    val displayName: String? = null,
)

@Serializable
data class EspnEventDto(
    val id: String,
    /** ISO-8601 без секунд, например "2026-10-11T11:00Z". */
    val date: String,
    val status: EspnStatusDto? = null,
    val week: EspnWeekDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnWeekDto(
    val number: Int? = null,
)

@Serializable
data class EspnCompetitionDto(
    val id: String? = null,
    val date: String? = null,
    val status: EspnStatusDto? = null,
    val notes: List<EspnNoteDto> = emptyList(),
    val competitors: List<EspnCompetitorDto> = emptyList(),
)

@Serializable
data class EspnNoteDto(
    val headline: String? = null,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val name: String? = null,
    val state: String? = null,
    val completed: Boolean = false,
)

@Serializable
data class EspnCompetitorDto(
    /** "home" или "away". */
    val homeAway: String,
    val score: String? = null,
    val team: EspnTeamDto,
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
data class EspnLogoDto(
    val href: String? = null,
)

@Serializable
data class EspnSummaryDto(
    val header: EspnSummaryHeaderDto? = null,
)

@Serializable
data class EspnSummaryHeaderDto(
    val id: String? = null,
    val league: EspnLeagueDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)
