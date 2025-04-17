@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class UpdateUserRequest(
        @Schema(title = "유저 아이디", example = "1", required = true)
        @field:NotEmpty(message = "userId는 필수 값 입니다.")
        val id: Long,

        @Schema(title = "프로필 이미지", example = "example.jpg", required = true)
        val profileImageUrl: String,

        @Schema(title = "닉네임", example = "치맥살인마", required = true)
        @field:NotEmpty(message = "닉네임은 필수 값 입니다.")
        val nickname: String,

        @Schema(title = "이름", example = "홍길동", required = true)
        @field:NotEmpty(message = "이름은 필수 값 입니다.")
        val name: String,

        @Schema(title = "성별", example = "MAN", required = true)
        @field:NotEmpty(message = "성별은 필수 값 입니다.")
        var gender: Gender
)
