@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class LogOutUserRequest(

    @Schema(title = "유저 id", example = "01011111111", required = true)
    @field:NotEmpty(message = "휴대폰 번호 (아이디)는 필수 값 입니다.")
    val id: Long,
)
