package com.restaurant.be.kakao.repository

import com.restaurant.be.kakao.domain.entity.RestaurantRank
import org.springframework.data.jpa.repository.JpaRepository

interface PopularRestaurantRepository : JpaRepository<RestaurantRank, Long>
