package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
class UpdateUserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun updateUser(req: UpdateUserRequest): CommonResponse<out Any> {
        userRepository
            .findById(req.id)
            .orElseThrow { NotFoundUserException() }
            .apply { update(req) }
        return CommonResponse.success("유저데이터 수정완료")
    }
}
