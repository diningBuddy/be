package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.sms.domain.service.CertificationSmsService
import com.restaurant.be.sms.presentation.dto.VerifyCertificationSmsRequest
import com.restaurant.be.user.presentation.dto.SignInUserRequest
import com.restaurant.be.user.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
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
    fun signIn(request: SignInUserRequest, servletResponse: HttpServletResponse) {
        val user = userRepository.findByPhoneNumber(request.id) ?: throw NotFoundUserException()
        certificationSmsService.verifyCertificationNumber(VerifyCertificationSmsRequest(request.id, request.certificationNumber))
        val token = tokenProvider.createTokens(user.phoneNumber, listOf("ROLE_USER"))
        redisRepository.saveRefreshToken(user.phoneNumber, token.refreshToken)
        servletResponse.setHeader("Authorization", "Bearer ${token.accessToken}")
        servletResponse.setHeader("RefreshToken", token.refreshToken)
    }
}
