package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team

fun TeamDto.toDomain(): Team = Team(
    id = id,
    name = name,
    logoUrl = logoUrl,
)