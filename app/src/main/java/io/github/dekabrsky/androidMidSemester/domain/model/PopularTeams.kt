package io.github.dekabrsky.androidMidSemester.domain.model

/**
 * 10 популярных клубов России, которые экран выбора показывает сразу.
 * Порядок списка задаёт порядок на экране. Числа это id команд в sstats
 * (их можно посмотреть в ответе Teams/list?Country=Russia).
 */
val POPULAR_TEAM_IDS: List<Long> = listOf(
    596L,  // Zenit
    558L,  // Spartak Moscow
    555L,  // CSKA Moscow
    597L,  // Lokomotiv
    1088L, // Dynamo
    621L,  // FC Krasnodar
    1083L, // Rubin
    779L,  // FC Rostov
    1085L, // Akhmat
    1084L, // Ural
)
