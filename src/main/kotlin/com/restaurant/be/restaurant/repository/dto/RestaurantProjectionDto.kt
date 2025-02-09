package com.restaurant.be.restaurant.repository.dto

import com.restaurant.be.category.domain.entity.Category
import com.restaurant.be.restaurant.domain.entity.Menu
import com.restaurant.be.restaurant.domain.entity.Restaurant
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDetailDto
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.review.domain.entity.Review

data class RestaurantProjectionDto(
    val restaurant: Restaurant,
    val isLike: Boolean,
    val menus: List<Menu>,
    val review: Review?,
    val categories: List<Category>
) {
    fun toDto(): RestaurantDto {
        return RestaurantDto(
            id = restaurant.id,
            representativeImageUrl = restaurant.representativeImageUrl,
            name = restaurant.name,
            ratingAvg = restaurant.ratingAvg,
            ratingCount = restaurant.ratingCount,
            facilityInfos = restaurant.facilityInfos,
            operationInfos = restaurant.operationInfos,
            operationTimes = restaurant.operationTimes,
            reviewCount = restaurant.reviewCount,
            bookmarkCount = restaurant.bookmarkCount,
            categories = categories.map { it.name },
            representativeMenu = menus.firstOrNull()?.toDto(),
            representativeReviewContent = review?.content,
            isBookmarked = isLike,
            discountContent = restaurant.discountContent,
            longitude = restaurant.longitude,
            latitude = restaurant.latitude,
            kakaoRatingAvg = restaurant.kakaoRatingAvg,
            kakaoRatingCount = restaurant.kakaoRatingCount,
            detailInfo = RestaurantDetailDto(
                contactNumber = restaurant.contactNumber,
                address = restaurant.address,
                menus = menus.map { it.toDto() }
            )
        )
    }
}
