package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.SignInUserService
import com.restaurant.be.user.presentation.dto.SignInUserRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["01. User Info"], description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class SignInUserController(
    private val signInUserService: SignInUserService
) {

    @PostMapping("/sign-in")
    @ApiOperation(value = "로그인 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun signIn(
        @Valid @RequestBody
        request: SignInUserRequest,
        servletResponse: HttpServletResponse
    ): CommonResponse<Unit> {
        val token = signInUserService.signIn(request)
        servletResponse.setHeader("Authorization", "Bearer ${token.accessToken}")
        servletResponse.setHeader("RefreshToken", token.refreshToken)
        return CommonResponse.success("로그인 되었습니다.")
    }
}
