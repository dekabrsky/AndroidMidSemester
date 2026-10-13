package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
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

fun EspnEventDto.toMatch(leagueName: String): Match {
    val competition = competitions.first()
    val home = competition.competitors.first { it.homeAway == "home" }
    val away = competition.competitors.first { it.homeAway == "away" }
    val matchStatus = competition.status?.type ?: status?.type
    val hasStarted = matchStatus?.state == "in" || matchStatus?.state == "post"

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = if (hasStarted) home.score?.toIntOrNull() else null,
        awayScore = if (hasStarted) away.score?.toIntOrNull() else null,
        status = if (matchStatus?.completed == true) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(competition.date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = leagueName,
        round = "",
    )
}
