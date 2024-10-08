package com.restaurant.be.restaurant.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.domain.service.LikeRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.GetLikeRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.LikeRestaurantRequest
import com.restaurant.be.restaurant.presentation.controller.dto.LikeRestaurantResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Api(tags = ["02. Restaurant Info"], description = "음식점 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class LikeRestaurantController(
    private val likeRestaurantService: LikeRestaurantService
) {

    @GetMapping("/my-like")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "좋아요한 음식점 리스트 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetLikeRestaurantsResponse::class))]
    )
    fun getMyLikeRestaurants(
        principal: Principal,
        pageable: Pageable
    ): CommonResponse<GetLikeRestaurantsResponse> {
        val response = likeRestaurantService.getMyLikeRestaurant(pageable, principal.name)
        return CommonResponse.success(response)
    }

    @PostMapping("/{restaurantId}/like")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "음식점 좋아요 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = LikeRestaurantResponse::class))]
    )
    fun likeRestaurant(
        principal: Principal,
        @PathVariable restaurantId: Long,
        @RequestBody @Valid
        request: LikeRestaurantRequest
    ): CommonResponse<LikeRestaurantResponse> {
        val response =
            likeRestaurantService.likeRestaurant(principal.name, restaurantId, request.isLike)
        return CommonResponse.success(response)
    }
}
