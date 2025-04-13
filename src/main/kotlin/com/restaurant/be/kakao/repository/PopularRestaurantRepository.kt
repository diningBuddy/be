package com.restaurant.be.kakao.repository

import com.restaurant.be.kakao.domain.entity.PopularRestaurant
import com.restaurant.be.kakao.domain.entity.ScrapCategory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PopularRestaurantRepository : JpaRepository<PopularRestaurant, Long> {
    fun findTopRankedByScrapCategory(
        scrapCategory: ScrapCategory,
        pageable: Pageable
    ): List<PopularRestaurant>
}
