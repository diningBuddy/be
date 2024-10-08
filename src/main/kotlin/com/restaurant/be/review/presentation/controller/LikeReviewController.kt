package com.restaurant.be.review.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.review.domain.service.LikeReviewService
import com.restaurant.be.review.presentation.dto.LikeReviewRequest
import com.restaurant.be.review.presentation.dto.LikeReviewResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Api(tags = ["03. Review Info"], description = "리뷰 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class LikeReviewController(
    val likeReviewService: LikeReviewService
) {

    @PostMapping("/reviews/{reviewId}/like")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "리뷰 좋아요 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = LikeReviewResponse::class))]
    )
    fun likeReview(
        principal: Principal,
        @PathVariable reviewId: Long,
        @RequestBody @Valid
        request: LikeReviewRequest
    ): CommonResponse<LikeReviewResponse> {
        val response = likeReviewService.likeReview(reviewId, request, principal.name)
        return CommonResponse.success(response)
    }
}
