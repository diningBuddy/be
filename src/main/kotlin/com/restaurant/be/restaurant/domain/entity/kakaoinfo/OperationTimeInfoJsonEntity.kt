package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfoJsonEntity(
    @JsonProperty("start_time")
    val startTime: String?,
    @JsonProperty("end_time")
    val endTime: String?,
    @JsonProperty("break_start_time")
    val breakStartTime: String?,
    @JsonProperty("break_end_time")
    val breakEndTime: String?,
    @JsonProperty("last_order")
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
