package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.espn.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.espn.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun EspnScoreboardDto.toMatches(): List<Match> {
    val defaultLeagueName = leagues.firstOrNull()?.name ?: "Soccer"
    return events.mapNotNull { event -> event.toMatch(defaultLeagueName) }
}

fun EspnEventDto.toMatch(leagueName: String): Match? {
    val competition = competitions.firstOrNull() ?: return null
    val homeCompetitor = competition.competitors.find { it.homeAway == "home" } ?: return null
    val awayCompetitor = competition.competitors.find { it.homeAway == "away" } ?: return null

    val isFinished = (competition.status?.type?.completed == true) ||
            (competition.status?.type?.state == "post")

    val isStarted = isFinished || (competition.status?.type?.state == "in")

    val homeScore = if (isStarted) homeCompetitor.score?.toIntOrNull() else null
    val awayScore = if (isStarted) awayCompetitor.score?.toIntOrNull() else null

    val kickoff = runCatching {
        Instant.parse(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }.getOrElse {
        LocalDateTime.now()
    }

    return Match(
        id = id.toLongOrNull() ?: id.hashCode().toLong(),
        home = Team(
            id = homeCompetitor.team.id.toLongOrNull() ?: homeCompetitor.team.id.hashCode().toLong(),
            name = homeCompetitor.team.displayName ?: homeCompetitor.team.name ?: "Home Team",
            logoUrl = homeCompetitor.team.logo,
        ),
        away = Team(
            id = awayCompetitor.team.id.toLongOrNull() ?: awayCompetitor.team.id.hashCode().toLong(),
            name = awayCompetitor.team.displayName ?: awayCompetitor.team.name ?: "Away Team",
            logoUrl = awayCompetitor.team.logo,
        ),
        homeScore = homeScore,
        awayScore = awayScore,
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = kickoff,
        league = leagueName,
        round = season?.slug ?: competition.altGameNote ?: "Regular Season",
    )
}
