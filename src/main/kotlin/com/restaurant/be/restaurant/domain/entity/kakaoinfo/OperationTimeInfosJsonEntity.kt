package com.restaurant.be.restaurant.domain.entity.kakaoinfo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.restaurant.be.restaurant.repository.dto.OperationTimeInfosEsDocument

@JsonIgnoreProperties(ignoreUnknown = true)
data class OperationTimeInfosJsonEntity(
    @JsonProperty("dayOfTheWeek")
    val dayOfTheWeek: String?,
    @JsonProperty("operationTimeInfo")
    val operationTimeInfo: OperationTimeInfoJsonEntity?

)

fun OperationTimeInfosEsDocument.toResponse(): OperationTimeInfosJsonEntity {
    return OperationTimeInfosJsonEntity(
        dayOfTheWeek = this.dayOfTheWeek,
        operationTimeInfo = this.operationTimeInfo.toResponse()
    )
}
