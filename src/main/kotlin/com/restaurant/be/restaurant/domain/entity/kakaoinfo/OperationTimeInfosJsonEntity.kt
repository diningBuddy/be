package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfosJsonEntity(
    @JsonProperty("day_of_the_week")
    val dayOfTheWeek: String?,
    @JsonProperty("operation_time_info")
    val operationTimeInfo: OperationTimeInfoJsonEntity?

) {
    companion object {
        fun create(
            dayOfTheWeek: String,
            operationTimeInfo: OperationTimeInfoJsonEntity
        ): OperationTimeInfosJsonEntity {
            return OperationTimeInfosJsonEntity(
                dayOfTheWeek = dayOfTheWeek,
                operationTimeInfo = operationTimeInfo
            )
        }
    }
}
