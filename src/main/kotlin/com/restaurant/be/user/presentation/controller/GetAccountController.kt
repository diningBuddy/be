package com.restaurant.be.user.presentation.controller

import com.restaurant.be.user.domain.service.GetAccountService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users")
class GetAccountController(
    private val getAccountService: GetAccountService,
) {
//    @GetMapping("/accounts")
//    @PreAuthorize("hasRole('USER')")
//    @Operation(summary = "계정정보조회 API")
//    @ApiResponse(
//        responseCode = "200",
//        description = "성공",
//        content = [Content(schema = Schema(implementation = GetMyUserResponse::class))],
//    )
//    fun getUserAccount(
//        @RequestParam nickname: String
//    ): CommonResponse<> {
//        val response = getAccountService.getAccount(nickname)
//        return CommonResponse.success(response)
//    }
}
