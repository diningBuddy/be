package com.restaurant.be.restaurant.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.domain.service.GetRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.MenuDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "02. Restaurant Info", description = "음식점 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class GetRestaurantController(
    private val getRestaurantService: GetRestaurantService
) {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "음식점 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetRestaurantsResponse::class))]
    )
    fun getRestaurants(
        principal: Principal,
        @ModelAttribute request: GetRestaurantsRequest,
        pageable: Pageable
    ): CommonResponse<GetRestaurantsResponse> {
        val response =
            getRestaurantService.getRestaurants(request, pageable, principal.name.toLong())
        return CommonResponse.success(response)
    }

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "음식점 상세 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetRestaurantResponse::class))]
    )
    fun getRestaurant(
        principal: Principal,
        @PathVariable restaurantId: Long
    ): CommonResponse<GetRestaurantResponse> {
        val response = getRestaurantService.getRestaurant(
            restaurantId,
            principal.name.toLong()
        )
        return CommonResponse.success(response)
    }

    @GetMapping("/{restaurantId}/menus")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "음식점 메뉴 전체 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = MenuDto::class))]
    )
    fun getRestaurantMenus(
        principal: Principal,
        @PathVariable restaurantId: Long
    ): CommonResponse<List<MenuDto>> {
        val response = getRestaurantService.getMenus(
            restaurantId,
            principal.name.toLong()
        )
        return CommonResponse.success(response)
    }
}
