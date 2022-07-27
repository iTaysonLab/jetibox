package bruhcollective.itaysonlab.jetibox.core.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class RoomCollectionItem(
    @PrimaryKey val productId: String,
    @ColumnInfo(name = "title_id") val titleId: Long,
    @ColumnInfo(name = "acquired_date") val acquiredDate: Long,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "modified_date") val modifiedDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    @ColumnInfo(name = "country") val purchasedCountry: String,
    @ColumnInfo(name = "kind") val productKind: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "trial") val isTrial: Boolean,
    @ColumnInfo(name = "trial_remaining") val trialTimeRemaining: String,
)