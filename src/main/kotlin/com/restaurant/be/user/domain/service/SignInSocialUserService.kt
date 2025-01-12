package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import com.restaurant.be.user.presentation.dto.SignInSocialUserRequest
import com.restaurant.be.user.repository.KakaoRepository
import com.restaurant.be.user.repository.SocialUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignInSocialUserService(
    private val tokenProvider: TokenProvider,
    private val socialUserRepository: SocialUserRepository,
    private val kakaoRepository: KakaoRepository,
    private val redisRepository: RedisRepository
) {
    fun kakaoSignIn(request: SignInSocialUserRequest): Token {
        val kakaoKey = kakaoRepository.getKakaoKey(request.code)
        val user = socialUserRepository.findBySocialKey(kakaoKey)?.user ?: run {
            redisRepository.saveSocialKey(request.code, kakaoKey)
            throw NotFoundUserException()
        }
        val token = tokenProvider.createTokens(user.getId(), user.roles)
        redisRepository.saveRefreshToken(user.getId(), token.refreshToken)
        return token
    }
}
