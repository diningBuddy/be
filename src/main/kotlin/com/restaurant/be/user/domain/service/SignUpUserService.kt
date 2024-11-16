package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.DuplicateUserPhoneNumberException
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import com.restaurant.be.user.repository.UserRepository
import com.restaurant.be.user.util.NickNameGenerateUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignUpUserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun signUp(request: SignUpUserRequest) {
        if (userRepository.findByPhoneNumber(request.phoneNumber) != null) {
            throw DuplicateUserPhoneNumberException()
        }

        val nickname = generateUniqueNickname()
        val user = User.create(request, nickname)
        userRepository.save(user)
    }

    fun generateUniqueNickname(): String {
        val nickname = NickNameGenerateUtil.generateNickname()

        val duplicateNicknameUsers = userRepository.findByNicknameStartingWith(nickname)

        if (duplicateNicknameUsers.isEmpty()) {
            return nickname
        }

        val lastNumber = duplicateNicknameUsers
            .mapNotNull { duplicateNicknameUser ->
                duplicateNicknameUser.nickname.replace(nickname, "").toIntOrNull()
            }.maxOrNull() ?: 0

        return "$nickname${lastNumber + 1}"
    }
}
