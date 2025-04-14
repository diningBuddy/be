package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfoJsonEntity(
    @JsonProperty("startTime")
    val startTime: String?,
    @JsonProperty("endTime")
    val endTime: String?,
    @JsonProperty("breakStartTime")
    val breakStartTime: String?,
    @JsonProperty("breakEndTime")
    val breakEndTime: String?,
    @JsonProperty("lastOrder")
    val lastOrder: String?

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
