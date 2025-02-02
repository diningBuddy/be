@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema

data class GetUserResponse(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long,
    @Schema(title = "닉네임", example = "닉네임", required = true)
    val nickname: String,
    @Schema(
        title = "프로필 이미지 URL",
        example = "https://test.com/test.jpg",
        required = true
    )
    val profileImageUrl: String
) {
    constructor(user: User) : this(
        id = user.id ?: 0,
        nickname = user.nickname,
        profileImageUrl = user.profileImageUrl ?: ""
    )
}

data class GetMyUserResponse(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long,
    @Schema(title = "닉네임", example = "닉네임", required = true)
    val nickname: String,
    @Schema(
        title = "프로필 이미지 URL",
        example = "https://test.com/test.jpg",
        required = true
    )
    val profileImageUrl: String,

    @Schema(title = "포인트", example = "1000", required = true)
    val point: Long
) {
    constructor(user: User) : this(
        id = user.id ?: 0,
        nickname = user.nickname,
        profileImageUrl = user.profileImageUrl ?: "",
        point = 1000
    )
}
