package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitor
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEvent
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardResponse
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

private const val HOME = "home"
private const val AWAY = "away"
private const val STATE_POST = "post"

fun EspnScoreboardResponse.toMatches(): List<Match> {
    val leagueName = leagues.firstOrNull()?.name.orEmpty()
    return events.map { it.toMatch(leagueName) }
}

fun EspnEvent.toMatch(leagueName: String): Match {
    val competition = competitions.firstOrNull()
    val home = competition?.competitors?.firstOrNull { it.homeAway == HOME }
    val away = competition?.competitors?.firstOrNull { it.homeAway == AWAY }

    val isFinished = competition?.status?.type?.let {
        it.completed || it.state == STATE_POST
    } == true

    return Match(
        id = id.toLongOrNull() ?: id.hashCode().toLong(),
        home = home.toTeam(),
        away = away.toTeam(),
        homeScore = home?.score?.toIntOrNull(),
        awayScore = away?.score?.toIntOrNull(),
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = leagueName,
        round = competition?.notes?.firstOrNull()?.headline
            ?: week?.number?.let { "Тур $it" }
            ?: "",
    )
}

private fun EspnCompetitor?.toTeam(): Team {
    val t = this?.team
    return Team(
        id = t?.id?.toLongOrNull() ?: t?.id?.hashCode()?.toLong() ?: 0L,
        name = t?.displayName ?: t?.name ?: "—",
        logoUrl = t?.logo,
    )
}