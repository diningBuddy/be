package com.restaurant.be.kakao.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "popular_restaurants")
class PopularRestaurant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "scrap_category", nullable = false)
    var scrapCategory: ScrapCategory,

    @Column(name = "rank_number", nullable = false)
    var rankNumber: Int
)
