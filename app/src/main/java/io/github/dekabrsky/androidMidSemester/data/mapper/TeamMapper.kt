package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamListItemDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team

fun TeamListItemDto.toTeam(): Team = Team(
    id = id,
    name = name,
    logoUrl = logoUrl,
)
