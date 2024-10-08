package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.UpdateUserService
import com.restaurant.be.user.presentation.dto.UpdatePasswordRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["01. User Info"], description = "유저 서비스")
@RestController
@RequestMapping("/v1/users/password")
class UpdatePasswordController(
    private val updateUserService: UpdateUserService
) {

    @PatchMapping
    @ApiOperation(value = "비밀번호 변경 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    fun passwordUpdate(
        @Valid @RequestBody
        request: UpdatePasswordRequest
    ): CommonResponse<Unit> {
        updateUserService.updatePassword(request)
        return CommonResponse.success()
    }
}
