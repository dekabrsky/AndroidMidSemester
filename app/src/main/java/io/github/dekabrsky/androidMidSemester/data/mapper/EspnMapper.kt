package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val HOME_AWAY_HOME = "home"
private const val HOME_AWAY_AWAY = "away"

/** ESPN отдаёт "pre" для ещё не начавшегося матча — счёт в этом случае не показываем. */
private const val STATE_PRE = "pre"

/**
 * ESPN отдаёт дату без секунд ("2026-10-11T11:00Z"), поэтому парсим своим форматом,
 * а не строгим [java.time.Instant.parse].
 */
private val ESPN_DATE_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]X")

/**
 * ESPN scoreboard -> доменная модель.
 *
 * @param leagueName название лиги (leagues[0].name).
 * @param seasonName запасной вариант для поля «тур»: у ESPN РПЛ нет отдельного номера тура,
 *   поэтому если в `notes` тура нет, показываем название сезона.
 */
fun EspnEventDto.toMatch(leagueName: String, seasonName: String?): Match {
    val competition = competitions.firstOrNull()
        ?: throw NoSuchElementException("У события ESPN $id нет competitions")
    return competition.toMatch(
        id = id.toLong(),
        league = leagueName,
        round = competition.roundOr(seasonName),
    )
}

/**
 * ESPN summary -> доменная модель (для запроса одного матча).
 */
fun EspnSummaryDto.toMatch(): Match {
    val header = header ?: throw NoSuchElementException("Пустой ответ ESPN summary")
    val competition = header.competitions.firstOrNull()
        ?: throw NoSuchElementException("В ответе ESPN summary нет competitions")
    return competition.toMatch(
        id = header.id.toLong(),
        league = header.league?.name.orEmpty(),
        round = competition.roundOr(header.season?.name),
    )
}

private fun EspnCompetitionDto.toMatch(
    id: Long,
    league: String,
    round: String,
): Match {
    val home = competitors.firstOrNull { it.homeAway == HOME_AWAY_HOME }
        ?: throw NoSuchElementException("В матче ESPN $id нет домашней команды")
    val away = competitors.firstOrNull { it.homeAway == HOME_AWAY_AWAY }
        ?: throw NoSuchElementException("В матче ESPN $id нет гостевой команды")

    val started = status?.type?.state != STATE_PRE
    val kickoff = date ?: throw NoSuchElementException("В матче ESPN $id нет даты")

    return Match(
        id = id,
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        // Счёт ESPN отдаёт всегда (даже "0" у неначавшихся матчей) — обнуляем его для "pre".
        homeScore = if (started) home.score?.toIntOrNull() else null,
        awayScore = if (started) away.score?.toIntOrNull() else null,
        status = if (status?.type?.completed == true) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(kickoff, ESPN_DATE_FORMAT)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = league,
        round = round,
    )
}

/** Тур: сначала пробуем `notes` (например, "Round of 16"), иначе название сезона. */
private fun EspnCompetitionDto.roundOr(seasonName: String?): String =
    notes.firstOrNull()?.headline?.takeIf { it.isNotBlank() }
        ?: seasonName.orEmpty()

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName,
    // ESPN отдаёт готовый URL логотипа, паттерн не нужен.
    logoUrl = logo,
)
