package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.Instant
import java.time.ZoneId

internal const val LOGO_URL_PATTERN = "https://sstats.net/assets/logos/%d.png"

private fun toTeam(dto: TeamDto): Team = Team(
    id = dto.id,
    name = dto.name,
    logoUrl = LOGO_URL_PATTERN.format(dto.id),
)

/**
 * DTO -> доменная модель.
 * - Промежуточные статусы ("Second Half" и т.п.) считаем несыгранным матчем,
 *   но текущий счёт live-игры при этом показываем.
 * - null в счёте означает, что матч ещё не начался.
 */
fun GameDto.toMatch(): Match = Match(
    id = id,
    home = toTeam(homeTeam),
    away = toTeam(awayTeam),
    homeScore = homeResult,
    awayScore = awayResult,
    status = if (statusName == "Finished") MatchStatus.FINISHED else MatchStatus.SCHEDULED,
    kickoff = Instant.ofEpochSecond(dateUtc).atZone(ZoneId.systemDefault()).toLocalDateTime(),
    league = season.league.name,
    round = roundName,
)
