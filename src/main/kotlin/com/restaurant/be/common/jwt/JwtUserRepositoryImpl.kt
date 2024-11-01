package com.restaurant.be.common.jwt

import com.restaurant.be.common.exception.NotFoundUserPhoneNumberException
import com.restaurant.be.common.exception.WithdrawalUserException
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class JwtUserRepositoryImpl(
    private val userRepository: UserRepository
) : JwtUserRepository {
    override fun validTokenByPhoneNumber(phoneNumber: String): Boolean {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: return false
        return !user.isDeleted
    }

    override fun userRolesByPhoneNumber(phoneNumber: String): List<String> {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: throw NotFoundUserPhoneNumberException()
        if (user.isDeleted) {
            throw WithdrawalUserException()
        }

        return user.roles
    }
}
