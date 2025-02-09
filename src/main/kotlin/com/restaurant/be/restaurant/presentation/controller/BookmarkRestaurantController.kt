package com.restaurant.be.restaurant.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.domain.service.BookmarkRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.BookmarkRestaurantRequest
import com.restaurant.be.restaurant.presentation.controller.dto.BookmarkRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetBookmarkRestaurantsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
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

@Tag(name = "02. Restaurant Info", description = "음식점 서비스")
@RestController
@RequestMapping("/v1/restaurants")
class BookmarkRestaurantController(
    private val bookmarkRestaurantService: BookmarkRestaurantService
) {
    @GetMapping("/bookmarks")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "좋아요한 음식점 리스트 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetBookmarkRestaurantsResponse::class))]
    )
    fun getMyLikeRestaurants(
        principal: Principal,
        pageable: Pageable
    ): CommonResponse<GetBookmarkRestaurantsResponse> {
        val response = bookmarkRestaurantService.getMyLikeRestaurant(
                pageable,
                principal.name.toLong()
            )
        return CommonResponse.success(response)
    }

    @PostMapping("/{restaurantId}/bookmark")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "음식점 좋아요 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = BookmarkRestaurantResponse::class))]
    )
    fun likeRestaurant(
        principal: Principal,
        @PathVariable restaurantId: Long,
        @RequestBody @Valid
        request: BookmarkRestaurantRequest
    ): CommonResponse<BookmarkRestaurantResponse> {
        val response =
            bookmarkRestaurantService.likeRestaurant(
                principal.name.toLong(),
                restaurantId,
                request.isLike
            )
        return CommonResponse.success(response)
    }
}
