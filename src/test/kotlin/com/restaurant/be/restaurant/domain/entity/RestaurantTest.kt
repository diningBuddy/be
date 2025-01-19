package com.restaurant.be.restaurant.domain.entity

import com.restaurant.be.restaurant.domain.entity.kakaoinfo.FacilityInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationInfoJsonEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe

class RestaurantTest : DescribeSpec({
    describe("Restaurant entity") {
        lateinit var restaurant: Restaurant

        beforeEach {
            restaurant = Restaurant(
                id = 1L,
                name = "Test Restaurant",
                originalCategories = "Category1",
                reviewCount = 0,
                bookmarkCount = 0,
                address = "123 Test St",
                contactNumber = "123-456-7890",
                ratingAvg = 0.0,
                ratingCount = 0,
                facilityInfos = FacilityInfoJsonEntity("N", "N", "N", "N", "N", "N"),
                operationInfos = OperationInfoJsonEntity("N", "N", "N"),
                operationTimes = mutableListOf(),

                representativeImageUrl = "http://example.com/image.jpg",
                viewCount = 0,
                discountContent = null,
                longitude = 0.0,
                latitude = 0.0,
                naverRatingAvg = 0.0,
                naverReviewCount = 0,
                kakaoRatingAvg = 0.0,
                kakaoRatingCount = 0,
                menus = mutableListOf()
            )
        }
        context("should create a correct Restaurant instance") {
            it("should create a correct Restaurant instance") {
                restaurant.id shouldBe 1L
                restaurant.name shouldBe "Test Restaurant"
                restaurant.originalCategories shouldBe "Category1"
                restaurant.reviewCount shouldBe 0
                restaurant.bookmarkCount shouldBe 0
                restaurant.address shouldBe "123 Test St"
                restaurant.contactNumber shouldBe "123-456-7890"
                restaurant.ratingAvg?.shouldBeExactly(0.0)
                restaurant.ratingCount shouldBe 0
                restaurant.facilityInfos shouldBe FacilityInfoJsonEntity("N", "N", "N", "N", "N", "N")
                restaurant.operationInfos shouldBe OperationInfoJsonEntity("N", "N", "N")
                restaurant.operationTimes shouldBe mutableListOf()
                restaurant.representativeImageUrl shouldBe "http://example.com/image.jpg"
                restaurant.viewCount shouldBe 0
                restaurant.discountContent shouldBe null
                restaurant.longitude shouldBeExactly 0.0
                restaurant.latitude shouldBeExactly 0.0
                restaurant.naverRatingAvg?.shouldBeExactly(0.0)
                restaurant.naverReviewCount shouldBe 0
                restaurant.menus shouldBe mutableListOf()
                restaurant.kakaoRatingAvg shouldBe 0.0
                restaurant.kakaoRatingCount shouldBe 0
            }
        }

        context("createReview") {
            it("should update ratingAvg and reviewCount when a new review is added") {
                restaurant.createReview(4.0)
                restaurant.reviewCount shouldBe 1
                restaurant.ratingAvg?.shouldBeExactly(4.0)

                restaurant.createReview(2.0)
                restaurant.reviewCount shouldBe 2
                restaurant.ratingAvg?.shouldBeExactly(3.0)
            }
        }

        context("deleteReview") {
            it("should update ratingAvg and reviewCount when a review is deleted") {
                // Add initial reviews
                restaurant.createReview(4.0)
                restaurant.createReview(2.0)

                restaurant.deleteReview(4.0)
                restaurant.reviewCount shouldBe 1
                restaurant.ratingAvg?.shouldBeExactly(2.0)

                restaurant.deleteReview(2.0)
                restaurant.reviewCount shouldBe 0
                restaurant.ratingAvg?.shouldBeExactly(0.0)
            }
        }

        context("updateReview") {
            it("should update ratingAvg when a review is updated") {
                // Add initial reviews
                restaurant.createReview(4.0)
                restaurant.createReview(2.0)

                restaurant.updateReview(2.0, 5.0)
                restaurant.reviewCount shouldBe 2
                restaurant.ratingAvg?.shouldBeExactly(4.5)

                restaurant.updateReview(4.0, 3.0)
                restaurant.reviewCount shouldBe 2
                restaurant.ratingAvg?.shouldBeExactly(4.0)
            }
        }
    }
})
