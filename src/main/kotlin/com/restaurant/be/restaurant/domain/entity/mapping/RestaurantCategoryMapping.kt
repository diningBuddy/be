package com.restaurant.be.restaurant.domain.entity.mapping

import com.restaurant.be.restaurant.domain.entity.Restaurant
import com.restaurant.be.restaurant.domain.entity.RestaurantCategory
import jakarta.persistence.*

@Entity
@Table(name = "restaurant_categories_mapping")
class RestaurantCategoryMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    var restaurantId: Restaurant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var categoryId: RestaurantCategory
) {}
