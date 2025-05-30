package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import com.restaurant.be.sms.domain.service.CertificationSmsService
import com.restaurant.be.sms.presentation.dto.VerifyCertificationSmsRequest
import com.restaurant.be.user.presentation.dto.SignInUserRequest
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignInUserService(
    private val certificationSmsService: CertificationSmsService,
    private val tokenProvider: TokenProvider,
    private val userRepository: UserRepository,
    private val redisRepository: RedisRepository
) {
    fun signIn(request: SignInUserRequest): Token {
        val user = userRepository.findByPhoneNumber(request.phoneNumber) ?: throw NotFoundUserException()
        if (user.isDeleted) { // soft deleted된 유저 판별
            throw NotFoundUserException()
        }
        certificationSmsService.verifyCertificationNumber(VerifyCertificationSmsRequest(request.phoneNumber, request.certificationNumber))
        val token = tokenProvider.createTokens(user.getId(), user.roles)
        redisRepository.saveRefreshToken(user.getId(), token.refreshToken)
        return token
    }
}
