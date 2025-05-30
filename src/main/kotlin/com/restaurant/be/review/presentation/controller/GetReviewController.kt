package com.restaurant.be.review.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.review.domain.service.GetReviewService
import com.restaurant.be.review.presentation.dto.GetReviewResponse
import com.restaurant.be.review.presentation.dto.GetReviewsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "03. Review Info", description = "리뷰 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class GetReviewController(
    private val getReviewService: GetReviewService
) {
    @GetMapping("/{restaurantId}/reviews")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "리뷰 리스트 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetReviewResponse::class))]
    )
    fun getReviews(
        principal: Principal,
        @PathVariable restaurantId: Long,
        pageable: Pageable
    ): CommonResponse<GetReviewsResponse> {
        val response = getReviewService.getReviews(pageable, restaurantId, principal.name.toLong())
        return CommonResponse.success(response)
    }

    @GetMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "리뷰 단건 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetReviewResponse::class))]
    )
    fun getOneReview(
        principal: Principal,
        @PathVariable reviewId: Long
    ): CommonResponse<GetReviewResponse> {
        val response = getReviewService.getReview(reviewId, principal.name.toLong())
        return CommonResponse.success(response)
    }
}
