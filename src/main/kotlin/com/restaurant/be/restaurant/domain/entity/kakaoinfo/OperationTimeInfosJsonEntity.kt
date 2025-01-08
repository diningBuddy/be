package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfosJsonEntity(
    val dayOfTheWeek: String?,
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
