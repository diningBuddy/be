@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class SignUpSocialUserRequest(

    @Schema(title = "access_token", example = "사용자 액세스 토큰 값", required = true)
    @field:NotEmpty(message = "access_token은 필수 값입니다.")
    val accessToken: String,

    @Schema(title = "refresh_token", example = "사용자 리프레시 토큰 값", required = true)
    @field:NotEmpty(message = "refresh_token은 필수 값입니다.")
    val refreshToken: String,

    @Schema(title = "token_type", example = "bearer", required = true)
    @field:NotEmpty(message = "token_type은 필수 값입니다.")
    val tokenType: String,

    @Schema(title = "핸드폰 번호", example = "01011111111", required = true)
    @field:NotEmpty(message = "휴대폰 번호는 필수 값 입니다.")
    @field:Pattern(
        regexp = "^01[016789][0-9]{7,8}\$",
        message = "유효하지 않는 휴대폰 번호 입니다."
    )
    val phoneNumber: String,

    @Schema(title = "이름", example = "홍길동", required = true)
    @field:NotEmpty(message = "이름은 필수 값 입니다.")
    val name: String,

    @Schema(title = "생년월일", example = "1999-01-01", required = true)
    @field:NotEmpty(message = "생년월일은 필수 값 입니다.")
    val birthday: LocalDate,

    @Schema(title = "성별", example = "MAN", required = true)
    @field:NotEmpty(message = "성별은 필수 값 입니다.")
    var gender: Gender,

    @Schema(title = "id_token", example = "Base64로 인코딩 된 사용자 인증 정보", required = false)
    val idToken: String? = null,

    @Schema(title = "expires_in", example = "21599", required = false)
    val expiresIn: Int? = null,

    @Schema(title = "refresh_token_expires_in", example = "5183999", required = false)
    val refreshTokenExpiresIn: Int? = null,

    @Schema(title = "scope", example = "profile,account_email", required = false)
    val scope: String? = null
)
