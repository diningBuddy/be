package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfoJsonEntity(
    val startTime: String?,
    val endTime: String?,
    val breakStartTime: String?,
    val breakEndTime: String?,
    val lastOrder: String?,

) {
    companion object {
        fun create(
            startTime: String,
            endTime: String,
            breakStartTime: String,
            breakEndTime: String,
            lastOrder: String
        ): OperationTimeInfoJsonEntity {
            return OperationTimeInfoJsonEntity(
                startTime = startTime,
                endTime = endTime,
                breakStartTime = breakStartTime,
                breakEndTime = breakEndTime,
                lastOrder = lastOrder
            )
        }
    }
}
