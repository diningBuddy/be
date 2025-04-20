package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.presentation.dto.CategoryParam
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService(
    private val popularRestaurantRepository: PopularRestaurantRepository
) {
    companion object {
        private const val DEFAULT_MAX_RESULTS = 100
    }

    @Transactional(readOnly = true)
    fun getRestaurantIdsByScrapCategory(
        mainCategory: CategoryParam,
        limit: Int = DEFAULT_MAX_RESULTS
    ): List<Long> {
        val scrapCategory = mainCategory.toDomain()
        return popularRestaurantRepository
            .findByScrapCategory(
                scrapCategory,
                PageRequest.of(0, limit)
            )
            .map { it.restaurantId }
    }
}
