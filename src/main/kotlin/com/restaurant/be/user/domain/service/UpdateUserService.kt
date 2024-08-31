package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserEmailException
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import com.restaurant.be.user.presentation.dto.UpdateUserResponse
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class UpdateUserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun updateUser(request: UpdateUserRequest, email: String): UpdateUserResponse {
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundUserEmailException()
        user.updateUser(request)
        userRepository.save(user)

        return UpdateUserResponse(user)
    }
}
