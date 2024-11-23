@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.annotations.ApiModelProperty
import jakarta.validation.constraints.NotEmpty

data class SignInUserRequest(

    @ApiModelProperty(value = "ID", example = "01011111111", required = true)
    @field:NotEmpty(message = "ID는 필수 값 입니다.")
    val id: String,

    @field:NotEmpty(message = "인증번호는 필수 값 입니다.")
    @ApiModelProperty(value = "인증 번호", example = "1111", required = true)
    val certificationNumber: String
)
