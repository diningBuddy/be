package com.restaurant.be.restaurant.presentation.controller.dto

import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import java.time.DayOfWeek

data class GetRestaurantsRequest(
    @Schema(title = "식당 이름 검색", example = "맛집", required = false)
    val query: String?,
    @Schema(title = "카테고리 필터", example = "['1', '2']", required = false)
    val categories: List<String>?,
    @Schema(title = "킹고패스 할인 여부 필터", example = "false", required = false)
    val discountForSkku: Boolean?,
    @Schema(title = "평점 필터", example = "4.5", required = false)
    val ratingAvg: Double?,
    @Schema(title = "리뷰 개수 필터", example = "100", required = false)
    val reviewCount: Int?,
    @Schema(title = "최소 가격 필터", example = "10000", required = false)
    val priceMin: Int?,
    @Schema(title = "최대 가격 필터", example = "30000", required = false)
    val priceMax: Int?,
    @Schema(title = "정렬 기준", example = "BASIC", required = false)
    val customSort: Sort = Sort.BASIC,
    @Schema(title = "식당 평점 개수 필터", example = "100", required = false)
    var ratingCount: Long?,
    @Schema(title = "와이파이 제공 여부", example = "true", required = false)
    val hasWifi: Boolean?,
    @Schema(title = "반려동물 동반 가능 여부", example = "true", required = false)
    val hasPet: Boolean?,
    @Schema(title = "주차 가능 여부", example = "true", required = false)
    val hasParking: Boolean?,
    @Schema(title = "수유실 보유 여부", example = "true", required = false)
    val hasNursery: Boolean?,
    @Schema(title = "흡연실 보유 여부", example = "true", required = false)
    val hasSmokingRoom: Boolean?,
    @Schema(title = "장애인 편의시설 보유 여부", example = "true", required = false)
    val hasDisabledFacility: Boolean?,
    @Schema(title = "예약 가능 여부", example = "true", required = false)
    val hasAppointment: Boolean?,
    @Schema(title = "배달 가능 여부", example = "true", required = false)
    val hasDelivery: Boolean?,
    @Schema(title = "포장 가능 여부", example = "true", required = false)
    val hasPackagee: Boolean?,
    @Schema(title = "영업 요일", example = "MONDAY", required = false)
    val operationDay: DayOfWeek?,
    @Schema(title = "영업 시작 시간", example = "09:00", required = false)
    val operationStartTime: String?,
    @Schema(title = "영업 종료 시간", example = "22:00", required = false)
    val operationEndTime: String?,
    @Schema(title = "영업 휴식 시작 시간", example = "15:00", required = false)
    val breakStartTime: String?,
    @Schema(title = "영업 휴식 종료 시간", example = "17:00", required = false)
    val breakEndTime: String?,
    @Schema(title = "영업 마지막 주문 시간", example = "21:30", required = false)
    val lastOrder: String?,
    @Schema(title = "카카오 평점 필터", example = "4.5", required = false)
    val kakaoRatingAvg: Double?,
    @Schema(title = "카카오 평점 개수 필터", example = "100", required = false)
    val kakaoRatingCount: Int?,
    @Schema(title = "찜 필터", example = "false", required = false)
    val bookmark: Boolean?,
    @Schema(title = "경도(거리순 정렬 할 때 사용)", example = "126.123456", required = false)
    val longitude: Double?,
    @Schema(title = "위도(거리순 정렬 할 때 사용)", example = "37.123456", required = false)
    val latitude: Double?
) {
}

enum class Sort {
    BASIC,
    CLOSELY_DESC,
    RATING_DESC,
    REVIEW_COUNT_DESC,
    BOOKMARK_COUNT_DESC,
    ID_ASC
}

data class GetRestaurantsResponse(
    @Schema(description = "식당 리스트")
    val restaurants: Page<RestaurantDto>,
    @Schema(description = "다음 페이지")
    val nextCursor: List<Double>?
)

data class GetRestaurantResponse(
    @Schema(description = "식당 정보")
    val restaurant: RestaurantDto
)
