@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.SocialType
import com.restaurant.be.user.domain.entity.SocialUser
import com.restaurant.be.user.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

data class GetSocialUserDtoResponse(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long?,
    val socialUsers: List<SocialUserDto>
){
    constructor(user: User) : this(
        id = user.id,
        socialUsers = user.socialUsers
            .map { SocialUserDto(it) }
    )
}

data class SocialUserDto(
    @Schema(title = "소셜 유형", example = "KAKAO", required = true)
    val socialType: SocialType,
) {
    constructor(entity: SocialUser) : this(
        socialType = entity.socialType
    )
}
