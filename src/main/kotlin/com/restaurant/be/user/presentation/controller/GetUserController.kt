package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.GetUserService
import com.restaurant.be.user.presentation.dto.GetUserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class GetUserController(
    private val getUserService: GetUserService
) {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "로그인 한 유저 정보 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetUserResponse::class))]
    )
    fun getMyUser(
        principal: Principal
    ): CommonResponse<GetUserResponse> {
        val response = getUserService.getUser(principal.name.toLong())
        return CommonResponse.success(response)
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "타 유저 정보 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetUserResponse::class))]
    )
    fun getUser(
        @PathVariable userId: Long
    ): CommonResponse<GetUserResponse> {
        val response = getUserService.getUser(userId)
        return CommonResponse.success(response)
    }
}
