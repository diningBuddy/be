package com.restaurant.be.restaurant.presentation.controller.dto.jsonentity

import com.restaurant.be.restaurant.domain.entity.kakaoinfo.FacilityInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfosJsonEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 식당 편의시설 정보 DTO
@Schema(
    description = "식당 편의시설 정보 DTO",
    title = "FacilityInfoDto"
)
data class FacilityInfoDto(
    @Schema(
        description = "와이파이 제공 여부",
        example = "true"
    )
    val wifi: Boolean? = null,

    @Schema(
        description = "반려동물 동반 가능 여부",
        example = "true"
    )
    val pet: Boolean? = null,

    @Schema(
        description = "주차 가능 여부",
        example = "true"
    )
    val parking: Boolean? = null,

    @Schema(
        description = "수유실 보유 여부",
        example = "true"
    )
    val nursery: Boolean? = null,

    @Schema(
        description = "흡연실 보유 여부",
        example = "true"
    )
    val smokingRoom: Boolean? = null,

    @Schema(
        description = "장애인 편의시설 보유 여부",
        example = "true"
    )
    val forDisabled: Boolean? = null
) {
    companion object {
        fun from(entity: FacilityInfoJsonEntity): FacilityInfoDto {
            return FacilityInfoDto(
                wifi = entity.wifi?.equals("Y"),
                pet = entity.pet?.equals("Y"),
                parking = entity.parking?.equals("Y"),
                nursery = entity.nursery?.equals("Y"),
                smokingRoom = entity.smokingRoom?.equals("Y"),
                forDisabled = entity.forDisabled?.equals("Y")
            )
        }
    }

    fun toJsonEntity(): FacilityInfoJsonEntity {
        return FacilityInfoJsonEntity(
            wifi = wifi?.let { if (it) "Y" else "N" },
            pet = pet?.let { if (it) "Y" else "N" },
            parking = parking?.let { if (it) "Y" else "N" },
            nursery = nursery?.let { if (it) "Y" else "N" },
            smokingRoom = smokingRoom?.let { if (it) "Y" else "N" },
            forDisabled = forDisabled?.let { if (it) "Y" else "N" }
        )
    }
}

@Schema(
    description = "영업 정보 DTO",
    title = "OperationInfoDto"
)
data class OperationInfoDto(
    @Schema(
        description = "예약 가능 여부",
        example = "true"
    )
    val appointment: Boolean? = null,

    @Schema(
        description = "배달 서비스 제공 여부",
        example = "true"
    )
    val delivery: Boolean? = null,

    @Schema(
        description = "포장 서비스 제공 여부",
        example = "true"
    )
    val packagee: Boolean? = null
) {
    companion object {
        fun from(entity: OperationInfoJsonEntity): OperationInfoDto {
            return OperationInfoDto(
                appointment = entity.appointment?.equals("Y"),
                delivery = entity.delivery?.equals("Y"),
                packagee = entity.packagee?.equals("Y")
            )
        }
    }

    fun toJsonEntity(): OperationInfoJsonEntity {
        return OperationInfoJsonEntity(
            appointment = appointment?.let { if (it) "Y" else "N" },
            delivery = delivery?.let { if (it) "Y" else "N" },
            packagee = packagee?.let { if (it) "Y" else "N" }
        )
    }
}

@Schema(
    description = "영업 시간 상세 정보 DTO",
    title = "OperationTimeDto"
)
data class OperationTimeDto(
    @Schema(
        description = "영업 시작 시간",
        example = "09:00",
        type = "string"
    )
    val startTime: LocalTime?,

    @Schema(
        description = "영업 종료 시간",
        example = "22:00",
        type = "string"
    )
    val endTime: LocalTime?,

    @Schema(
        description = "브레이크 타임 시작 시간",
        example = "15:00",
        type = "string"
    )
    val breakStartTime: LocalTime?,

    @Schema(
        description = "브레이크 타임 종료 시간",
        example = "17:00",
        type = "string"
    )
    val breakEndTime: LocalTime?,

    @Schema(
        description = "주문 마감 시간",
        example = "21:30",
        type = "string"
    )
    val lastOrder: LocalTime?
) {
    companion object {
        fun from(entity: OperationTimeInfoJsonEntity): OperationTimeDto {
            return OperationTimeDto(
                startTime = entity.startTime?.let { LocalTime.parse(it) },
                endTime = entity.endTime?.let { LocalTime.parse(it) },
                breakStartTime = entity.breakStartTime?.let { LocalTime.parse(it) },
                breakEndTime = entity.breakEndTime?.let { LocalTime.parse(it) },
                lastOrder = entity.lastOrder?.let { LocalTime.parse(it) }
            )
        }
    }

    fun toJsonEntity(): OperationTimeInfoJsonEntity {
        return OperationTimeInfoJsonEntity(
            startTime = startTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endTime = endTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            breakStartTime = breakStartTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            breakEndTime = breakEndTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            lastOrder = lastOrder?.format(DateTimeFormatter.ofPattern("HH:mm"))
        )
    }
}

@Schema(
    description = "요일별 영업 시간 정보 DTO",
    title = "DayOperationTimeDto"
)
data class DayOperationTimeDto(
    @Schema(
        description = "영업 요일",
        example = "MONDAY",
        allowableValues = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"]
    )
    val dayOfWeek: DayOfWeek,

    @Schema(
        description = "해당 요일의 영업 시간 정보"
    )
    val operationTime: OperationTimeDto?
) {
    companion object {
        fun from(entity: OperationTimeInfosJsonEntity): DayOperationTimeDto {
            return DayOperationTimeDto(
                dayOfWeek = when (entity.dayOfTheWeek) {
                    "월요일" -> DayOfWeek.MONDAY
                    "화요일" -> DayOfWeek.TUESDAY
                    "수요일" -> DayOfWeek.WEDNESDAY
                    "목요일" -> DayOfWeek.THURSDAY
                    "금요일" -> DayOfWeek.FRIDAY
                    "토요일" -> DayOfWeek.SATURDAY
                    "일요일" -> DayOfWeek.SUNDAY
                    else -> throw IllegalArgumentException("Invalid day of week: ${entity.dayOfTheWeek}")
                },
                operationTime = entity.operationTimeInfo?.let { OperationTimeDto.from(it) }
            )
        }
    }

    fun toJsonEntity(): OperationTimeInfosJsonEntity {
        return OperationTimeInfosJsonEntity(
            dayOfTheWeek = when (dayOfWeek) {
                DayOfWeek.MONDAY -> "월요일"
                DayOfWeek.TUESDAY -> "화요일"
                DayOfWeek.WEDNESDAY -> "수요일"
                DayOfWeek.THURSDAY -> "목요일"
                DayOfWeek.FRIDAY -> "금요일"
                DayOfWeek.SATURDAY -> "토요일"
                DayOfWeek.SUNDAY -> "일요일"
            },
            operationTimeInfo = operationTime?.toJsonEntity()
        )
    }
}
