package com.restaurant.be.restaurant.domain.service

import com.restaurant.be.common.exception.NotFoundRestaurantException
import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import com.restaurant.be.restaurant.presentation.controller.dto.GetLikeRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.LikeRestaurantResponse
import com.restaurant.be.restaurant.repository.RestaurantLikeRepository
import com.restaurant.be.restaurant.repository.RestaurantRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeRestaurantService(
    private val restaurantRepository: RestaurantRepository,
    private val restaurantLikeRepository: RestaurantLikeRepository
) {
    @Transactional
    fun likeRestaurant(
        userId: Long,
        restaurantId: Long,
        isLike: Boolean
    ): LikeRestaurantResponse {
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
        return LikeRestaurantResponse(restaurantRepository.findDtoById(restaurantId, userId)!!.toDto())
    }

    @Transactional(readOnly = true)
    fun getMyLikeRestaurant(
        pageable: Pageable,
        userId: Long
    ): GetLikeRestaurantsResponse =
        GetLikeRestaurantsResponse(
            restaurantRepository
                .findMyLikeRestaurants(userId, pageable)
                .map { it.toDto() }
        )
}
