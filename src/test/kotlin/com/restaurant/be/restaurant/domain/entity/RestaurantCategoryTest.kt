package com.restaurant.be.restaurant.domain.entity

import com.restaurant.be.category.domain.entity.Category
import com.restaurant.be.common.util.RestaurantUtil
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RestaurantCategoryTest : DescribeSpec({

    describe("RestaurantCategory") {
        it("should create a correct RestaurantCategory instance") {
            // Given
            val restaurant = RestaurantUtil.generateRestaurantEntity(
                id = 1L,
                name = "default_name"
            )
            var category = Category(
                id = 1L,
                name = "default_name"
            )
            val restaurantCategory = RestaurantCategory(
                id = 1L,
                restaurant = restaurant,
                category = category
            )

            // When

            // Then
            restaurantCategory.id shouldBe 1L
            restaurantCategory.restaurant shouldBe restaurant
            restaurantCategory.category shouldBe category
        }
    }
})
