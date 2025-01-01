package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.DuplicateSocialUserException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.user.domain.entity.SocialUser
import com.restaurant.be.user.presentation.dto.SignUpSocialUserRequest
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import com.restaurant.be.user.repository.SocialUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignUpSocialUserService(
    private val signUpUserService: SignUpUserService,
    private val socialUserRepository: SocialUserRepository,
    private val redisRepository: RedisRepository
) {
    fun kakaoSignUp(request: SignUpSocialUserRequest) {
        val kakaoKey = redisRepository.getSocialKey(request.socialCode)
        socialUserRepository.findBySocialKey(kakaoKey)?.let { throw DuplicateSocialUserException() }

        val user = signUpUserService.signUp(
            SignUpUserRequest(
                phoneNumber = request.phoneNumber,
                name = request.name,
                birthday = request.birthday,
                gender = request.gender
            )
        )

        val socialUser = SocialUser.createKakao(
            user = user,
            socialKey = kakaoKey
        )

        socialUserRepository.save(socialUser)
    }

}
