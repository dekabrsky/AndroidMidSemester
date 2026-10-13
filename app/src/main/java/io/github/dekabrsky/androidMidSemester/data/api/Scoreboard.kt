package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class Scoreboard(
    val leagues: List<League> = emptyList(),
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class Summary(val header: EspnEventDto)

@Serializable
data class League(val name: String)

@Serializable
data class EspnEventDto(
    val id: String,
    val competitions: List<Competition>,
    val league: League? = null,
)

@Serializable
data class Competition(
    val date: String,
    val competitors: List<Competitor>,
    val status: Status,
)

@Serializable
data class Status(val type: StatusType)

@Serializable
data class StatusType(
    val completed: Boolean = false,
    val state: String = "pre",
)

@Serializable
data class Competitor(
    val homeAway: String,
    val score: String? = null,
    val team: TeamsDto,
)

@Serializable
data class TeamsDto(
    val id: String,
    val displayName: String,
    val logo: String? = null,
    val logos: List<Logo> = emptyList(),
)

@Serializable
data class Logo(val href: String)