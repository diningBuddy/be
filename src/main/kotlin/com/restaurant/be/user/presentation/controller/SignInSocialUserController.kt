package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.SignInSocialUserService
import com.restaurant.be.user.presentation.dto.SignInSocialUserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users/sign-in/social")
class SignInSocialUserController(
    private val signInSocialUserService: SignInSocialUserService
) {
    @PostMapping("/kakao")
    @Operation(summary = "카카오 로그인 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun kakaoSignIn(
        @RequestBody @Valid
        request: SignInSocialUserRequest,
        servletResponse: HttpServletResponse
    ): CommonResponse<Unit> {
        val token = signInSocialUserService.kakaoSignIn(request)
        servletResponse.setHeader("Authorization", "Bearer ${token.accessToken}")
        servletResponse.setHeader("RefreshToken", token.refreshToken)
        return CommonResponse.success("카카오 소셜 로그인 되었습니다.")
    }
}
