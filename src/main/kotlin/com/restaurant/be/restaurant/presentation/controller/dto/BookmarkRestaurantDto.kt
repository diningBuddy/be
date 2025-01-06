package com.restaurant.be.restaurant.presentation.controller.dto

import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.swagger.annotations.ApiModelProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

data class GetBookmarkRestaurantsResponse(
    @Schema(description = "좋아요한 식당 리스트")
    val restaurants: Page<RestaurantDto>
)

data class BookmarkRestaurantRequest(
    @ApiModelProperty(value = "현재 좋아요 했는지 여부", example = "false", required = true)
    val isLike: Boolean
)
data class BookmarkRestaurantResponse(
    @Schema(description = "좋아요한 식당 정보")
    val restaurant: RestaurantDto
)
