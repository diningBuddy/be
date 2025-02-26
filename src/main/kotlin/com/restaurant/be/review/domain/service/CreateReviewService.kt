package com.restaurant.be.review.domain.service

import com.restaurant.be.common.exception.NotFoundRestaurantException
import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.review.domain.entity.ReviewImage
import com.restaurant.be.review.presentation.dto.CreateReviewResponse
import com.restaurant.be.review.presentation.dto.common.ReviewRequestDto
import com.restaurant.be.review.presentation.dto.common.ReviewResponseDto
import com.restaurant.be.review.repository.ReviewRepository
import com.restaurant.be.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CreateReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) {
    @Transactional
    fun createReview(
        restaurantId: Long,
        reviewRequest: ReviewRequestDto,
        userId: Long
    ): CreateReviewResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundUserException()

        val review = reviewRequest.toEntity(user, restaurantId)

        reviewRequest.imageUrls.forEach {
            review.addImage(
                ReviewImage(
                    imageUrl = it
                )
            )
        }

        reviewRepository.save(review)

        applyReviewCountAndAvgRating(restaurantId, reviewRequest.rating)

        val reviewWithLikes = reviewRepository.findReview(user, review.id ?: 0)!!

        val responseDto =
            ReviewResponseDto.toDto(
                reviewWithLikes.review,
                reviewWithLikes.isLikedByUser
            )

        return CreateReviewResponse(responseDto)
    }

    private fun applyReviewCountAndAvgRating(
        restaurantId: Long,
        newRating: Double
    ) {
        val restaurant =
            restaurantRepository.findById(restaurantId).getOrNull()
                ?: throw NotFoundRestaurantException()
        restaurant.createReview(newRating)
    }
}
