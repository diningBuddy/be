package com.restaurant.be.restaurant.presentation.controller.dto.common

import com.restaurant.be.restaurant.repository.dto.FacilityInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.OperationInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.RestaurantEsDocument
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RestaurantEsDocumentTest : DescribeSpec({
    describe("MenuDto") {
        it("should create a correct RestaurantEsDocument instance") {
            // Given
            val restaurantEsDocument = RestaurantEsDocument(
                id = 1L,
                name = "Pizza",
                originalCategory = "Italian",
                address = "Seoul",
                imageUrl = "http://example.com/pizza.jpg",
                categories = listOf("Italian"),
                discountContent = "10% off",
                menus = mutableListOf(),
                reviewCount = 1L,
                ratingAvg = 4.5,
                kakaoRatingAvg = 4.5,
                kakaoRatingCount = 100,
                ratingCount = 100,
                facilityInfos = FacilityInfoEsDocument("N", "N", "N", "N", "N", "N"),
                operationInfos = OperationInfoEsDocument("N", "N", "N"),
                operationTimeInfos = mutableListOf(),
                bookmarkCount = 0
            )

            // When

            // Then
            restaurantEsDocument.id shouldBe 1L
            restaurantEsDocument.name shouldBe "Pizza"
            restaurantEsDocument.originalCategory shouldBe "Italian"
            restaurantEsDocument.address shouldBe "Seoul"
            restaurantEsDocument.imageUrl shouldBe "http://example.com/pizza.jpg"
            restaurantEsDocument.categories shouldBe listOf("Italian")
            restaurantEsDocument.discountContent shouldBe "10% off"
            restaurantEsDocument.menus shouldBe mutableListOf()
            restaurantEsDocument.reviewCount shouldBe 1L
            restaurantEsDocument.ratingAvg shouldBe 4.5
            restaurantEsDocument.kakaoRatingAvg shouldBe 4.5
            restaurantEsDocument.kakaoRatingCount shouldBe 100
            restaurantEsDocument.facilityInfos shouldBe FacilityInfoEsDocument("N", "N", "N", "N", "N", "N")
            restaurantEsDocument.operationInfos shouldBe OperationInfoEsDocument("N", "N", "N")
            restaurantEsDocument.operationTimeInfos shouldBe mutableListOf()
            restaurantEsDocument.ratingCount shouldBe 100
            restaurantEsDocument.bookmarkCount shouldBe 0
        }

        it("should create a correct MenuDto instance") {
            // Given
            val menuEsDocument = MenuDto(
                name = "Pasta",
                price = 15000,
                description = "Delicious pasta",
                isRepresentative = true,
                imageUrl = "http://example.com/pasta.jpg"
            )

            // When

            // Then
            menuEsDocument.name shouldBe "Pasta"
            menuEsDocument.price shouldBe 15000
            menuEsDocument.description shouldBe "Delicious pasta"
            menuEsDocument.isRepresentative shouldBe true
            menuEsDocument.imageUrl shouldBe "http://example.com/pasta.jpg"
        }
    }
})
