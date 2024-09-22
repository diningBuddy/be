package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "restaurant_categories")
data class RestaurantCategory(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,

    @Column(name = "category_id", nullable = false)
    var categoryId: Long
)
