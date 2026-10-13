package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable


// полученные результат
@Serializable
data class EspnScoreboardDto(
    val leagues: List<EspnLeagueDto>? = null,
    val events: List<EspnEventDto>? = null,
)

// инфо о лиге
@Serializable
data class EspnLeagueDto(
    val name: String? = null,
)

// событие/матч
@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val competitions: List<EspnCompetitionDto>? = null,
    val week: EspnWeekDto? = null,
)

// инфо о туре
@Serializable
data class EspnWeekDto(
    val number: Int? = null,
)

// инфо о встрече
@Serializable
data class EspnCompetitionDto(
    val competitors: List<EspnCompetitorDto>? = null,
    val status: EspnStatusDto? = null,
)

// команда участник
@Serializable
data class EspnCompetitorDto(
    val homeAway: String, // "home" или "away"
    val score: String? = null,
    val team: EspnTeamDto,
)

// инфо о команде
@Serializable
data class EspnTeamDto(
    val id: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val logo: String? = null,
)

// статус матча
@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val state: String? = null, // "pre", "in", "post"
    val completed: Boolean? = null,
)