package com.restaurant.be.restaurant.repository

import com.restaurant.be.restaurant.repository.dto.RestaurantProjectionDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RestaurantRepositoryCustom {
    fun findDtoById(restaurantId: Long): RestaurantProjectionDto?
    fun findDtoByIds(
        restaurantIds: List<Long>,
        userId: Long,
        isLikeFilter: Boolean?,
        pageable: Pageable
    ): Page<RestaurantProjectionDto>
}