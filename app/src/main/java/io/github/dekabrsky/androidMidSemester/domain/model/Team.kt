package io.github.dekabrsky.androidMidSemester.domain.model

data class Team(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)