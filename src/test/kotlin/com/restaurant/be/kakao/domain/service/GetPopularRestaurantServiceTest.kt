package com.restaurant.be.kakao.domain.service

import com.restaurant.be.kakao.domain.entity.PopularRestaurant
import com.restaurant.be.kakao.domain.entity.ScrapCategory
import com.restaurant.be.kakao.presentation.dto.CategoryParam
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageRequest

class GetPopularRestaurantServiceTest : DescribeSpec({

    val popularRestaurantRepository = mockk<PopularRestaurantRepository>()
    val service = GetPopularRestaurantService(popularRestaurantRepository)

    describe("GetPopularRestaurantService") {
        describe("getRestaurantIdsByScrapCategory") {
            it("should return restaurant IDs for a given category") {
                // Given
                val category = CategoryParam.KOREAN
                val scrapCategory = ScrapCategory.KOREAN
                val expectedLimit = 100
                val pageable = PageRequest.of(0, expectedLimit)

                val popularRestaurants = listOf(
                    PopularRestaurant(1, 101, scrapCategory, 1),
                    PopularRestaurant(2, 102, scrapCategory, 2),
                    PopularRestaurant(3, 103, scrapCategory, 3)
                )

                every {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                } returns popularRestaurants

                // When
                val result = service.getRestaurantIdsByScrapCategory(category)

                // Then
                result shouldBe listOf(101L, 102L, 103L)

                verify(exactly = 1) {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                }
            }

            it("should use custom limit if provided") {
                // Given
                val category = CategoryParam.CAFE
                val scrapCategory = ScrapCategory.CAFE
                val customLimit = 50
                val pageable = PageRequest.of(0, customLimit)

                val popularRestaurants = listOf(
                    PopularRestaurant(1, 201, scrapCategory, 1),
                    PopularRestaurant(2, 202, scrapCategory, 2)
                )

                every {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                } returns popularRestaurants

                // When
                val result = service.getRestaurantIdsByScrapCategory(category, customLimit)

                // Then
                result shouldBe listOf(201L, 202L)

                verify(exactly = 1) {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                }
            }

            it("should return empty list if no restaurants found") {
                // Given
                val category = CategoryParam.CHINESE
                val scrapCategory = ScrapCategory.CHINESE
                val expectedLimit = 100 // Default limit
                val pageable = PageRequest.of(0, expectedLimit)

                every {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                } returns emptyList()

                // When
                val result = service.getRestaurantIdsByScrapCategory(category)

                // Then
                result shouldBe emptyList()

                verify(exactly = 1) {
                    popularRestaurantRepository.findByScrapCategory(scrapCategory, pageable)
                }
            }
        }
    }
})
