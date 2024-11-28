package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.InvalidTokenException
import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TokenRefreshService(
    private val tokenProvider: TokenProvider,
    private val userRepository: UserRepository,
    private val redisRepository: RedisRepository
) {
    fun tokenRefresh(refreshToken: String): String {
        val id = tokenProvider.getIdFromToken(refreshToken)
        return redisRepository.getRefreshToken(id).let {
            if (it != refreshToken) {
                throw InvalidTokenException()
            }
            val user = userRepository.findByIdOrNull(id.toLong()) ?: throw NotFoundUserException()
            tokenProvider.createAccessToken(user.getId().toString(), user.roles)
        }
    }

    fun refreshTokenRefresh(refreshToken: String): Token {
        val userId = tokenProvider.getIdFromExpiredToken(refreshToken)
        val user = userRepository.findByIdOrNull(userId.toLong()) ?: throw NotFoundUserException()
        val token = tokenProvider.createTokens(user.phoneNumber, user.roles)
        redisRepository.saveRefreshToken(user.getId(), token.refreshToken)
        return token
    }
}
