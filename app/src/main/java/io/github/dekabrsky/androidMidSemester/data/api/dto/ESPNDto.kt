package io.github.dekabrsky.androidMidSemester.data.api.dto

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class EspnScoreboardResponse(
    val leagues: List<EspnLeague> = emptyList(),
    val events: List<EspnEvent> = emptyList()
)

@Serializable
data class EspnLeague(val name: String)

@Serializable
data class EspnEvent(
    val id: String,
    val date: String, // "2023-10-28T11:00Z"
    val season: EspnSeason,
    val competitions: List<EspnCompetition>
)

@Serializable
data class EspnSeason(val year: Int, val slug: String)

@Serializable
data class EspnCompetition(
    val status: EspnStatus,
    val competitors: List<EspnCompetitor>
)

@Serializable
data class EspnStatus(val type: EspnStatusType)

@Serializable
data class EspnStatusType(
    val name: String, // "STATUS_FULL_TIME", "STATUS_IN_PROGRESS" и т.д.
    val completed: Boolean
)

@Serializable
data class EspnCompetitor(
    val team: EspnTeam,
    val homeAway: String, // "home" или "away"
    val score: String? // ESPN возвращает счёт как String
)

@Serializable
data class EspnTeam(
    val id: String,
    val displayName: String,
    val logo: String
)

// --- DTO для одиночного матча (Summary) ---
@Serializable
data class EspnSummaryResponse(
    val header: EspnSummaryHeader? = null,
    val boxscore: EspnBoxscore? = null
)

@Serializable
data class EspnSummaryHeader(
    val id: String,
    val date: String,
    val competitions: List<EspnCompetition>
)

@Serializable
data class EspnBoxscore(
    val teams: List<EspnCompetitor>
)

// --- Функции маппинга в доменную модель ---

fun EspnEvent.toMatch(leagueName: String): Match {
    val competition = competitions.firstOrNull()
    val homeComp = competition?.competitors?.find { it.homeAway == "home" }
    val awayComp = competition?.competitors?.find { it.homeAway == "away" }

    val homeTeam = homeComp?.let {
        Team(
            it.team.id.toLongOrNull() ?: 0L,
            it.team.displayName,
            it.team.logo
        )
    }
        ?: Team(0L, "Неизвестно", "")
    val awayTeam = awayComp?.let { Team(it.team.id.toLongOrNull() ?: 0L, it.team.displayName, it.team.logo) }
        ?: Team(0L, "Неизвестно", "")

    val homeScore = homeComp?.score?.toIntOrNull()
    val awayScore = awayComp?.score?.toIntOrNull()

    val statusType = competition?.status?.type?.name ?: ""
    val isFinished = statusType.contains("FULL_TIME", ignoreCase = true) ||
            statusType.contains("FINAL", ignoreCase = true) ||
            (competition?.status?.type?.completed == true)

    val kickoff = try {
        LocalDateTime.parse(date.replace("Z", "+00:00"), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        LocalDateTime.now()
    }

    return Match(
        id = id.toLongOrNull() ?: 0L,
        home = homeTeam,
        away = awayTeam,
        homeScore = homeScore,
        awayScore = awayScore,
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = kickoff,
        league = leagueName,
        round = "Сезон ${season.year}" // ESPN не всегда отдает тур в scoreboard, используем год
    )
}

fun EspnSummaryResponse.toMatch(): Match {
    val header = header ?: throw NoSuchElementException("Нет данных о матче")
    val competition = header.competitions.firstOrNull()

    val homeBox = boxscore?.teams?.find { it.homeAway == "home" }
    val awayBox = boxscore?.teams?.find { it.homeAway == "away" }

    val homeTeam = homeBox?.let { Team(it.team.id.toLongOrNull() ?: 0L, it.team.displayName, it.team.logo) }
        ?: Team(0L, "Неизвестно", "")
    val awayTeam = awayBox?.let { Team(it.team.id.toLongOrNull() ?: 0L, it.team.displayName, it.team.logo) }
        ?: Team(0L, "Неизвестно", "")

    val statusType = competition?.status?.type?.name ?: ""
    val isFinished = statusType.contains("FULL_TIME", ignoreCase = true) || competition?.status?.type?.completed == true

    val kickoff = try {
        LocalDateTime.parse(header.date.replace("Z", "+00:00"), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        LocalDateTime.now()
    }

    return Match(
        id = header.id.toLongOrNull() ?: 0L,
        home = homeTeam,
        away = awayTeam,
        homeScore = homeBox?.score?.toIntOrNull(),
        awayScore = awayBox?.score?.toIntOrNull(),
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = kickoff,
        league = "Russian Premier League",
        round = "Тур"
    )
}