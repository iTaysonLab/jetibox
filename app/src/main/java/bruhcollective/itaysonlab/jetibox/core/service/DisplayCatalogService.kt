package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.ProductFamiliesData
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoardsData
import retrofit2.http.GET
import retrofit2.http.Query

interface DisplayCatalogService {
    @GET("/v7.0/ratings")
    suspend fun getRatings(
        @Query("market") market: String,
        @Query("languages") languages: String
    ): RatingBoardsData

    @GET("/v7.0/productFamilies/Games")
    suspend fun getProductFamiliesForGames(
        @Query("market") market: String,
        @Query("languages") languages: String
    ): ProductFamiliesData
}