package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

/**
 * DTO ответа ESPN scoreboard (`/scoreboard?dates=yyyyMMdd`).
 */
@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnLeagueDto(
    val name: String = "",
    val season: EspnSeasonDto? = null,
)

@Serializable
data class EspnSeasonDto(
    val name: String? = null,
    val displayName: String? = null,
)

@Serializable
data class EspnEventDto(
    val id: String = "",
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnCompetitionDto(
    val date: String? = null,
    val competitors: List<EspnCompetitorDto> = emptyList(),
    val status: EspnStatusDto? = null,
    val notes: List<EspnNoteDto> = emptyList(),
)

@Serializable
data class EspnCompetitorDto(
    val homeAway: String = "",
    val score: String? = null,
    val team: EspnTeamDto = EspnTeamDto(),
)

@Serializable
data class EspnTeamDto(
    val id: String = "",
    val displayName: String = "",
    val logo: String? = null,
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val state: String = "",
    val completed: Boolean = false,
)

@Serializable
data class EspnNoteDto(
    val headline: String? = null,
)

/**
 * DTO ответа ESPN summary (`/summary?event={id}`) — используется для одного матча.
 * Нужные данные лежат в `header`, остальное (boxscore, rosters, news и т.д.) игнорируется.
 */
@Serializable
data class EspnSummaryDto(
    val header: EspnHeaderDto? = null,
)

@Serializable
data class EspnHeaderDto(
    val id: String = "",
    val league: EspnLeagueRefDto? = null,
    val season: EspnSeasonDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnLeagueRefDto(
    val name: String = "",
)
