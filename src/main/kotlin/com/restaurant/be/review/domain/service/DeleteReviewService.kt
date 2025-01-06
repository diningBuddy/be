package com.restaurant.be.review.domain.service

import com.restaurant.be.common.exception.NotFoundRestaurantException
import com.restaurant.be.common.exception.NotFoundReviewException
import com.restaurant.be.common.exception.UnAuthorizedDeleteException
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.review.repository.ReviewRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class DeleteReviewService(
    private val reviewRepository: ReviewRepository,
    private val restaurantRepository: RestaurantRepository
) {
    @Transactional
    fun deleteReview(
        reviewId: Long,
        userId: Long
    ) {
        val review = reviewRepository.findById(reviewId).getOrNull() ?: throw NotFoundReviewException()

        applyReviewCountAndAvgRating(review.restaurantId, review.rating)

        if (userId != review.user.id) throw UnAuthorizedDeleteException()

        reviewRepository.deleteById(reviewId)
    }

    private fun applyReviewCountAndAvgRating(
        restaurantId: Long,
        beforeRating: Double
    ) {
        val restaurant =
            restaurantRepository.findById(restaurantId).getOrNull()
                ?: throw NotFoundRestaurantException()
        restaurant.deleteReview(beforeRating)
    }
}
