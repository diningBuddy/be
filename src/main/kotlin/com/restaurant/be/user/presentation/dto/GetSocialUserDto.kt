@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.SocialType
import com.restaurant.be.user.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

data class GetSocialUserDtoResponse(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long?,
    @Schema(title = "소셜 유형", example = "KAKAO", required = true)
    val socialType: SocialType?,
    @Schema(
        title = "소셜 키",
        example = "awfwf@w3wf",
        required = true
    )
    val socialKey: String?,
) {
    constructor(user: User) : this(
        id = user.id ?: 0,
        socialType = user.socialUsers.firstOrNull()?.socialType,
        socialKey = user.socialUsers.firstOrNull()?.socialKey ?: ""
    )
}
