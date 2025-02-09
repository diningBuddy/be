@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.review.presentation.dto

import com.restaurant.be.review.presentation.dto.common.ReviewResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class CreateReviewResponse(
    @Schema(title = "리뷰 정보", required = true)
    val review: ReviewResponseDto
)
