package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class EspnMapper {

    fun map(dto: EspnScoreboardDto): List<Match> {
        val defaultLeague = dto.leagues?.firstOrNull()?.name ?: "Российская Премьер-Лига"
        return dto.events?.mapNotNull { event ->
            mapEvent(event, defaultLeague)
        } ?: emptyList()
    }

    private fun mapEvent(event: EspnEventDto, leagueName: String): Match? {
        val competition = event.competitions?.firstOrNull() ?: return null
        val homeCompetitor = competition.competitors?.firstOrNull { it.homeAway == "home" } ?: return null
        val awayCompetitor = competition.competitors.firstOrNull { it.homeAway == "away" } ?: return null

        val isFinished = competition.status?.type?.completed == true ||
                competition.status?.type?.state == "post"

        val kickoff = parseKickoff(event.date)
        val round = event.week?.number?.let { "$it тур" } ?: "Турнир"

        return Match(
            id = event.id.toLongOrNull() ?: 0L,
            home = mapTeam(homeCompetitor),
            away = mapTeam(awayCompetitor),
            homeScore = homeCompetitor.score?.toIntOrNull(),
            awayScore = awayCompetitor.score?.toIntOrNull(),
            kickoff = kickoff,
            league = leagueName,
            round = round,
            status = if (isFinished) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        )
    }

    private fun mapTeam(competitor: EspnCompetitorDto): Team {
        return Team(
            id = competitor.team.id?.toLongOrNull() ?: 0L,
            name = competitor.team.displayName ?: competitor.team.name.orEmpty(),
            logoUrl = competitor.team.logo,
        )
    }

    private fun parseKickoff(dateStr: String): LocalDateTime {
        return runCatching {
            ZonedDateTime.parse(dateStr).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        }.getOrElse {
            runCatching {
                Instant.parse(dateStr).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }.getOrElse {
                LocalDateTime.now()
            }
        }
    }
}