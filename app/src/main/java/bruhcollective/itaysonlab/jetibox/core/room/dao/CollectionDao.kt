package bruhcollective.itaysonlab.jetibox.core.room.dao

import androidx.room.*
import bruhcollective.itaysonlab.jetibox.core.room.models.RoomCollectionItem

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections")
    suspend fun getAll(): List<RoomCollectionItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg items: RoomCollectionItem)

    @Delete
    suspend fun delete(item: RoomCollectionItem)
}