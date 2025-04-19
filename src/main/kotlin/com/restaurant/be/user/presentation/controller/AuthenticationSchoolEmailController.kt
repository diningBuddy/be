package com.restaurant.be.user.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.service.AuthenticationSchoolEmailService
import com.restaurant.be.user.presentation.dto.SendAuthenticationSchoolEmailRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "01. User Info", description = "유저 서비스")
@RestController
@RequestMapping("/v1/users/school-email/authentication")
class AuthenticationSchoolEmailController(
    private val authenticationSchoolEmailService: AuthenticationSchoolEmailService
) {

    @GetMapping
    @Operation(summary = "학교 인증 메일 발송 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun schoolEmailAuthentication(
        @RequestParam authenticationSchoolEmailUuid: String,
        @RequestParam userId: Long
    ): CommonResponse<Unit> {
        authenticationSchoolEmailService.schoolEmailAuthentication(authenticationSchoolEmailUuid, userId)
        return CommonResponse.success("학교 인증 완료")
    }

    @PostMapping("/send")
    @Operation(summary = "학교 인증 메일 발송 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun sendSchoolEmailAuthentication(
        principal: Principal,
        @RequestBody request: SendAuthenticationSchoolEmailRequest,
        servletRequest: HttpServletRequest
    ): CommonResponse<Unit> {
        val scheme = servletRequest.scheme // http or https
        val serverName = servletRequest.serverName // localhost or domain
        val serverPort = servletRequest.serverPort // 8080

        val baseUrl = "$scheme://$serverName:$serverPort"

        authenticationSchoolEmailService.sendAuthenticationSchoolEmail(principal.name.toLong(), baseUrl, request)
        return CommonResponse.success("학교 인증 메일 발송 되었습니다.")
    }
}
