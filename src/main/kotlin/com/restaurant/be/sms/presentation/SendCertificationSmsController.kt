package com.restaurant.be.sms.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.sms.domain.service.SendCertificationSmsService
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["05. SMS"], description = "문자 서비스")
@RestController
@RequestMapping("/v1/sms")
class SendCertificationSmsController(
    private val sendCertificationSmsService: SendCertificationSmsService
) {
    @PostMapping("/certification")
    @ApiOperation(value = "인증 문자 발송 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = Void::class))]
    )
    fun checkNickname(
        @RequestBody @Valid
        request: SendCertificationSmsRequest
    ): CommonResponse<Void> {
        sendCertificationSmsService.sendCertification(request)
        return CommonResponse.success("인증 문자 발송 되었습니다.")
    }
}
