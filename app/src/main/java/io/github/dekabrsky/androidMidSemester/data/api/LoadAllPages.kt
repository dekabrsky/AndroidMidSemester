package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope

suspend fun <T> loadAllPages(
    pageSize: Int = 100,
    load: suspend (offset: Int, limit: Int) -> ListEnvelope<T>,
): List<T> {
    val items = mutableListOf<T>()
    var offset = 0
    while (true) {
        val page = load(offset, pageSize)
        items.addAll(page.data)
        offset += page.data.size
        if (page.data.isEmpty()) break
        val total = page.totalCount
        if (total != null && offset >= total) break
        if (total == null && page.data.size < pageSize) break
    }
    return items
}
