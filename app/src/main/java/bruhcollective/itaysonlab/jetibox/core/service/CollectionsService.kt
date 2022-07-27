package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.collections.QueryCollectionResponse
import bruhcollective.itaysonlab.jetibox.core.models.collections.QueryCollectionsBody
import retrofit2.http.Body
import retrofit2.http.POST

interface CollectionsService {
    @POST("/v7.0/collections/query")
    suspend fun query(
        @Body body: QueryCollectionsBody
    ): QueryCollectionResponse
}