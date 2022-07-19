package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RatingBoardsData(
    @Json(name = "RatingBoards") val ratingBoards: Map<String, RatingBoard>
)

@JsonClass(generateAdapter = true)
class RatingBoard(
    @Json(name = "LocalizedProperties") val localizedProperties: List<RatingBoardLocalized>,
    @Json(name = "Ratings") val ratings: Map<String, Rating>
)

@JsonClass(generateAdapter = true)
class Rating(
    @Json(name = "Name") val name: String,
    @Json(name = "Age") val age: Int,
    @Json(name = "LocalizedProperties") val localized: List<RatingLocalized>
)

@JsonClass(generateAdapter = true)
class RatingLocalized(
    @Json(name = "ShortName") val shortName: String,
    @Json(name = "LongName") val longName: String,
    @Json(name = "Description") val description: String,
    @Json(name = "LogoUrl") val logoUrl: String?,
)

@JsonClass(generateAdapter = true)
class RatingBoardLocalized(
    @Json(name = "ShortName") val shortName: String,
    @Json(name = "LongName") val longName: String,
    @Json(name = "Url") val url: String,
    @Json(name = "Descriptors") val descriptors: List<RatingDescriptor>,
    @Json(name = "InteractiveElements") val interactiveElements: List<RatingInteractiveElement>,
)

@JsonClass(generateAdapter = true)
class RatingDescriptor(
    @Json(name = "Key") val key: String,
    @Json(name = "Descriptor") val descriptor: String,
    @Json(name = "LogoUrl") val logoUrl: String?
)

@JsonClass(generateAdapter = true)
class RatingInteractiveElement(
    @Json(name = "Key") val key: String,
    @Json(name = "InteractiveElement") val interactiveElement: String,
    @Json(name = "LogoUrl") val logoUrl: String?
)