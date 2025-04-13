package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class FacilityInfoJsonEntity(
    val wifi: String?,
    val pet: String?,
    val parking: String?,
    val nursery: String?,
    @JsonProperty("smokingRoom")
    val smokingRoom: String?,
    @JsonProperty("forDisabled")
    val forDisabled: String?

)

data class FacilityInfoResponse(
    val wifi: Boolean?,
    val pet: Boolean?,
    val parking: Boolean,
    val nursery: Boolean,
    val forDisabled: Boolean,
    val smokingRoom: Boolean
)

fun FacilityInfoJsonEntity.toResponse(): FacilityInfoResponse =
    FacilityInfoResponse(
        wifi = wifi.toBooleanYn(),
        pet = pet.toBooleanYn(),
        parking = parking.toBooleanYn(),
        nursery = nursery.toBooleanYn(),
        forDisabled = forDisabled.toBooleanYn(),
        smokingRoom = smokingRoom.toBooleanYn()
    )

fun String?.toBooleanYn(): Boolean = this == "Y"
