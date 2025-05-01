package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.user.presentation.dto.GetAccountResponse
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetAccountService(
    private val userRepository: UserRepository
) {
    fun getAccount(userId: Long): GetAccountResponse {
        val user =
            userRepository.findById(userId).orElseThrow {
                NotFoundUserException()
            }
        return GetAccountResponse(user = user)
    }
}
