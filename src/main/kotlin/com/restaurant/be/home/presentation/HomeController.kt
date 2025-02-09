package com.restaurant.be.home.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.home.domain.service.HomeService
import com.restaurant.be.home.presentation.dto.HomeRequest
import com.restaurant.be.home.presentation.dto.HomeResponse
import com.restaurant.be.popular.domain.service.GetPopularRestaurantService
import com.restaurant.be.popular.presentation.dto.GetPopularRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "06. Home Info", description = "홈 서비스")
@RestController
@RequestMapping("/v1/home")
class HomeController(
    private val homeService: HomeService,
    private val getPopularRestaurantService: GetPopularRestaurantService
) {

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "메인 페이지 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = HomeResponse::class))]
    )
    fun getRecommendations(
        principal: Principal,
        @ModelAttribute request: HomeRequest
    ): CommonResponse<HomeResponse> {
        val response = homeService.getHome(
            request,
            principal.name.toLong()
        )
        return CommonResponse.success(response)
    }

    @GetMapping("/kakaos/popular-restaurants")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "음식점 랭킹 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetRestaurantsResponse::class))]
    )
    fun getPopularRestaurants(
        principal: Principal,
        pageable: Pageable
    ): CommonResponse<GetPopularRestaurantResponse> {
        val response =
            getPopularRestaurantService.getPopularRestaurants(pageable)
        return CommonResponse.success(response)
    }
    // TODO 홈 하단 섹션 자세히 나오도록하는 API(더보기) // 처음 20개, 이후 10개씩
}
