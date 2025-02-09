@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class SignInSocialUserRequest(

    @Schema(title = "code", example = "code", required = true)
    @field:NotEmpty(message = "code 는 필수 값 입니다.")
    val code: String,
)
