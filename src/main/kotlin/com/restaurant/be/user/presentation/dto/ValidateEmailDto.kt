package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.presentation.dto.common.EmailSendType
import io.swagger.annotations.ApiModelProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class ValidateEmailRequest(
    @field:NotEmpty(message = "이메일은 필수 값 입니다.")
    @field:Email(message = "유효하지 않는 이메일 입니다.")
    @ApiModelProperty(value = "이메일", example = "test@test.com", required = true)
    val email: String,
    @ApiModelProperty(value = "인증번호", required = true)
    val code: String,
    @ApiModelProperty(
        value = "이메일 전송 타입",
        example = "SIGN_UP",
        required = true
    )
    val sendType: EmailSendType
)

data class ValidateEmailResponse(
    val token: String
)
