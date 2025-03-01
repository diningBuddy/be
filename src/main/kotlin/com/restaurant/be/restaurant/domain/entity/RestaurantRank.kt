package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurant_ranks")
class RestaurantRank (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    var restaurant: Restaurant,

    @Column(name = "rank_number", nullable = false)
    var rank: Int
)