package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.UpdateUserService
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class UpdateUserController(
    private val updateUserService: UpdateUserService
) {
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저 수정 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun updateUser(
        @Valid @RequestBody
        request: UpdateUserRequest
    ): CommonResponse<out Any> = updateUserService.updateUser(request)
}
