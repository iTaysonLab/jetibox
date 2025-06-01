package bruhcollective.itaysonlab.jetibox.core.models.peoplehub

import kotlinx.serialization.Serializable

@Serializable
class PeopleHubResponse(
    val people: List<XblPerson> = emptyList()
)