package bruhcollective.itaysonlab.jetibox.core.room

import androidx.room.Database
import androidx.room.RoomDatabase
import bruhcollective.itaysonlab.jetibox.core.room.dao.CollectionDao
import bruhcollective.itaysonlab.jetibox.core.room.models.RoomCollectionItem

@Database(entities = [RoomCollectionItem::class], version = 1, exportSchema = true)
abstract class JBDatabase: RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}