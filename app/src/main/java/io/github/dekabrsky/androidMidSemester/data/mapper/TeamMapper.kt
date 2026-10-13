package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamListItemDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team

/** DTO команды из списка страны -> доменная модель. Если логотипа нет, строим ссылку по id. */
fun TeamListItemDto.toTeam(): Team = Team(
    id = id,
    name = name,
    logoUrl = logoUrl ?: "https://sstats.net/assets/logos/$id.png",
)
