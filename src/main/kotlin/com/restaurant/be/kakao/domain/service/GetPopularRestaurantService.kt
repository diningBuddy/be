package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.presentation.dto.CategoryParam
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService(
    private val popularRestaurantRepository: PopularRestaurantRepository
) {
    @Transactional(readOnly = true)
    fun getRestaurantIdsByScrapCategory(category: CategoryParam): List<Long> {
        return popularRestaurantRepository.findAllByScrapCategory(category.toDomain())
            .map { it.restaurantId }
    }
}
