package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Конверт ответа `/Games/list`: в `data` лежит список матчей.
 */
@Serializable
data class ListEnvelope<T>(
    val status: String,
    val data: List<T> = emptyList(),
    @SerialName("TotalCount") val totalCount: Int? = null,
)

/**
 * Конверт ответа `/Games/{id}`: в `data` лежит одиночный объект (или ничего).
 */
@Serializable
data class SingleEnvelope<T>(val status: String, val data: T? = null)
