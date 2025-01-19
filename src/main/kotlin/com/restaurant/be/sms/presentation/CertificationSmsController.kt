package com.restaurant.be.sms.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.sms.domain.service.CertificationSmsService
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import com.restaurant.be.sms.presentation.dto.VerifyCertificationSmsRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "05. SMS", description = "문자 서비스")
@RestController
@RequestMapping("/v1/sms")
class CertificationSmsController(
    private val certificationSmsService: CertificationSmsService
) {
    @PostMapping("/send-certification-number")
    @Operation(summary = "인증 문자 발송 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun sendCertificationNumber(
        @RequestBody @Valid
        request: SendCertificationSmsRequest
    ): CommonResponse<Unit> {
        certificationSmsService.sendCertificationNumber(request)
        return CommonResponse.success("인증 문자 발송 되었습니다.")
    }

    @PostMapping("/verify-certification-number")
    @Operation(summary = "인증 번호 검증 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Unit::class))]
    )
    fun verifyCertificationNumber(
        @RequestBody @Valid
        request: VerifyCertificationSmsRequest
    ): CommonResponse<Unit> {
        certificationSmsService.verifyCertificationNumber(request)
        return CommonResponse.success("인증번호 검증 되었습니다.")
    }
}
