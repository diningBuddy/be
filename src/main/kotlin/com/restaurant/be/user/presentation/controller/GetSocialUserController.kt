package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.GetSocialUserService
import com.restaurant.be.user.presentation.dto.CheckNicknameResponse
import com.restaurant.be.user.presentation.dto.GetSocialUserDtoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class GetSocialUserController(
    private val getSocialUserService: GetSocialUserService
) {
    @GetMapping("/social-users")
    @Operation(summary = "소셜 유저 정보조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = CheckNicknameResponse::class))]
    )
    fun getSocialUser(
        @RequestParam id: Long
    ): CommonResponse<GetSocialUserDtoResponse> {
        val response = getSocialUserService.getSocialUser(id)
        return CommonResponse.success(response)
    }
}
