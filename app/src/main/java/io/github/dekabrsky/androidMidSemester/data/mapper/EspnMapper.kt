
package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.*
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDateTime

private const val DEFAULT_LEAGUE = "Russian Premier League"

private fun EspnCompetitorDto.toTeam(): Team =
    Team(
        id = team.id.toLong(),
        name = team.displayName,
        logoUrl = team.logo
    )


private fun toDateTime(date: String): LocalDateTime =
    java.time.OffsetDateTime.parse(date)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()


private fun createMatch(
    id: Long,
    date: String,
    competition: EspnCompetitionDto,
    status: EspnStatusDto?,
    league: String
): Match {
    val home = competition.competitors.first {
        it.homeAway == "home"
    }

    val away = competition.competitors.first {
        it.homeAway == "away"
    }

    val currentStatus = status ?: competition.status
    val started = currentStatus?.type?.state != "pre" &&
            currentStatus != null

    return Match(
        id = id,
        home = home.toTeam(),
        away = away.toTeam(),
        homeScore = if (started) home.score?.toIntOrNull() else null,
        awayScore = if (started) away.score?.toIntOrNull() else null,
        status = if (currentStatus?.type?.completed == true) {
            MatchStatus.FINISHED
        } else {
            MatchStatus.SCHEDULED
        },
        kickoff = toDateTime(date),
        league = league,
        round = "Не указан"
    )
}

fun EspnEventDto.toMatch(league: String): Match {
    val competition = competitions.firstOrNull()
        ?: error("Нет данных о матче $id")

    return createMatch(
        id = id.toLong(),
        date = date,
        competition = competition,
        status = status,
        league = league
    )
}

fun EspnSummaryDto.toMatch(id: Long): Match {
    val competition = header.competitions.firstOrNull()
        ?: error("Нет данных о матче $id")

    val date = competition.date
        ?: error("Нет даты матча $id")

    return createMatch(
        id = id,
        date = date,
        competition = competition,
        status = competition.status,
        league = header.league?.name ?: DEFAULT_LEAGUE
    )
}
