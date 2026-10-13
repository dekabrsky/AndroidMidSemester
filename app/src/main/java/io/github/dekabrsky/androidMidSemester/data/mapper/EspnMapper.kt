package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName,
    logoUrl = logo ?: logos.firstOrNull()?.href,
)

/**
 * state у ESPN: "pre" — не начался, "in" — идёт, "post" — завершён.
 * Счёт приходит строкой и до начала матча равен "0", поэтому для "pre" его не берём.
 */
private fun EspnCompetitionDto.toMatch(id: String, date: String, league: String): Match {
    val home = competitors.first { it.homeAway == "home" }
    val away = competitors.first { it.homeAway == "away" }
    val state = status?.type?.state
    val started = state == "in" || state == "post"

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = if (started) home.score?.toIntOrNull() else null,
        awayScore = if (started) away.score?.toIntOrNull() else null,
        status = if (status?.type?.completed == true) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = league,
        round = week?.number?.toString() ?: "—",
    )
}

fun EspnEventDto.toMatch(league: String): Match =
    competitions.first().toMatch(id = id, date = date, league = league)

fun EspnSummaryDto.toMatch(id: Long): Match? {
    val competition = header?.competitions?.firstOrNull() ?: return null
    val date = competition.date ?: return null
    return competition.toMatch(
        id = id.toString(),
        date = date,
        league = header?.league?.name.orEmpty(),
    )
}
