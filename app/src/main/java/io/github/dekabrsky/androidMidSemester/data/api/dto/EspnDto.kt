package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardResponse(
    val events: List<EspnEvent> = emptyList(),
    val leagues: List<EspnLeague> = emptyList(),
)

@Serializable
data class EspnLeague(
    val name: String? = null,
    val abbreviation: String? = null,
)

@Serializable
data class EspnEvent(
    val id: String,
    val date: String,
    val name: String? = null,
    val shortName: String? = null,
    val competitions: List<EspnCompetition> = emptyList(),
    val week: EspnWeek? = null,
)

@Serializable
data class EspnWeek(
    val number: Int? = null,
)

@Serializable
data class EspnCompetition(
    val competitors: List<EspnCompetitor> = emptyList(),
    val status: EspnStatus? = null,
    val notes: List<EspnNote> = emptyList(),
)

@Serializable
data class EspnNote(
    val headline: String? = null,
)

@Serializable
data class EspnStatus(
    val type: EspnStatusType? = null,
)

@Serializable
data class EspnStatusType(
    val state: String? = null,       // "pre" | "in" | "post"
    val completed: Boolean = false,
    val description: String? = null,
)

@Serializable
data class EspnCompetitor(
    val id: String? = null,
    val homeAway: String? = null,    // "home" | "away"
    val score: String? = null,
    val team: EspnTeam? = null,
)

@Serializable
data class EspnTeam(
    val id: String? = null,
    val displayName: String? = null,
    val name: String? = null,
    val shortDisplayName: String? = null,
    val logo: String? = null,
)