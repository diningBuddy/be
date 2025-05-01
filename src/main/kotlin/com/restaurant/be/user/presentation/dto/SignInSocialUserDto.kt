@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class SignInSocialUserRequest(

    @Schema(title = "access_token", example = "사용자 액세스 토큰 값", required = true)
    @field:NotEmpty(message = "access_token은 필수 값입니다.")
    val accessToken: String,

    @Schema(title = "refresh_token", example = "사용자 리프레시 토큰 값", required = true)
    @field:NotEmpty(message = "refresh_token은 필수 값입니다.")
    val refreshToken: String,

    @Schema(title = "token_type", example = "bearer", required = true)
    @field:NotEmpty(message = "token_type은 필수 값입니다.")
    val tokenType: String,

    @Schema(title = "id_token", example = "Base64로 인코딩 된 사용자 인증 정보", required = false)
    val idToken: String? = null,

    @Schema(title = "expires_in", example = "21599", required = false)
    val expiresIn: Int? = null,

    @Schema(title = "refresh_token_expires_in", example = "5183999", required = false)
    val refreshTokenExpiresIn: Int? = null,

    @Schema(title = "scope", example = "profile,account_email", required = false)
    val scope: String? = null,
)
