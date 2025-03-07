package com.restaurant.be.restaurant.repository.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RestaurantEsDocumentTest : DescribeSpec({
    describe("RestaurantEsDocument") {
        it("should create a correct RestaurantEsDocument instance") {
            // Given
            val menuEsDocument = MenuEsDocument(
                menuName = "Pasta",
                price = 15000,
                description = "Delicious pasta",
                imageUrl = "http://example.com/pasta.jpg"
            )
            val restaurantEsDocument = RestaurantEsDocument(
                id = 1L,
                name = "Test Restaurant",
                originalCategory = "Italian, Pizza",
                kakaoRatingCount = 100L,
                address = "123 Test St",
                kakaoRatingAvg = 4.5,
                imageUrl = "http://example.com/restaurant.jpg",
                categories = listOf("Italian, Pizza"),
                discountContent = "10% off",
                menus = listOf(menuEsDocument),
                reviewCount = 200L,
                ratingAvg = 4.5,
                facilityInfos = FacilityInfoEsDocument("N", "N", "N", "N", "N", "N"),
                operationInfos = OperationInfoEsDocument("N", "N", "N"),
                operationTimeInfos = mutableListOf(),
                ratingCount = 100,
                bookmarkCount = 0
            )

            // When

            // Then
            restaurantEsDocument.id shouldBe 1L
            restaurantEsDocument.name shouldBe "Test Restaurant"
            restaurantEsDocument.originalCategory shouldBe "Italian, Pizza"
            restaurantEsDocument.kakaoRatingCount shouldBe 100L
            restaurantEsDocument.address shouldBe "123 Test St"
            restaurantEsDocument.kakaoRatingAvg shouldBe 4.5
            restaurantEsDocument.imageUrl shouldBe "http://example.com/restaurant.jpg"
            restaurantEsDocument.categories shouldBe listOf("Italian, Pizza")
            restaurantEsDocument.discountContent shouldBe "10% off"
            restaurantEsDocument.menus.size shouldBe 1
            restaurantEsDocument.menus[0].menuName shouldBe "Pasta"
            restaurantEsDocument.reviewCount shouldBe 200L
            restaurantEsDocument.ratingAvg shouldBe 4.5
            restaurantEsDocument.facilityInfos shouldBe FacilityInfoEsDocument("N", "N", "N", "N", "N", "N")
            restaurantEsDocument.operationInfos shouldBe OperationInfoEsDocument("N", "N", "N")
            restaurantEsDocument.operationTimeInfos shouldBe mutableListOf()
            restaurantEsDocument.bookmarkCount shouldBe 0
        }
    }

    describe("MenuEsDocument") {
        it("should create a correct MenuEsDocument instance") {
            // Given
            val menuEsDocument = MenuEsDocument(
                menuName = "Pasta",
                price = 15000,
                description = "Delicious pasta",
                imageUrl = "http://example.com/pasta.jpg"
            )

            // When

            // Then
            menuEsDocument.menuName shouldBe "Pasta"
            menuEsDocument.price shouldBe 15000
            menuEsDocument.description shouldBe "Delicious pasta"
            menuEsDocument.imageUrl shouldBe "http://example.com/pasta.jpg"
        }
    }
})
