package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.SStatsTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team

/** DTO команды из `/Teams/list` -> доменная модель. Если sstats не отдал логотип — строим по id. */
fun SStatsTeamDto.toTeam(): Team = Team(
    id = id,
    name = name,
    logoUrl = logoUrl ?: LOGO_URL_PATTERN.format(id),
)
