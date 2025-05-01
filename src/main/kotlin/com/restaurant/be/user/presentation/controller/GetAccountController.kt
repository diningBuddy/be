package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.GetAccountService
import com.restaurant.be.user.presentation.dto.GetAccountResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class GetAccountController(
    private val getAccountService: GetAccountService
) {
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "계정정보조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = GetAccountResponse::class))]
    )
    fun getUserAccount(
        @RequestParam id: Long
    ): CommonResponse<GetAccountResponse> {
        val response = getAccountService.getAccount(id)
        return CommonResponse.success(response)
    }
}
