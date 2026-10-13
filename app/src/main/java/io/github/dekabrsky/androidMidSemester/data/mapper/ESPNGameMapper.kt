import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnHeaderDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import java.time.OffsetDateTime
import java.time.ZoneId

fun EspnScoreboardDto.toMatches(): List<Match> =
    events.map { it.toMatch() }.sortedBy { it.kickoff }

fun EspnEventDto.toMatch(): Match {
    val competition = competitions.competitionFor(id)
    return competition.toMatch(league = competition.altGameNote.orEmpty())
}

fun EspnHeaderDto.toMatch(): Match {
    return competitions.competitionFor(id).toMatch(league = league.name)
}

fun List<EspnCompetitionDto>.competitionFor(eventId: String): EspnCompetitionDto =
    requireNotNull(singleOrNull { it.id == eventId }) {
        "ESPN: матч $eventId отсутствует или неоднозначен"
    }

private fun EspnCompetitionDto.toMatch(league: String): Match {
    val home = requireNotNull(competitors.singleOrNull { it.homeAway == "home" }) {
        "ESPN: не удалось определить хозяев матча $id"
    }
    val away = requireNotNull(competitors.singleOrNull { it.homeAway == "away" }) {
        "ESPN: не удалось определить гостей матча $id"
    }
    val hasStarted = status.type.state == "in" || status.type.completed

    return Match(
        id = id.toLong(),
        home = home.team.toTeam(),
        away = away.team.toTeam(),
        homeScore = home.score?.toIntOrNull().takeIf { hasStarted },
        awayScore = away.score?.toIntOrNull().takeIf { hasStarted },
        status = if (status.type.completed) MatchStatus.FINISHED else MatchStatus.SCHEDULED,
        kickoff = OffsetDateTime.parse(date)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        league = league,
        round = "",
    )
}

private fun EspnTeamDto.toTeam(): Team = Team(
    id = id.toLong(),
    name = displayName,
    logoUrl = logo?.takeIf { it.isNotBlank() }
        ?: logos.firstOrNull { it.href.isNotBlank() }?.href,
)