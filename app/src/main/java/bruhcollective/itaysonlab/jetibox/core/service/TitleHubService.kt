package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.titlehub.StoreBatchRequest
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.TitleHubResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TitleHubService {
    @POST("/titles/storeBatch/decoration/{fields}")
    @Headers("x-xbl-contract-version: 2")
    suspend fun storeBatch(
        @Path("fields") fields: String = "GamePass,Image",
        @Body body: StoreBatchRequest
    ): TitleHubResponse

    @POST("/users/xuid({xuid})/titles/titleId({titleId})/decoration/{fields}")
    @Headers("x-xbl-contract-version: 2")
    suspend fun personalTitleInfo(
        @Path("xuid") xuid: String,
        @Path("titleId") titleId: String,
        @Path("fields") fields: String = "GamePass,Achievement"
    ): TitleHubResponse
}