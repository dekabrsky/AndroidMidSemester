package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import java.time.Instant
import java.time.ZoneId

fun GameDto.toMatch(): Match = Match(
    id = id,
    home = homeTeam.toTeam(),
    away = awayTeam.toTeam(),
    homeScore = homeResult,
    awayScore = awayResult,
    status = if (statusName == "Finished") MatchStatus.FINISHED else MatchStatus.SCHEDULED,
    kickoff = Instant.ofEpochSecond(dateUtc).atZone(ZoneId.systemDefault()).toLocalDateTime(),
    league = season.league.name,
    round = roundName,
)