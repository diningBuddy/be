package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import com.restaurant.be.sms.domain.service.CertificationSmsService
import com.restaurant.be.sms.presentation.dto.VerifyCertificationSmsRequest
import com.restaurant.be.user.presentation.dto.LogOutUserRequest
import com.restaurant.be.user.presentation.dto.SignInUserRequest
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LogOutUserService(
    private val userRepository: UserRepository,
    private val redisRepository: RedisRepository
) {
    fun logout(request: LogOutUserRequest): Boolean {
        val user = userRepository.findById(request.id).orElseThrow {
            NotFoundUserException()
        }
        redisRepository.deleteRefreshToken(request.id)
        return true
    }

}
