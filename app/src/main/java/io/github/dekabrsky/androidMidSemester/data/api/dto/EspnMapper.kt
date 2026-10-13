package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun EspnScoreboardDto.toMatches(): List<Match> {
    val leagueName = leagues.firstOrNull()?.name.orEmpty()
    return events.mapNotNull { it.toMatch(leagueName) }
}

private fun EspnEventDto.toMatch(leagueName: String): Match? {
    val competition = competitions.firstOrNull() ?: return null
    val home = competition.competitors.firstOrNull { it.homeAway == "home" } ?: return null
    val away = competition.competitors.firstOrNull { it.homeAway == "away" } ?: return null
    val matchId = id.toLongOrNull() ?: return null

    val type = competition.status.type
    val started = type.state != "pre"

    return Match(
        id = matchId,
        home = home.team.toTeam() ?: return null,
        away = away.team.toTeam() ?: return null,
        homeScore = if (started) home.score?.toIntOrNull() else null,
        awayScore = if (started) away.score?.toIntOrNull() else null,
        status = if (type.completed) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = parseKickoff(date),
        league = leagueName,
        round = "—" // в scoreboard ESPN тура нет
    )
}

private fun EspnTeamDto.toTeam(): Team? {
    val teamId = id.toLongOrNull() ?: return null
    return Team(id = teamId, name = displayName, logoUrl = logo)
}

// ESPN отдаёт "2026-10-11T11:00Z" (без секунд) — Instant.parse такое не любит
private fun parseKickoff(date: String): LocalDateTime =
    LocalDateTime.parse(date.removeSuffix("Z"))
        .atOffset(ZoneOffset.UTC)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()