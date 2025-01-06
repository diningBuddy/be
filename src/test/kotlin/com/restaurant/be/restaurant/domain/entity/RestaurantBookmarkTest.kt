package com.restaurant.be.restaurant.domain.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RestaurantBookmarkTest : DescribeSpec({
    describe("RestaurantBookmark") {
        it("should create a correct RestaurantBookmark instance") {
            // Given
            val restaurantBookmark = RestaurantBookmark(
                id = 1L,
                restaurantId = 1,
                userId = 1
            )

            // When

            // Then
            restaurantBookmark.id shouldBe 1L
            restaurantBookmark.restaurantId shouldBe 1
            restaurantBookmark.userId shouldBe 1
        }
    }
})
