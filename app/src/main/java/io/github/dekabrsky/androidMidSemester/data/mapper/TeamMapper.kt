package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team

fun TeamDto.toTeam(): Team = Team(
    id = id,
    name = name,
    logoUrl = logoUrl?.takeIf { it.isNotBlank() } ?: "https://sstats.net/assets/logos/$id.png",
    country = country?.name,
)
