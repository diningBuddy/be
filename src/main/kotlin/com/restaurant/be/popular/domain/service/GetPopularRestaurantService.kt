package com.restaurant.be.popular.domain.service

import com.restaurant.be.popular.presentation.dto.GetPopularRestaurantResponse
import com.restaurant.be.popular.repository.PopularRestaurantRepository
import com.restaurant.be.restaurant.presentation.controller.dto.common.PopularRestaurantDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService(
    private val popularRestaurantRepository: PopularRestaurantRepository
) {
    @Transactional(readOnly = true)
    fun getPopularRestaurants(
        pageable: Pageable
    ): GetPopularRestaurantResponse {
        val popularRestaurants = popularRestaurantRepository.findAllByOrderByRankAsc(pageable)

        return GetPopularRestaurantResponse(
            popularRestaurants.content.map { restaurant ->
                PopularRestaurantDto(
                    rank = restaurant.rank,
                    name = restaurant.name,
                    originalCategories = restaurant.originalCategories,
                    longitude = restaurant.longitude,
                    latitude = restaurant.latitude,
                    ratingAvg = restaurant.ratingAvg,
                    ratingCount = restaurant.ratingCount,
                    representativeImageUrl = restaurant.representativeImageUrl
                )
            }
        )
    }
}
