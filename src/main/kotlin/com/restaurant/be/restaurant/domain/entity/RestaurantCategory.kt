package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue



@Entity
@Table(name = "restaurant_categories")
data class RestaurantCategory(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "group_id")
    var groupId: Long,

    @Column(name = "restaurant_id")
    var restaurantId: Long,

    @Column(name = "category_group", nullable = false)
    var categoryGroup: String
)
