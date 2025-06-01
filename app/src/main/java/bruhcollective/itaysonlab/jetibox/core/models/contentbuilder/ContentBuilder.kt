package bruhcollective.itaysonlab.jetibox.core.models.contentbuilder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
class ContentBuilderResponse(
    val v: Int,
    val layout: List<ContentBuilderLayoutItem> = emptyList()
)

@Serializable
class ContentBuilderLayoutItem(
    val channelType: String,
    val channelId: String,
    //
    val channelStyle: String? = null,
    val channelLabel: String? = null,
    val channelData: ContentBuilderLayoutItemData? = null
)

@Serializable
class ContentBuilderLayoutItemData(
    val items: List<ContentBuilderDataItem> = emptyList()
)

@Serializable
class ContentBuilderDataItem(
    val itemId: String,
    val itemLayers: List<ContentBuilderLayer> = emptyList()
)

@Serializable
class ContentBuilderDataLayer(
    val layer: String,
    val dataType: String,
    val data: ContentBuilderDataLayerEntry,
)

@Serializable
class ContentBuilderDataLayerEntry(
    val totalCount: Int,
)

//

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("dataType")
sealed class ContentBuilderLayer {
    companion object {
        val serializerModule = SerializersModule {
            polymorphic(ContentBuilderLayer::class) {
                defaultDeserializer { Unknown.serializer() }
            }
        }
    }

    abstract val layer: String

    @SerialName("title")
    @Serializable
    data class GameTitle(override val layer: String, val data: GameTitleLayerData) : ContentBuilderLayer()

    @SerialName("label")
    @Serializable
    data class Label(override val layer: String, val data: LabelLayerData) : ContentBuilderLayer()

    @SerialName("editorial")
    @Serializable
    data class Editorial(override val layer: String, val data: EditorialLayerData) : ContentBuilderLayer()

    @Serializable
    data object Unknown : ContentBuilderLayer() {
        override val layer = ""
    }
}

//

@Serializable
class GameTitleLayerData(
    val totalCount: Int,
    val titles: List<Long> = emptyList()
)

@Serializable
class LabelLayerData(
    val label: String
)

@Serializable
class EditorialLayerData(
    val label: String,
    val action: String,
    val image: String
)