package com.restaurant.be.common.jwt

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.exception.WithdrawalUserException
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JwtUserRepositoryImpl(
    private val userRepository: UserRepository
) : JwtUserRepository {
    override fun validTokenById(id: String): Boolean {
        val user = userRepository.findByIdOrNull(id.toLong()) ?: return false
        return !user.isDeleted
    }

    override fun userRolesById(id: String): List<String> {
        val user = userRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundUserException()
        if (user.isDeleted) {
            throw WithdrawalUserException()
        }

        return user.roles
    }
}
