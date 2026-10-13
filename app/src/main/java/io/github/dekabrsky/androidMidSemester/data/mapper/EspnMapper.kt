package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun EspnScoreboardDto.toMatches(): List<Match> {
    val leagueName = leagues.firstOrNull()?.name.orEmpty()
    return events.mapNotNull { it.toMatchOrNull(leagueName) }
}

private fun EspnEventDto.toMatchOrNull(leagueName: String): Match? {
    val competition = competitions.firstOrNull() ?: return null
    val home = competition.competitors.firstOrNull { it.homeAway == "home" } ?: return null
    val away = competition.competitors.firstOrNull { it.homeAway == "away" } ?: return null

    val finished = competition.status?.type?.let {
        it.completed || it.state == "post"
    } ?: false

    return Match(
        id = id.toLongOrNull() ?: return null,
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = if (finished) home.score?.toIntOrNull() else null,
        awayScore = if (finished) away.score?.toIntOrNull() else null,
        status = if (finished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = parseEspnDate(date),
        league = leagueName,
        round = competition.notes.firstOrNull()?.headline.orEmpty(),
    )
}

private fun EspnTeamDto.toTeam() = Team(
    id = id.toLongOrNull() ?: 0L,
    name = displayName,
    logoUrl = logo,
)

private fun parseEspnDate(raw: String): LocalDateTime =
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()