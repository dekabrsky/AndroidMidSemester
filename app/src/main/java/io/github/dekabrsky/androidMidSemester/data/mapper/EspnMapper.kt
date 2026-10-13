package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.espn.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.espn.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.Instant
import java.time.ZoneId
import java.time.OffsetDateTime

private fun EspnTeamDto.toDomainTeam(): Team = Team(
    id = id.toLongOrNull() ?: 0L,
    name = displayName,
    logoUrl = logo,
)

fun EspnEventDto.toMatchOrNull(): Match? {
    val competitors = competitions.firstOrNull()?.competitors
        ?: return null

    val home = competitors.firstOrNull { it.homeAway == "home" }
        ?: return null

    val away = competitors.firstOrNull { it.homeAway == "away" }
        ?: return null

    val kickoff = runCatching {
        OffsetDateTime.parse(date).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }.getOrNull() ?: return null

    return Match(
        id = id.toLongOrNull() ?: return null,
        home = home.team.toDomainTeam(),
        away = away.team.toDomainTeam(),
        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),
        status = if (status?.type?.completed == true) {
            MatchStatus.FINISHED
        } else {
            MatchStatus.SCHEDULED
        },
        kickoff = kickoff,
        league = "Российская Премьер-лига",
        round = week?.number?.let { "Тур $it" } ?: "",
    )
}