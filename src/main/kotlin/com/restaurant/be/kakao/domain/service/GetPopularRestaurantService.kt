package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.domain.entity.ScrapCategory
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService(
    private val popularRestaurantRepository: PopularRestaurantRepository
) {
    @Transactional(readOnly = true)
    fun getRestaurantIds(): List<Long> {
        return popularRestaurantRepository.findAll().map { it.restaurantId }
    }

    @Transactional(readOnly = true)
    fun getHomeBannerRestaurants(
        category: ScrapCategory,
        limit: Int = 10
    ): List<Long> {
        return popularRestaurantRepository
            .findTopRankedByScrapCategory(category, PageRequest.of(0, limit))
            .map { it.restaurantId }
    }
}
