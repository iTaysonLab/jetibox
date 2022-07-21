package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Gameclip
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubQuery
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubSearchResponse
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Screenshot
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MediaHubService {
    @POST("/gameclips/search")
    @Headers("x-xbl-contract-version: 3")
    suspend fun searchGameclips(
        @Body query: MediaHubQuery
    ): MediaHubSearchResponse<Gameclip>

    @POST("/screenshots/search")
    @Headers("x-xbl-contract-version: 3")
    suspend fun searchScreenshots(
        @Body query: MediaHubQuery
    ): MediaHubSearchResponse<Screenshot>
}