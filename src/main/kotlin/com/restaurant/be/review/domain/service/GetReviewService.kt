package com.restaurant.be.review.domain.service

import com.restaurant.be.common.exception.NotFoundReviewException
import com.restaurant.be.common.exception.NotFoundUserPhoneNumberException
import com.restaurant.be.review.presentation.dto.GetMyReviewsResponse
import com.restaurant.be.review.presentation.dto.GetReviewResponse
import com.restaurant.be.review.presentation.dto.GetReviewsResponse
import com.restaurant.be.review.presentation.dto.common.ReviewResponseDto
import com.restaurant.be.review.repository.ReviewRepository
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReviewService(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) {
    @Transactional(readOnly = true)
    fun getReviews(
        pageable: Pageable,
        restaurantId: Long,
        userId: Long
    ): GetReviewsResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundUserPhoneNumberException()

        val reviewsWithLikes = reviewRepository.findReviews(user, restaurantId, pageable)

        return GetReviewsResponse(
            reviewsWithLikes.map {
                ReviewResponseDto.toDto(
                    it.review,
                    it.isLikedByUser
                )
            }
        )
    }

    @Transactional
    fun getReview(
        reviewId: Long,
        userId: Long
    ): GetReviewResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundUserPhoneNumberException()

        val reviewWithLikes =
            reviewRepository.findReview(user, reviewId)
                ?: throw NotFoundReviewException()

        if (reviewWithLikes.review.user.id != user.id) {
            reviewWithLikes.review.incrementViewCount()
        }

        val responseDto =
            ReviewResponseDto.toDto(
                reviewWithLikes.review,
                reviewWithLikes.isLikedByUser
            )

        return GetReviewResponse(responseDto)
    }

    @Transactional(readOnly = true)
    fun getMyReviews(
        pageable: Pageable,
        userId: Long
    ): GetMyReviewsResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundUserPhoneNumberException()

        val reviewsWithLikes = reviewRepository.findMyReviews(user, pageable)

        return GetMyReviewsResponse(
            reviewsWithLikes.map {
                ReviewResponseDto.toDto(
                    it.review,
                    it.isLikedByUser
                )
            }
        )
    }
}
