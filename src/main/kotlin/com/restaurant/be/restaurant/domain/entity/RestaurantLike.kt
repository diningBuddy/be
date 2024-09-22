package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "restaurant_likes")
class RestaurantLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long
)
