package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.achievements.AchievementsResponse
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoardsData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AchievementsService {
    @GET("/users/xuid({xuid})/achievements")
    suspend fun getAchievements(
        @Path("xuid") xuid: String,
        @Query("titleId") titleId: String,
        @Query("maxItems") maxItems: Int
    ): AchievementsResponse
}