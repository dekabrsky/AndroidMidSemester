package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Контракт ESPN. Лига захардкожена (rus.1 — Российская Премьер-лига),
 * как и в примере запроса от бизнеса.
 */
interface EspnApi {

    /** @param dates дата в формате "yyyyMMdd" (например, "20261011"). */
    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun scoreboard(@Query("dates") dates: String): EspnScoreboardDto

    /** @param event идентификатор матча (ESPN event id). */
    @GET("apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun summary(@Query("event") event: Long): EspnSummaryDto
}
