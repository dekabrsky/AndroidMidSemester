package io.github.dekabrsky.androidMidSemester.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Конверт ответа `/Games/list`: в `data` лежит список матчей.
 */
@Serializable
data class ListEnvelope<T>(
    val status: String,
    val data: List<T> = emptyList(),
    @SerialName("TotalCount") val totalCount: Int? = null,
    val message: String? = null,
)

/**
 * Конверт ответа `/Games/{id}`: в `data` лежит одиночный объект (или ничего).
 */
@Serializable
data class SingleEnvelope<T>(val status: String, val data: T? = null)
