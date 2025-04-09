package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.domain.entity.ScrapCategory
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService(
    private val popularRestaurantRepository: PopularRestaurantRepository
) {
    @Transactional(readOnly = true)
    fun getRestaurantIdsByScrapCategory(scrapCategory: ScrapCategory): List<Long> {
        return popularRestaurantRepository.findAllByScrapCategory(scrapCategory)
            .mapNotNull { it.restaurantId }
    }
}
