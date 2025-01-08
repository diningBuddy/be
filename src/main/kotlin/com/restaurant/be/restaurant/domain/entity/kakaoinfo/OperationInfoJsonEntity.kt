package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationInfoJsonEntity(
    val appointment: String?,
    val delivery: String?,
    val packagee: String?
) {
    companion object {
        fun create(
            appointment: String = "N",
            delivery: String = "N",
            packagee: String = "N"
        ): OperationInfoJsonEntity {
            return OperationInfoJsonEntity(
                appointment = appointment,
                delivery = delivery,
                packagee = packagee
            )
        }
    }
}
