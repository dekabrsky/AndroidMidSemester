package io.github.dekabrsky.androidMidSemester.data

enum class MatchDataProvider { ESPN, SSTATS }

object MatchDataConfig {
    // Источник можно сменить без изменений домена и экранов.
    val provider = MatchDataProvider.SSTATS
    const val ESPN_LEAGUE = "rus.1"
}
