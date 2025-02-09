@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.sms.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class SendCertificationSmsRequest(
    @field:NotEmpty(message = "휴대폰 번호는 필수 값 입니다.")
    @field:Pattern(
        regexp = "^01[016789][0-9]{7,8}\$",
        message = "유효하지 않는 휴대폰 번호 입니다."
    )
    @Schema(title = "휴대폰 번호", example = "01012341234", required = true)
    val phoneNumber: String
)

data class VerifyCertificationSmsRequest(
    @field:NotEmpty(message = "휴대폰 번호는 필수 값 입니다.")
    @field:Pattern(
        regexp = "^01[016789][0-9]{7,8}\$",
        message = "유효하지 않는 휴대폰 번호 입니다."
    )
    @Schema(title = "휴대폰 번호", example = "01012341234", required = true)
    val phoneNumber: String,

    @field:NotEmpty(message = "인증번호는 필수 값 입니다.")
    @Schema(title = "인증 번호", example = "1111", required = true)
    val certificationNumber: String
)
