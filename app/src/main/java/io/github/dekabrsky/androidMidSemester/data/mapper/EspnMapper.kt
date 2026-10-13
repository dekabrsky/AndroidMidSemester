package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

private fun EspnTeamDto.toTeam(): Team =
    Team(
        id = id.toLong(),
        name = displayName,
        logoUrl = logo
    )

fun EspnEventDto.toMatch(): Match {
    val competition = competitions.firstOrNull()
        ?: throw IllegalStateException("У матча $id нет competition")

    val home = competition.competitors
        .firstOrNull { it.homeAway == "home" }
        ?: throw IllegalStateException("У матча $id нет домашней команды")

    val away = competition.competitors
        .firstOrNull { it.homeAway == "away" }
        ?: throw IllegalStateException("У матча $id нет гостевой команды")

    val statusType = competition.status?.type

    val status = if (statusType?.completed == true) {
        MatchStatus.FINISHED
    } else {
        MatchStatus.SCHEDULED
    }

    val kickoff = OffsetDateTime.parse(date)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),
        status = status,
        kickoff = kickoff,
        league = "Russian Premier League",
        round = season?.slug ?: ""
    )
}