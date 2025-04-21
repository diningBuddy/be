package com.restaurant.be.kakao.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.kakao.domain.service.GetPopularRestaurantService
import com.restaurant.be.kakao.presentation.dto.CategoryParam
import com.restaurant.be.restaurant.domain.service.GetRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/v1/kakao")
@Tag(name = "07. Restaurant Rank Info", description = "음식점 랭킹 서비스")
class PopularRestaurantController(
    private val getPopularRestaurantService: GetPopularRestaurantService,
    private val getRestaurantService: GetRestaurantService
) {
    @GetMapping("/popular-restaurants")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "카카오 맛집 랭킹 조회 API",
        parameters = [
            Parameter(
                name = "categoryParam",
                description = "카테고리 (전체, 한식, 양식, 일식, 중식, 아시안, 카페)",
                required = true,
                example = "ALL"
            )
        ]
    )
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetRestaurantsResponse::class))]
    )
    fun getPopularRestaurants(
        principal: Principal,
        @ModelAttribute request: GetRestaurantsRequest,
        pageable: Pageable,
        @RequestParam(required = true, defaultValue = "ALL") categoryParam: CategoryParam
    ): CommonResponse<GetRestaurantsResponse> {
        return CommonResponse.success(
            getRestaurantService.getPopularRestaurants(
                request = request,
                pageable = pageable,
                userId = principal.name.toLong(),
                restaurantIds = getPopularRestaurantService.getRestaurantIdsByScrapCategory(categoryParam)
            )
        )
    }
}
