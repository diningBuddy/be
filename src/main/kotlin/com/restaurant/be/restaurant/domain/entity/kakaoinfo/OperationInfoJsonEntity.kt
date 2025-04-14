package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationInfoJsonEntity(
    val appointment: String?,
    val delivery: String?,
    @JsonProperty("package")
    val packagee: String?

)

data class OperationInfoResponse(
    val appointment: Boolean?,
    val delivery: Boolean?,
    val packagee: Boolean
)

fun OperationInfoJsonEntity.toResponse(): OperationInfoResponse =
    OperationInfoResponse(
        appointment = appointment.toBooleanYn(),
        delivery = delivery.toBooleanYn(),
        packagee = packagee.toBooleanYn()
    )
