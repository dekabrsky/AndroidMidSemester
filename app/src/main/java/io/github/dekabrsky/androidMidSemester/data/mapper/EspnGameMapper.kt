package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnSummaryHeaderDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

fun EspnEventDto.toMatch(leagueName: String): Match? {
    val competition = competitions.firstOrNull() ?: return null
    val home = competition.competitors.firstOrNull { it.homeAway == "home" } ?: return null
    val away = competition.competitors.firstOrNull { it.homeAway == "away" } ?: return null
    val homeTeamId = home.team.id.toLongOrNull() ?: home.id.toLongOrNull() ?: return null
    val awayTeamId = away.team.id.toLongOrNull() ?: away.id.toLongOrNull() ?: return null

    return Match(
        id = id.toLongOrNull() ?: return null,
        home = home.toTeam(homeTeamId),
        away = away.toTeam(awayTeamId),
        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),
        status = if (competition.status.type.completed) {
            MatchStatus.FINISHED
        } else {
            MatchStatus.SCHEDULED
        },
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = leagueName,
        round = competition.altGameNote.ifBlank { "Тур не указан" },
    )
}

fun EspnSummaryHeaderDto.toMatch(): Match? {
    val competition = competitions.firstOrNull() ?: return null
    return EspnEventDto(
        id = id,
        date = competition.date ?: return null,
        competitions = listOf(competition),
    ).toMatch(league.name)
}

private fun EspnCompetitorDto.toTeam(teamId: Long): Team = Team(
    id = teamId,
    name = team.displayName,
    logoUrl = team.logo,
)
