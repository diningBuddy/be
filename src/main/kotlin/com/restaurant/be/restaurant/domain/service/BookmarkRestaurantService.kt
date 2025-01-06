package com.restaurant.be.restaurant.domain.service

import com.restaurant.be.common.exception.NotFoundRestaurantException
import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import com.restaurant.be.restaurant.presentation.controller.dto.BookmarkRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetBookmarkRestaurantsResponse
import com.restaurant.be.restaurant.repository.RestaurantBookmarkRepository
import com.restaurant.be.restaurant.repository.RestaurantRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookmarkRestaurantService(
    private val restaurantRepository: RestaurantRepository,
    private val restaurantLikeRepository: RestaurantBookmarkRepository
) {
    @Transactional
    fun likeRestaurant(
        userId: Long,
        restaurantId: Long,
        isLike: Boolean
    ): BookmarkRestaurantResponse {
        val restaurantDto =
            restaurantRepository.findDtoById(restaurantId, userId)
                ?: throw NotFoundRestaurantException()

        val restaurant =
            restaurantRepository
                .findById(restaurantId)
                .orElseThrow { NotFoundRestaurantException() }

        if (isLike) {
            if (!restaurantDto.isLike) {
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        restaurantId = restaurantId,
                        userId = userId
                    )
                )
                restaurant.bookmarkCount += 1
            }
        } else {
            if (restaurantDto.isLike) {
                restaurant.bookmarkCount -= 1
                restaurantLikeRepository.deleteByUserIdAndRestaurantId(userId, restaurantId)
            }
        }

        restaurantRepository.save(restaurant)
        return BookmarkRestaurantResponse(restaurantRepository.findDtoById(restaurantId, userId)!!.toDto())
    }

    @Transactional(readOnly = true)
    fun getMyLikeRestaurant(
        pageable: Pageable,
        userId: Long
    ): GetBookmarkRestaurantsResponse =
        GetBookmarkRestaurantsResponse(
            restaurantRepository
                .findMyLikeRestaurants(userId, pageable)
                .map { it.toDto() }
        )
}
