package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.TokenRefreshService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["01. User Info"], description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class TokenRefreshController(
    private val tokenRefreshService: TokenRefreshService
) {

    @PostMapping("/token-reissue")
    @ApiOperation(value = "토큰 재발급 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun tokenRefresh(
        @RequestHeader("RefreshToken") refreshToken: String,
        httpServletResponse: HttpServletResponse
    ): CommonResponse<Unit> {
        val newToken = tokenRefreshService.tokenRefresh(refreshToken)
        httpServletResponse.setHeader("Authorization", "Bearer $newToken")
        return CommonResponse.success("토큰 재발급 되었습니다.")
    }

    @PostMapping("/refresh-token-reissue")
    @ApiOperation(value = "리프래시 토큰 재발급 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun refreshTokenRefresh(
        @RequestHeader("RefreshToken") refreshToken: String,
        httpServletResponse: HttpServletResponse
    ): CommonResponse<Unit> {
        val token = tokenRefreshService.refreshTokenRefresh(refreshToken)
        httpServletResponse.setHeader("Authorization", "Bearer ${token.accessToken}")
        httpServletResponse.setHeader("RefreshToken", token.refreshToken)
        return CommonResponse.success("리프래시 토큰 재발급 되었습니다.")
    }
}
