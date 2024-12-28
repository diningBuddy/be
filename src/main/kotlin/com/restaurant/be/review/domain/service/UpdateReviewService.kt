package com.restaurant.be.review.domain.service

import com.restaurant.be.common.exception.NotFoundReviewException
import com.restaurant.be.common.exception.UnAuthorizedUpdateException
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.review.presentation.dto.UpdateReviewRequest
import com.restaurant.be.review.presentation.dto.UpdateReviewResponse
import com.restaurant.be.review.presentation.dto.common.ReviewResponseDto
import com.restaurant.be.review.repository.ReviewLikeRepository
import com.restaurant.be.review.repository.ReviewRepository
import com.restaurant.be.user.domain.entity.QUser.user
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UpdateReviewService(
    private val reviewRepository: ReviewRepository,
    private val restaurantRepository: RestaurantRepository,
    private val reviewLikeRepository: ReviewLikeRepository
) {
    @Transactional
    fun updateReview(
        restaurantId: Long,
        reviewId: Long,
        reviewRequest: UpdateReviewRequest,
        userId: Long
    ): UpdateReviewResponse {
        val review =
            reviewRepository
                .findById(reviewId)
                .orElseThrow { NotFoundReviewException() }

        if (userId != review.user.id) throw UnAuthorizedUpdateException()

        applyReviewCountAndAvgRating(
            review.restaurantId,
            review.rating,
            reviewRequest.review.rating
        )

        review.updateReview(reviewRequest)

        reviewRepository.save(review)

        val responseDto =
            ReviewResponseDto.toDto(
                review,
                reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)
            )

        return UpdateReviewResponse(responseDto)
    }

    private fun applyReviewCountAndAvgRating(
        restaurantId: Long,
        rating: Double,
        updateRating: Double
    ) {
        val restaurant = restaurantRepository.findById(restaurantId).getOrNull()
        if (restaurant != null) {
            restaurant.updateReview(rating, updateRating)
            restaurantRepository.save(restaurant)
        }
    }
}
