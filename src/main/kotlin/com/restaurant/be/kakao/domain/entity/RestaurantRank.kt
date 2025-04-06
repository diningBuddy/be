package com.restaurant.be.kakao.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurant_ranks")
class RestaurantRank(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "scrap_category", nullable = false)
    var scrapCategory: ScrapCategory,

    @Column(name = "rank_number", nullable = false)
    var rank: Int,
)
