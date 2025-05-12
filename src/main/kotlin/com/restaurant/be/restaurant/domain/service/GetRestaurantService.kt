package com.restaurant.be.restaurant.domain.service

import com.restaurant.be.common.exception.NotFoundRestaurantException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.recent.domain.service.RecentSearchService
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.toResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.repository.RestaurantBookmarkRepository
import com.restaurant.be.restaurant.repository.RestaurantEsRepository
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.user.domain.service.GetUserService
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRestaurantService(
    private val restaurantEsRepository: RestaurantEsRepository,
    private val redisRepository: RedisRepository,
    private val restaurantRepository: RestaurantRepository,
    private val restaurantLikeRepository: RestaurantBookmarkRepository,
    private val recentSearchService: RecentSearchService,
    private val userRepository: UserRepository,
    private val getUserService: GetUserService
) {
    @Transactional
    fun getRestaurants(
        request: GetRestaurantsRequest,
        pageable: Pageable,
        userId: Long
    ): GetRestaurantsResponse {
        val restaurantIds =
            if (request.bookmark != null) {
                restaurantLikeRepository
                    .findAllByUserId(userId)
                    .map { it.restaurantId }
            } else {
                null
            }
        // TODO:테마, 소개 관련 필터링등..
        val (restaurants, nextCursor) =
            restaurantEsRepository.searchRestaurants(
                request,
                pageable,
                restaurantIds,
                request.bookmark
            )

        val restaurantProjections =
            restaurantRepository.findDtoByIds(
                restaurants.map { it.id },
                userId
            )

        val restaurantMap = restaurantProjections.associateBy { it.restaurant.id }

        val sortedRestaurantProjections = restaurants.mapNotNull { esRestaurant ->
            val projection = restaurantMap[esRestaurant.id] ?: return@mapNotNull null
            val dto = projection.toDto()

            esRestaurant.operationTimeInfos?.let { esOperationTimes
                ->
                dto.operationTimes = esOperationTimes.map { it.toResponse() }
            }
            dto
        }

        if (!request.query.isNullOrEmpty() && sortedRestaurantProjections.isNotEmpty()) {
            userRepository.findById(userId)
                .ifPresent { user ->
                    recentSearchService.saveRecentSearch(user, request.query)
                }
        }

        return GetRestaurantsResponse(
            PageImpl(
                sortedRestaurantProjections,
                pageable,
                sortedRestaurantProjections.size.toLong()
            ),
            nextCursor
        )
    }

    @Transactional(readOnly = true)
    fun getPopularRestaurants(
        request: GetRestaurantsRequest,
        pageable: Pageable,
        userId: Long,
        restaurantIds: List<Long>
    ): GetRestaurantsResponse {
        val (restaurants, nextCursor) =
            restaurantEsRepository.searchPopularRestaurants(
                request,
                pageable,
                restaurantIds
            )

        if (!request.query.isNullOrEmpty()) {
            redisRepository.addSearchQuery(userId, request.query)
        }

        val restaurantProjections =
            restaurantRepository.findDtoByIds(
                restaurants.map { it.id },
                userId
            )

        val restaurantMap = restaurantProjections.associateBy { it.restaurant.id }
        val sortedRestaurantProjections = restaurants.mapNotNull { esRestaurant ->
            val projection = restaurantMap[esRestaurant.id] ?: return@mapNotNull null
            val dto = projection.toDto()

            esRestaurant.operationTimeInfos?.let { esOperationTimes
                ->
                dto.operationTimes = esOperationTimes.map { it.toResponse() }
            }
            dto
        }

        return GetRestaurantsResponse(
            PageImpl(
                sortedRestaurantProjections,
                pageable,
                sortedRestaurantProjections.size.toLong()
            ),
            nextCursor
        )
    }

    @Transactional(readOnly = true)
    fun getRestaurant(
        restaurantId: Long,
        userId: Long
    ): GetRestaurantResponse {
        val restaurant =
            restaurantRepository.findDtoById(restaurantId, userId)
                ?: throw NotFoundRestaurantException()
        return GetRestaurantResponse(restaurant.toDto())
    }
}
