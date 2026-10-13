package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardDto(
    @SerialName("events") val events: List<EspnEventDto> = emptyList(),
    @SerialName("leagues") val leagues: List<EspnLeagueDto> = emptyList(),
)

@Serializable
data class EspnEventDto(
    @SerialName("id") val id: String,
    @SerialName("date") val date: String,
    @SerialName("competitions") val competitions: List<EspnCompetitionDto> = emptyList(),
)

@Serializable
data class EspnCompetitionDto(
    @SerialName("competitors") val competitors: List<EspnCompetitorDto> = emptyList(),
    @SerialName("status") val status: EspnStatusDto? = null,
    @SerialName("notes") val notes: List<EspnNoteDto> = emptyList(),
)

@Serializable
data class EspnNoteDto(
    @SerialName("headline") val headline: String? = null,
)

@Serializable
data class EspnCompetitorDto(
    @SerialName("homeAway") val homeAway: String,
    @SerialName("score") val score: String? = null,
    @SerialName("team") val team: EspnTeamDto,
)

@Serializable
data class EspnTeamDto(
    @SerialName("id") val id: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("logo") val logo: String? = null,
)

@Serializable
data class EspnStatusDto(
    @SerialName("type") val type: EspnStatusTypeDto,
)

@Serializable
data class EspnStatusTypeDto(
    @SerialName("state") val state: String = "",
    @SerialName("completed") val completed: Boolean = false,
)

@Serializable
data class EspnLeagueDto(
    @SerialName("name") val name: String = "",
    @SerialName("slug") val slug: String = "",
)