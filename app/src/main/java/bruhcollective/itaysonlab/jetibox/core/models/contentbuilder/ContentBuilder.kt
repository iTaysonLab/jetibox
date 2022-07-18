package bruhcollective.itaysonlab.jetibox.core.models.contentbuilder

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.DefaultObject
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true)
class ContentBuilderResponse(
    val v: Int,
    val layout: List<ContentBuilderLayoutItem>
)

@JsonClass(generateAdapter = true)
class ContentBuilderLayoutItem(
    val channelType: String,
    val channelId: String,
    //
    val channelStyle: String? = null,
    val channelLabel: String? = null,
    val channelData: ContentBuilderLayoutItemData? = null
)

@JsonClass(generateAdapter = true)
class ContentBuilderLayoutItemData(
    val items: List<ContentBuilderDataItem>
)

@JsonClass(generateAdapter = true)
class ContentBuilderDataItem(
    val itemId: String,
    val itemLayers: List<ContentBuilderLayer>
)

@JsonClass(generateAdapter = true)
class ContentBuilderDataLayer(
    val layer: String,
    val dataType: String,
    val data: ContentBuilderDataLayerEntry,
)

@JsonClass(generateAdapter = true)
class ContentBuilderDataLayerEntry(
    val totalCount: Int,
)

//

@JsonClass(generateAdapter = true, generator = "sealed:dataType")
sealed class ContentBuilderLayer {
    abstract val layer: String

    @TypeLabel("title")
    @JsonClass(generateAdapter = true)
    data class GameTitle(override val layer: String, val data: GameTitleLayerData) : ContentBuilderLayer()

    @TypeLabel("label")
    @JsonClass(generateAdapter = true)
    data class Label(override val layer: String, val data: LabelLayerData) : ContentBuilderLayer()

    @TypeLabel("editorial")
    @JsonClass(generateAdapter = true)
    data class Editorial(override val layer: String, val data: EditorialLayerData) : ContentBuilderLayer()

    @DefaultObject
    object Unknown : ContentBuilderLayer() {
        override val layer = ""
    }
}

//

@JsonClass(generateAdapter = true)
class GameTitleLayerData(
    val totalCount: Int,
    val titles: List<Long>
)

@JsonClass(generateAdapter = true)
class LabelLayerData(
    val label: String
)

@JsonClass(generateAdapter = true)
class EditorialLayerData(
    val label: String,
    val action: String,
    val image: String
)