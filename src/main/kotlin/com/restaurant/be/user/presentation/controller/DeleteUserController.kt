package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.DeleteUserService
import com.restaurant.be.user.domain.service.SignUpSocialUserService
import com.restaurant.be.user.presentation.dto.SignUpSocialUserRequest
import com.restaurant.be.user.presentation.dto.common.UserIdDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class DeleteUserController(
    private val deleteUserService: DeleteUserService
) {
    @DeleteMapping
    @Operation(summary = "계정탈퇴 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun withdraw(
        @RequestBody @Valid
        request: UserIdDto
    ): CommonResponse<Unit> {
        deleteUserService.withdraw(request)
        return CommonResponse.success("회원 탈퇴 되었습니다.")
    }
}
