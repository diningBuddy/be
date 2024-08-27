package com.restaurant.be.user.presentation.dto.certification

import com.restaurant.be.user.domain.entity.User
import io.swagger.annotations.ApiModelProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CertifyUserRequest(
    @field:Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})\$", message = "알맞은 전화번호 형식이 아닙니다.")
    @field:NotBlank(message = "전화번호를 입력해 주세요.")
    @ApiModelProperty(value = "전화번호(하이픈 제외)", example = "01012345678", required = true)
    val phoneNumber: String,

    @field:Size(min = 6, max = 6, message = "인증번호 6자리를 정확히 입력해주세요")
    @field:NotBlank(message = "인증번호를 입력해주세요")
    @ApiModelProperty(value = "여섯자리 수", example = "012345", required = true)
    val certificationNumber: String
) {
    fun toUserEntity() = User(
        phoneNumber = phoneNumber,
        createdAt = LocalDateTime.now(),
    )
}
data class CertifyUserResponse(
    @Schema(description = "인증 완료 유무")
    val isAuthenticated: Boolean,
    @Schema(description = "인증 결과 메시지")
    val message: String

)
