package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope

private const val PAGE_SIZE = 100

internal suspend fun <T> loadSStatsPages(
    idOf: (T) -> Long,
    loadPage: suspend (offset: Int, limit: Int) -> ListEnvelope<T>,
): List<T> {
    val items = linkedMapOf<Long, T>()
    var offset = 0

    while (true) {
        val response = loadPage(offset, PAGE_SIZE)
        check(response.status == "OK") {
            response.message?.takeIf { it.isNotBlank() } ?: "Не удалось загрузить данные SStats"
        }

        val page = response.data
        val total = response.totalCount
        if (page.isEmpty()) {
            check(total == null || offset >= total) { "SStats вернул неполный список" }
            break
        }

        val previousSize = items.size
        page.forEach { items[idOf(it)] = it }
        check(items.size > previousSize) { "SStats повторно вернул ту же страницу" }
        offset += page.size

        if (total != null && offset >= total) break
        if (total == null && page.size < PAGE_SIZE) break
    }

    return items.values.toList()
}