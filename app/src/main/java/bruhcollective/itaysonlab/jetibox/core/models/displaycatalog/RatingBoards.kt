package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RatingBoardsData(
    @SerialName(value = "RatingBoards") val ratingBoards: Map<String, RatingBoard>
)

@Serializable
class RatingBoard(
    @SerialName(value = "LocalizedProperties") val localizedProperties: List<RatingBoardLocalized> = emptyList(),
    @SerialName(value = "Ratings") val ratings: Map<String, Rating>,
    @SerialName(value = "MarketProperties") val markets: List<RatingBoardMarket> = emptyList()
)

@Serializable
class RatingBoardMarket(
    @SerialName(value = "Priority") val priority: Int,
    @SerialName(value = "Market") val market: String,
)

@Serializable
class Rating(
    @SerialName(value = "Name") val name: String,
    @SerialName(value = "Age") val age: Int,
    @SerialName(value = "LocalizedProperties") val localized: List<RatingLocalized> = emptyList()
)

@Serializable
class RatingLocalized(
    @SerialName(value = "ShortName") val shortName: String,
    @SerialName(value = "LongName") val longName: String,
    @SerialName(value = "Description") val description: String,
    @SerialName(value = "LogoUrl") val logoUrl: String? = null,
)

@Serializable
class RatingBoardLocalized(
    @SerialName(value = "ShortName") val shortName: String,
    @SerialName(value = "LongName") val longName: String,
    @SerialName(value = "Url") val url: String,
    @SerialName(value = "Descriptors") val descriptors: List<RatingDescriptor> = emptyList(),
    @SerialName(value = "InteractiveElements") val interactiveElements: List<RatingInteractiveElement> = emptyList(),
)

@Serializable
class RatingDescriptor(
    @SerialName(value = "Key") val key: String,
    @SerialName(value = "Descriptor") val descriptor: String,
    @SerialName(value = "LogoUrl") val logoUrl: String? = null
)

@Serializable
class RatingInteractiveElement(
    @SerialName(value = "Key") val key: String,
    @SerialName(value = "InteractiveElement") val interactiveElement: String,
    @SerialName(value = "LogoUrl") val logoUrl: String? = null
)