package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.DuplicateUserPhoneNumberException
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import com.restaurant.be.user.repository.UserRepository
import com.restaurant.be.user.util.NickNameGenerateUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignUpUserService(
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
    private val redisRepository: RedisRepository
) {
    @Transactional
    fun createUser(request: SignUpUserRequest): User {
        if (userRepository.findByPhoneNumber(request.phoneNumber) != null) {
            throw DuplicateUserPhoneNumberException()
        }

        val nickname = generateUniqueNickname()
        val user = User.create(request, nickname)
        return userRepository.save(user)
    }

    @Transactional
    fun signUp(request: SignUpUserRequest): Token {
        val user = createUser(request)

        val token = tokenProvider.createTokens(user.getId(), user.roles)
        redisRepository.saveRefreshToken(user.getId(), token.refreshToken)
        return token
    }

    fun generateUniqueNickname(): String {
        val nickname = NickNameGenerateUtil.generateNickname()

        val duplicateNicknameUsers = userRepository.findByNicknameStartingWith(nickname)

        if (duplicateNicknameUsers.isEmpty()) {
            return nickname
        }

        val lastNumber =
            duplicateNicknameUsers
                .mapNotNull { duplicateNicknameUser ->
                    duplicateNicknameUser.nickname.replace(nickname, "").toIntOrNull()
                }.maxOrNull() ?: 0

        return "$nickname${lastNumber + 1}"
    }
}
