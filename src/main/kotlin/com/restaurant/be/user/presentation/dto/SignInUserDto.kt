@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class SignInUserRequest(

    @Schema(title = "휴대폰 번호 (아이디)", example = "01011111111", required = true)
    @field:NotEmpty(message = "휴대폰 번호 (아이디)는 필수 값 입니다.")
    val phoneNumber: String,

    @field:NotEmpty(message = "인증번호는 필수 값 입니다.")
    @Schema(title = "인증 번호", example = "1111", required = true)
    val certificationNumber: String
)
