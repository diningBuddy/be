package com.restaurant.be.review.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.review.domain.service.GetReviewService
import com.restaurant.be.review.presentation.dto.GetMyReviewsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "03. Review Info", description = "리뷰 서비스")
@RestController
@RequestMapping("/v1/restaurants/my-reviews")
class GetMyReviewController(
    val getReviewService: GetReviewService
) {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "내가 작성한 리뷰 리스트 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetMyReviewsResponse::class))]
    )
    fun getMyReview(
        principal: Principal,
        pageable: Pageable
    ): CommonResponse<GetMyReviewsResponse> {
        val response = getReviewService.getMyReviews(pageable, principal.name.toLong())
        return CommonResponse.success(response)
    }
}
