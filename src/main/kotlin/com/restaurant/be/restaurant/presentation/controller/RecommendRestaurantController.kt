package com.restaurant.be.restaurant.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.domain.service.RecommendRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.RecommendRestaurantResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "02. Restaurant Info", description = "음식점 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class RecommendRestaurantController(
    private val recommendRestaurantService: RecommendRestaurantService
) {
    @GetMapping("/recommend")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "gpt 기반 추천 음식점 리스트 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = RecommendRestaurantResponse::class))]
    )
    fun getRecommendRestaurants(principal: Principal): CommonResponse<RecommendRestaurantResponse> {
        val response = recommendRestaurantService.recommendRestaurants(principal.name.toLong())
        return CommonResponse.success(response)
    }
}
