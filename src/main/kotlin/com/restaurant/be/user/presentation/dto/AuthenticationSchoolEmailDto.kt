@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class SendAuthenticationSchoolEmailRequest(

    @Schema(description = "학교 이메일", example = "test@test.com", required = true)
    @field:NotBlank(message = "학교 이메일을 입력해 주세요.")
    val email: String
)
