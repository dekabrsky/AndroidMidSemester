package io.github.dekabrsky.androidMidSemester.data

enum class MatchDataProvider { ESPN, SSTATS }

object MatchDataConfig {
    // Для возврата к прежнему API достаточно выбрать SSTATS и перезапустить приложение.
    val provider = MatchDataProvider.ESPN
    const val ESPN_LEAGUE = "rus.1"
}