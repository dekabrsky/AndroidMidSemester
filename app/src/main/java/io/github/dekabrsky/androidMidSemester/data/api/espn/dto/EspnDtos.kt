package io.github.dekabrsky.androidMidSemester.data.api.espn.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    val events: List<EspnEventDto> = emptyList(),
)

@Serializable
data class EspnEventDto(
    val id: String,
    val date: String,
    val name: String? = null,
    val status: EspnStatusDto? = null,
    val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnStatusDto(
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    val name: String? = null,
)

@Serializable
data class EspnCompetitionDto(
    val competitors: List<EspnCompetitorDto> = emptyList(),
    @SerialName("venue") val venue: EspnVenueDto? = null,
)

@Serializable
data class EspnCompetitorDto(
    val id: String,
    val homeAway: String? = null,
    val score: String? = null,
    val team: EspnTeamDto? = null,
)

@Serializable
data class EspnTeamDto(
    val id: String,
    val displayName: String? = null,
    val logo: String? = null,
    val logos: List<EspnLogoDto> = emptyList(),
)

@Serializable
data class EspnLogoDto(
    val href: String? = null,
)

@Serializable
data class EspnVenueDto(
    @SerialName("fullName") val fullName: String? = null,
)