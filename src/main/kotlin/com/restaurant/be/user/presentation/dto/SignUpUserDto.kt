package com.restaurant.be.user.presentation.dto

import com.restaurant.be.common.response.Token
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.presentation.dto.common.UserDto
import io.swagger.annotations.ApiModelProperty
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class SignUpUserRequest(
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotBlank(message = "아이디를 입력해 주세요.")
    @ApiModelProperty(value = "이메일 아이디", example = "test@gmail.com", required = true)
    val email: String,

    @field:NotBlank(message = "닉네임을 입력해 주세요.")
    @ApiModelProperty(value = "닉네임", example = "닉네임", required = true)
    val nickname: String,

    @ApiModelProperty(
        value = "프로필 이미지 URL",
        example = "https://test.com/test.jpg",
        required = true
    )
    val profileImageUrl: String
)

data class SignUpUserResponse(
    @Schema(description = "유저 정보")
    val userDto: UserDto,
    @Schema(description = "토큰 정보")
    val token: Token
) {
    constructor(user: User, token: Token) : this(
        userDto = UserDto(
            id = user.id ?: 0,
            email = user.email,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl
        ),
        token = token
    )
}
