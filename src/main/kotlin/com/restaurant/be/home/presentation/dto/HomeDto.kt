package com.restaurant.be.home.presentation.dto

import com.restaurant.be.kakao.presentation.dto.CategoryParam
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.swagger.v3.oas.annotations.media.Schema

data class HomeRequest(
    @Schema(title = "사용자 위치 - 위도", example = "37.2977142440725")
    val userLatitude: Double,
    @Schema(title = "사용자 위치 - 경도", example = "126.970140339367")
    val userLongitude: Double
)

data class HomeResponse(
    val restaurantBanner: List<GetBannerResponse>,
    val restaurantRecommendations: List<GetRecommendationRestaurantsResponse>
)

data class GetBannerResponse(
    @Schema(description = "배너 id")
    val bannerId: Long,
    @Schema(description = "대표 imageUrl")
    val imageUrl: String,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "카테고리")
    val category: CategoryParam,
    @Schema(description = "소제목")
    val subtitle: String
)

data class GetRecommendationRestaurantsResponse(
    @Schema(description = "추천 타입")
    val recommendationType: RecommendationType,
    @Schema(description = "섹션 제목")
    val sectionTitle: String,
    @Schema(description = "식당 리스트")
    val restaurants: List<RestaurantDto>
)

enum class RecommendationType(
    val title: String
) {
    LUNCH("점심 맛집 정보,  오늘 뭐 먹지?"),
    LATE_NIGHT("야식의 성지! 새벽까지 든든하게")
}
