package com.restaurant.be.restaurant.presentation.controller.dto

import com.restaurant.be.restaurant.domain.entity.jsonentity.MenuJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.FacilityInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfosJsonEntity
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

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
    @Schema(title = "식당 편의 정보 필터", example = "example", required = false)
    var facilityInfos: FacilityInfoJsonEntity?,
    @Schema(title = "식당 운영 정보 필터", example = "APPOINTMENT", required = false)
    var operationInfos: OperationInfoJsonEntity?,
    @Schema(title = "식당 운영 시간 필터", example = "잠깐 뭔가 JSON형태여야하는데..?", required = false)
    var operationTimes: List<OperationTimeInfosJsonEntity>?,
    @Schema(title = "카카오 평점 필터", example = "4.5", required = false)
    val kakaoRatingAvg: Double?,
    @Schema(title = "카카오 평점 개수 필터", example = "100", required = false)
    val kakaoRatingCount: Int?,
    @Schema(title = "메뉴 Json리스트", example = "100", required = false)
    val menus: List<MenuJsonEntity>?,
    @Schema(title = "찜 필터", example = "false", required = false)
    val bookmark: Boolean?,
    @Schema(title = "경도(거리순 정렬 할 때 사용)", example = "126.123456", required = false)
    val longitude: Double?,
    @Schema(title = "위도(거리순 정렬 할 때 사용)", example = "37.123456", required = false)
    val latitude: Double?,
    @Schema(title = "페이지 커서(리스트 검색에서만 사용)", example = "['1', '2']", required = false)
    val cursor: List<Double>?,
)

enum class Sort {
    BASIC,
    CLOSELY_DESC,
    RATING_DESC,
    REVIEW_COUNT_DESC,
    BOOKMARK_COUNT_DESC,
    ID_ASC,
}

data class GetRestaurantsResponse(
    @Schema(description = "식당 리스트")
    val restaurants: Page<RestaurantDto>,
    @Schema(description = "다음 페이지")
    val nextCursor: List<Double>?,
)

data class GetRestaurantResponse(
    @Schema(description = "식당 정보")
    val restaurant: RestaurantDto,
)
