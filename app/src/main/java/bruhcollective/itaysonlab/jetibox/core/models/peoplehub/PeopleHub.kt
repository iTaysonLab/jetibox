package bruhcollective.itaysonlab.jetibox.core.models.peoplehub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PeopleHubResponse(
    val people: List<XblPerson>
)