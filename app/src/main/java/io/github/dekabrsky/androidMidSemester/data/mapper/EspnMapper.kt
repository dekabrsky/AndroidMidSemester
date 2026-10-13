package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

/** Преобразование ESPN в те же доменные модели, которые использует экран матчей. */
fun EspnEventDto.toMatch(leagueName: String = league?.name.orEmpty()): Match {
    val competition = competitions.firstOrNull()
        ?: throw IllegalArgumentException("У матча ESPN $id нет данных соревнования")
    val home = competition.competitors.firstOrNull { it.homeAway == "home" }
        ?: throw IllegalArgumentException("У матча ESPN $id не указана домашняя команда")
    val away = competition.competitors.firstOrNull { it.homeAway == "away" }
        ?: throw IllegalArgumentException("У матча ESPN $id не указана гостевая команда")
    val kickoff = competition.date ?: date
        ?: throw IllegalArgumentException("У матча ESPN $id не указано время начала")
    val status = competition.status?.type
    val isFinished = status?.completed == true
    // ESPN возвращает "0" даже до начала матча. Для таких матчей сохраняем null.
    val hasScore = isFinished || status?.state == "in"

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = if (hasScore) home.score?.toIntOrNull() else null,
        awayScore = if (hasScore) away.score?.toIntOrNull() else null,
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(kickoff)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = leagueName.ifBlank { "Russian Premier League" },
        // В ответах scoreboard/summary РПЛ нет номера тура: не придумываем его.
        round = "Не указан",
    )
}

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName.ifBlank { name.ifBlank { shortDisplayName } },
    // В scoreboard используется logo, а в summary — массив logos.
    logoUrl = logo?.takeIf { it.isNotBlank() }
        ?: logos.firstOrNull { it.href.isNotBlank() }?.href,
)
