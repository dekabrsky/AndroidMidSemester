package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

fun EspnEventDto.toEspnMatch(leagueName: String): Match {
    val competition = competitions.firstOrNull()
        ?: throw IllegalArgumentException("Матч $id не содержит соревнование")
    val home = competition.competitors.firstOrNull { it.homeAway == "home" }
        ?: throw IllegalArgumentException("У матча $id не найдена домашняя команда")
    val away = competition.competitors.firstOrNull { it.homeAway == "away" }
        ?: throw IllegalArgumentException("У матча $id не найдена гостевая команда")

    return Match(
        id = id.toLong(),
        home = Team(home.team.id.toLong(), home.team.displayName, home.team.logo),
        away = Team(away.team.id.toLong(), away.team.displayName, away.team.logo),
        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),
        status = if (competition.status?.type?.state == "post") MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = leagueName,
        round = season?.year?.toString().orEmpty(),
    )
}
