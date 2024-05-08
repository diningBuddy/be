package com.restaurant.be.restaurant.presentation.dto.common

import io.swagger.v3.oas.annotations.media.Schema

data class RestaurantDto(
    @Schema(description = "식당 id")
    val id: Long,
    @Schema(description = "식당 대표 이미지 URL")
    val representativeImageUrl: String,
    @Schema(description = "식당 이름")
    val name: String,
    @Schema(description = "식당 평점 평균")
    val ratingAvg: Double,
    @Schema(description = "식당 리뷰 수")
    val reviewCount: Long,
    @Schema(description = "식당 좋아요 수")
    val likeCount: Long,
    @Schema(description = "식당 카테고리")
    val category: String,
    @Schema(description = "식당 대표 메뉴")
    val representativeMenu: String,
    @Schema(description = "식당 영업 시작 시간")
    val operatingStartTime: String,
    @Schema(description = "식당 영업 종료 시간")
    val operatingEndTime: String,
    @Schema(description = "식당 대표 리뷰 내용")
    val representativeReviewContent: String,
    @Schema(description = "식당 좋아요 여부(로그인한 유저)")
    val isLike: Boolean,
    @Schema(description = "식당 할인 여부")
    val isDiscountForSkku: Boolean,
    @Schema(description = "식당 할인 내용")
    val discountContent: String,

    @Schema(description = "식당 상세 정보")
    val detailInfo: RestaurantDetailDto
)

data class RestaurantDetailDto(
    @Schema(description = "식당 전화번호")
    val contactNumber: String,
    @Schema(description = "식당 주소")
    val address: String
)