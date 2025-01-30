package com.restaurant.be.user.presentation.dto.common

import io.swagger.v3.oas.annotations.media.Schema

data class UserDto(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long,
    @Schema(title = "닉네임", example = "닉네임", required = true)
    val nickname: String,
    @Schema(
        name = "프로필 이미지 URL",
        example = "https://test.com/test.jpg",
        required = true,
    )
    val profileImageUrl: String,
)
