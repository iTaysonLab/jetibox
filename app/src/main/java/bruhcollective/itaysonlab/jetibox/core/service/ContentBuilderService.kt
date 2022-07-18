package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ContentBuilderService {
    @GET("/users/me/layouts/XboxMobileHome")
    @Headers("x-xbl-contract-version: 3")
    suspend fun getHomeLayout(
        @Query("ring") ring: String = "retail",
        @Query("deviceCategory") deviceCategory: String = "phone"
    ): ContentBuilderResponse
}