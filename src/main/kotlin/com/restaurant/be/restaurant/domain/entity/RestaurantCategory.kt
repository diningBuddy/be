package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurant_categories")
data class RestaurantCategory(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "parent_category")
    var groupId: Long,

    @Column(name = "restaurant_id")
    var restaurantId: Long,

    @Column(name = "category_groups", nullable = false)
    var categoryGroup: String
)
