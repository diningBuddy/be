package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.user.presentation.dto.common.UserIdDto
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
class DeleteUserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun withdraw(request: UserIdDto) {
        userRepository
            .findById(request.id)
            .orElseThrow { NotFoundUserException() }
            .apply { isDeleted = true }
    }
}
