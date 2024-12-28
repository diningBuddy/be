package com.restaurant.be.common.jwt

import com.restaurant.be.common.exception.NotFoundUserPhoneNumberException
import com.restaurant.be.common.exception.WithdrawalUserException
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JwtUserRepositoryImpl(
    private val userRepository: UserRepository
) : JwtUserRepository {
    override fun validTokenById(phoneNumber: String): Boolean {
        val user = userRepository.findByIdOrNull(phoneNumber.toLong()) ?: return false
        return !user.isDeleted
    }

    override fun userRolesById(phoneNumber: String): List<String> {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: throw NotFoundUserPhoneNumberException()
        if (user.isDeleted) {
            throw WithdrawalUserException()
        }

        return user.roles
    }
}
