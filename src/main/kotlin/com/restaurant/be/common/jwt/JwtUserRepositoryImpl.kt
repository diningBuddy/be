package com.restaurant.be.common.jwt

import com.restaurant.be.common.exception.NotFoundUserPhoneNumberException
import com.restaurant.be.common.exception.WithdrawalUserException
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JwtUserRepositoryImpl(
    private val userRepository: UserRepository
) : JwtUserRepository {

    override fun validTokenByPhoneNumber(phoneNumber: String): Boolean {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: return false
        return !user.withdrawal
    }

    override fun userRolesByPhoneNumber(phoneNumber: String): List<String> {
        val user = userRepository.findByPhoneNumber(phoneNumber) ?: throw NotFoundUserPhoneNumberException()
        if (user.withdrawal) {
            throw WithdrawalUserException()
        }

        return user.roles
    }
}
