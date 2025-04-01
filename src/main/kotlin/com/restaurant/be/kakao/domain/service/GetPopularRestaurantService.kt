package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPopularRestaurantService (
    private val popularRestaurantRepository: PopularRestaurantRepository,
){
    @Transactional(readOnly = true)
    fun getRestaurantIds(): List<Long> {
        return popularRestaurantRepository.findAll().map { it.restaurantId }
    }

}
