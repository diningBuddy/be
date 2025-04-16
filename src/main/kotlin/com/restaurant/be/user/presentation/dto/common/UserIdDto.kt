package com.restaurant.be.user.presentation.dto.common

import io.swagger.v3.oas.annotations.media.Schema

data class UserIdDto(
    @Schema(title = "유저 아이디", example = "1", required = true)
    val id: Long
)
