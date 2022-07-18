package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.peoplehub.PeopleHubResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface PeopleHubService {
    @GET("/users/me/people/xuids({xuids})/decoration/{decorations}")
    @Headers("x-xbl-contract-version: 5")
    suspend fun getPeople(
        @Path("xuids") xuids: String,
        @Path("decorations") decorations: String = "detail,preferredColor,presenceDetail,multiplayerSummary"
    ): PeopleHubResponse
}