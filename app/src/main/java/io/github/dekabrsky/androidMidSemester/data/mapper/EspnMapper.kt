package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnHeaderDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

fun EspnEventDto.toMatch(league: String, zoneId: ZoneId = ZoneId.systemDefault()): Match =
    competitions.firstOrNull()?.toMatch(id, league, zoneId)
        ?: throw NoSuchElementException("У матча ESPN $id нет данных соревнования")

fun EspnHeaderDto.toMatch(zoneId: ZoneId = ZoneId.systemDefault()): Match =
    competitions.firstOrNull()?.toMatch(id, league?.name.orEmpty(), zoneId)
        ?: throw NoSuchElementException("У матча ESPN $id нет данных соревнования")

private fun EspnCompetitionDto.toMatch(id: String, league: String, zoneId: ZoneId): Match {
    val home = competitors.single { it.homeAway == "home" }
    val away = competitors.single { it.homeAway == "away" }
    val started = status.type.state == "in" || status.type.completed
    val homeScore = home.score?.toIntOrNull()
    val awayScore = away.score?.toIntOrNull()
    val hasScore = started && homeScore != null && awayScore != null

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = homeScore.takeIf { hasScore },
        awayScore = awayScore.takeIf { hasScore },
        status = if (status.type.completed) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date).atZoneSameInstant(zoneId).toLocalDateTime(),
        league = league,
        // Scoreboard и summary этой лиги не передают номер тура. Не подменяем его сезоном.
        round = "",
    )
}

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName?.takeIf { it.isNotBlank() }
        ?: name?.takeIf { it.isNotBlank() }
        ?: throw NoSuchElementException("У команды ESPN $id нет названия"),
    logoUrl = logo?.takeIf { it.isNotBlank() }
        ?: logos.firstOrNull { it.href.isNotBlank() }?.href,
)