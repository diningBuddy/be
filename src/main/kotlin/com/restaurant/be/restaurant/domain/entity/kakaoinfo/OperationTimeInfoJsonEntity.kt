package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.restaurant.be.restaurant.repository.dto.OperationTimeInfoEsDocument

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

)

fun OperationTimeInfoEsDocument.toResponse(): OperationTimeInfoJsonEntity {
    return OperationTimeInfoJsonEntity(
        startTime = this.startTime,
        endTime = this.endTime,
        breakStartTime = this.breakStartTime,
        breakEndTime = this.breakEndTime,
        lastOrder = this.lastOrder
    )
}
