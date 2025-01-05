package com.restaurant.be.restaurant.domain.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RestaurantCategoryTest : DescribeSpec({

    describe("RestaurantCategory") {
        it("should create a correct RestaurantCategory instance") {
            // Given
            val restaurantCategory = RestaurantCategory(
                id = 1L,
                name = "name",
                groupId = 1L,
                restaurantId = 1L,
                categoryGroup = "default_group"
            )

            // When

            // Then
            restaurantCategory.id shouldBe 1L
            restaurantCategory.name shouldBe "name"
            restaurantCategory.groupId shouldBe 1L
            restaurantCategory.restaurantId shouldBe 1L
            restaurantCategory.categoryGroup shouldBe 1L
        }
    }
})
