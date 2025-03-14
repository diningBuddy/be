package com.restaurant.be.restaurant.domain.service

import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.restaurant.presentation.controller.dto.RecommendRestaurantResponse
import com.restaurant.be.restaurant.repository.RestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommendRestaurantService(
    private val redisRepository: RedisRepository,
    private val restaurantRepository: RestaurantRepository
) {
    @Transactional(readOnly = true)
    fun recommendRestaurants(userId: Long): RecommendRestaurantResponse {
        val restaurantIds = redisRepository.getRecommendation(userId)

        val restaurantDtos = restaurantRepository.findDtoByIds(restaurantIds, userId)

        val randomRestaurantDtos =
            if (restaurantDtos.size > 5) {
                restaurantDtos.shuffled().take(5)
            } else {
                restaurantDtos
            }

        return RecommendRestaurantResponse(
            restaurants = randomRestaurantDtos.map { it.toDto() }
        )
    }
}
