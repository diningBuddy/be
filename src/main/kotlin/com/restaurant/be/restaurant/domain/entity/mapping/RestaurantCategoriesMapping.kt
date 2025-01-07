package com.restaurant.be.restaurant.domain.entity.mapping

import jakarta.persistence.*

@Entity
@Table(name = "restaurant_categories_mapping")
class RestaurantCategoriesMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long,
    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,
    @Column(name = "category_id", nullable = false)
    var categoryId: Long,
)