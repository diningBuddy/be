package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FacilityInfoJsonEntity(
    val wifi: String,
    val pet: String,
    val parking: String,
    val nursery: String,
    val smokingRoom: String,
    val forDisabled: String
) {
    companion object {
        fun create(
            wifi: String = "N",
            pet: String = "N",
            parking: String = "N",
            nursery: String = "N",
            smokingRoom: String = "N",
            forDisabled: String = "N"
        ): FacilityInfoJsonEntity {
            return FacilityInfoJsonEntity(
                wifi = wifi,
                pet = pet,
                parking = parking,
                nursery = nursery,
                smokingRoom = smokingRoom,
                forDisabled = forDisabled
            )
        }
    }
}
