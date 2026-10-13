package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnLeagueDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnStatusDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnSummaryDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

private const val STATE_NOT_STARTED = "pre"
private const val STATE_FINISHED = "post"

fun EspnEventDto.toMatch(league: EspnLeagueDto?): Match {
    val competition = competitions.firstOrNull()
        ?: throw IllegalStateException("ESPN: у события $id нет competitions")
    return competition.toMatch(
        eventId = id,
        fallbackDate = date,
        fallbackStatus = status,
        league = league,
        weekNumber = week?.number,
    )
}

fun EspnSummaryDto.toMatch(eventId: Long): Match {
    val header = header ?: throw NoSuchElementException("Матч $eventId не найден")
    val competition = header.competitions.firstOrNull()
        ?: throw NoSuchElementException("Матч $eventId не найден")
    return competition.toMatch(
        eventId = header.id ?: eventId.toString(),
        fallbackDate = null,
        fallbackStatus = null,
        league = header.league,
        weekNumber = null,
    )
}

private fun EspnCompetitionDto.toMatch(
    eventId: String,
    fallbackDate: String?,
    fallbackStatus: EspnStatusDto?,
    league: EspnLeagueDto?,
    weekNumber: Int?,
): Match {
    val home = competitors.firstOrNull { it.homeAway == "home" }
        ?: throw IllegalStateException("ESPN: у события $eventId нет хозяев")
    val away = competitors.firstOrNull { it.homeAway == "away" }
        ?: throw IllegalStateException("ESPN: у события $eventId нет гостей")

    val statusType = (status ?: fallbackStatus)?.type
    val notStarted = statusType?.state == STATE_NOT_STARTED
    val finished = statusType?.completed == true || statusType?.state == STATE_FINISHED

    return Match(
        id = eventId.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = home.scoreOrNull(notStarted),
        awayScore = away.scoreOrNull(notStarted),
        status = if (finished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = parseKickoff(date ?: fallbackDate),
        league = league?.name.orEmpty(),
        round = roundName(weekNumber, league),
    )
}

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName ?: name ?: "—",
    logoUrl = logo ?: logos.firstOrNull()?.href,
)

private fun EspnCompetitorDto.scoreOrNull(notStarted: Boolean): Int? =
    if (notStarted) null else score?.toIntOrNull()

private fun EspnCompetitionDto.roundName(weekNumber: Int?, league: EspnLeagueDto?): String =
    when {
        weekNumber != null -> "Тур $weekNumber"
        !notes.firstOrNull()?.headline.isNullOrBlank() -> notes.first().headline!!
        !league?.season?.displayName.isNullOrBlank() -> "Сезон ${league?.season?.displayName}"
        else -> "—"
    }

private fun parseKickoff(raw: String?): LocalDateTime {
    requireNotNull(raw) { "ESPN: у события нет даты" }
    return OffsetDateTime.parse(raw)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
}
