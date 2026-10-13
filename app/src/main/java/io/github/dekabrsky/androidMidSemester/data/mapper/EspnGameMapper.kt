package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Маппер ESPN DTO → доменная модель.
 *
 * ESPN отдаёт дату в ISO 8601 ("2026-10-11T15:00Z"). Парсим через Instant,
 * потом переводим в локальную зону.
 */
fun EspnEventDto.toMatch(): Match? {
    val competition = competitions.firstOrNull() ?: return null
    val competitors = competition.competitors
    if (competitors.size < 2) return null

    val home = competitors.firstOrNull { it.homeAway == "home" } ?: return null
    val away = competitors.firstOrNull { it.homeAway == "away" } ?: return null

    val statusName = status?.type?.name.orEmpty()
    val isFinished = statusName == "STATUS_FINAL" || statusName == "STATUS_FULL_TIME"

    return Match(
        id = id.toLongOrNull() ?: return null,
        home = home.toTeam(),
        away = away.toTeam(),
        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = parseKickoff(date),
        league = "Российская Премьер-лига",
        round = "", // у ESPN в scoreboard тур не всегда есть; можно расширить позже
    )
}

private fun EspnCompetitorDto.toTeam(): Team = Team(
    id = team?.id?.toLongOrNull() ?: id.toLongOrNull() ?: 0L,
    name = team?.displayName ?: "Unknown",
    logoUrl = team?.logo ?: team?.logos?.firstOrNull()?.href,
)

private fun parseKickoff(isoDate: String): LocalDateTime =
    OffsetDateTime.parse(isoDate)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()