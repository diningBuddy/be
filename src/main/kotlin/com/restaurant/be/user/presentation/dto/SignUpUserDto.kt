@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class SignUpUserRequest(

    @Schema(name = "핸드폰 번호", example = "01011111111", required = true)
    @field:NotEmpty(message = "휴대폰 번호는 필수 값 입니다.")
    @field:Pattern(
        regexp = "^01[016789][0-9]{7,8}\$",
        message = "유효하지 않는 휴대폰 번호 입니다."
    )
    val phoneNumber: String,

    @Schema(name = "이름", example = "홍길동", required = true)
    @field:NotEmpty(message = "이름은 필수 값 입니다.")
    val name: String,

    @Schema(name = "생년월일", example = "1999-01-01", required = true)
    @field:NotEmpty(message = "생년월일은 필수 값 입니다.")
    val birthday: LocalDate,

    @Schema(name = "성별", example = "MAN", required = true)
    @field:NotEmpty(message = "성별은 필수 값 입니다.")
    var gender: Gender
)
