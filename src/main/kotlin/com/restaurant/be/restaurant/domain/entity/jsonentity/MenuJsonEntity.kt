package com.restaurant.be.restaurant.domain.entity.jsonentity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MenuJsonEntity(
    val menuName: String?,
    val price: Int,
    val description: String,
    val isRepresentative: Boolean,
    val imageUrl: String,
) {
    companion object {
        fun create(
            menuName: String ,
            price: Int ,
            description: String ,
            isRepresentative: Boolean ,
            imageUrl: String ,
        ): MenuJsonEntity {
            return MenuJsonEntity(
                menuName = menuName,
                price = price,
                description = description,
                isRepresentative = isRepresentative,
                imageUrl = imageUrl,
            )
        }
    }
}