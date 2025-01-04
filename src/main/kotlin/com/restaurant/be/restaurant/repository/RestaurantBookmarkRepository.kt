package com.restaurant.be.restaurant.repository

import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import org.springframework.data.jpa.repository.JpaRepository

interface RestaurantBookmarkRepository : JpaRepository<RestaurantBookmark, Long> {
    fun deleteByUserIdAndRestaurantId(userId: Long, restaurantId: Long)
    fun findAllByUserId(userId: Long): List<RestaurantBookmark>
}
