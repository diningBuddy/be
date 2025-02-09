package com.restaurant.be.review.presentation.dto

import com.restaurant.be.review.presentation.dto.common.ReviewRequestDto
import com.restaurant.be.review.presentation.dto.common.ReviewResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateReviewRequest(
    @Schema(title = "리뷰 정보", required = true)
    val review: ReviewRequestDto
)

data class UpdateReviewResponse(
    @Schema(description = "리뷰 정보")
    val review: ReviewResponseDto
)
