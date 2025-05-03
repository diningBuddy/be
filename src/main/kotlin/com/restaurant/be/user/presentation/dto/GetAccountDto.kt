@file:Suppress("ktlint", "MatchingDeclarationName")

package com.restaurant.be.user.presentation.dto

import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.domain.constant.School
import com.restaurant.be.user.domain.constant.SocialType
import com.restaurant.be.user.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

data class GetAccountResponse(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long,
    @Schema(title = "가입일", example = "2025-01-01", required = true)
    val createdAt: LocalDateTime,
    @Schema(title = "로그인유형", example = "카카오", required = true)
    val loginType: SocialType?,
    @Schema(title = "생일", example = "1997-03-05", required = true)
    val birthday: LocalDate,
    @Schema(title = "성별", example = "MAN", required = true)
    val gender: Gender,
    @Schema(title = "전화번호", example = "010-1234-1234", required = true)
    val phoneNumber: String,
    @Schema(title = "캠퍼스 인증", example = "SKKU", required = true)
    val verifiedSchool: School?,
) {
    constructor(user: User) : this(
        id = user.id ?: 0,
        createdAt = user.createdAt,
        loginType = user.socialUsers
            .getOrNull(0)
            ?.socialType, // 카카오, 애플 인증을 둘 다 활성화한 고객은 어떻게?
        birthday = user.birthday,
        gender = user.gender,
        phoneNumber = user.phoneNumber,
        verifiedSchool = user.verifiedSchool
    )
}
