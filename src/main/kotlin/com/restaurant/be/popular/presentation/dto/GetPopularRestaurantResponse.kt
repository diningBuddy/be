package com.restaurant.be.popular.presentation.dto

import com.restaurant.be.restaurant.presentation.controller.dto.common.PopularRestaurantDto
import io.swagger.v3.oas.annotations.media.Schema

data class GetPopularRestaurantResponse(
    @Schema(description = "인기 음식점 리스트")
    val popularRestaurants: List<PopularRestaurantDto>
)
