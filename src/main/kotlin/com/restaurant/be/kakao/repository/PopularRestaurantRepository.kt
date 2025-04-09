package com.restaurant.be.kakao.repository

import com.restaurant.be.kakao.domain.entity.PopularRestaurant
import org.springframework.data.jpa.repository.JpaRepository

interface PopularRestaurantRepository : JpaRepository<PopularRestaurant, Long>
