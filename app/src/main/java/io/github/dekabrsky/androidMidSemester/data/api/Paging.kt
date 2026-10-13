package io.github.dekabrsky.androidMidSemester.data.api

const val PAGE_SIZE = 100
private const val MAX_PAGES = 20

/** Загружает страницы по offset/limit, пока очередная страница не окажется неполной. */
suspend fun <T> loadAllPages(loadPage: suspend (offset: Int, limit: Int) -> List<T>): List<T> {
    val result = mutableListOf<T>()
    repeat(MAX_PAGES) {
        val page = loadPage(result.size, PAGE_SIZE)
        result += page
        if (page.size < PAGE_SIZE) return result
    }
    return result
}
