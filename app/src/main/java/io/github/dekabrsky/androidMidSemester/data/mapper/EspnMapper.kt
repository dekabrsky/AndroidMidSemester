package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

private const val HOME = "home"
private const val AWAY = "away"
private const val STATE_NOT_STARTED = "pre"

fun EspnScoreboardDto.toMatches(): List<Match> {
    val league = leagues.firstOrNull()
    return events
        .flatMap { event -> event.competitions }
        .map { competition ->
            competition.toMatch(
                league = league?.name.orEmpty(),
                round = league?.season?.type?.name.orEmpty(),
            )
        }
}

fun EspnSummaryDto.toMatch(): Match? =
    header.competitions.firstOrNull()?.toMatch(
        league = header.league?.name.orEmpty(),
        round = header.season?.name.orEmpty(),
    )

private fun EspnCompetitionDto.toMatch(league: String, round: String): Match {
    val home = competitors.first { it.homeAway == HOME }
    val away = competitors.first { it.homeAway == AWAY }
    val started = status.type.state != STATE_NOT_STARTED
    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = home.scoreIf(started),
        awayScore = away.scoreIf(started),
        status = if (status.type.completed) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = league,
        round = round,
    )
}

private fun EspnCompetitorDto.scoreIf(started: Boolean): Int? =
    if (started) score?.toIntOrNull() else null

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName,
    logoUrl = logo ?: logos.firstOrNull()?.href,
)
