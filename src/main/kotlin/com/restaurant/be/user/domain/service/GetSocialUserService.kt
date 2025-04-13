package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.user.presentation.dto.GetSocialUserDtoResponse
import com.restaurant.be.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GetSocialUserService(
    private val userRepository: UserRepository
) {
    fun getSocialUser(userId: Long): GetSocialUserDtoResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundUserException()
        return GetSocialUserDtoResponse(
            id = userId,
            socialType = user.socialUsers.firstOrNull()?.socialType,
            socialKey = user.socialUsers.firstOrNull()?.socialKey ?: ""
        )
    }
}
