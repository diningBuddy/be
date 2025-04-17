package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.response.ErrorCode
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
class UpdateUserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun updateUser(req: UpdateUserRequest): CommonResponse<out Any> {
        try {
            val user = userRepository.findByIdOrNull(req.id) ?: throw NotFoundUserException()
            user.update(req.nickname, req.name, req.profileImageUrl, req.gender)
        } catch (e: Exception) {
            return CommonResponse.fail("유저데이터 수정실패 e: " + e.message, errorCode = ErrorCode.COMMON_INVALID_PARAMETER)
        }
        return CommonResponse.success("유저데이터 수정완료")
    }
}
