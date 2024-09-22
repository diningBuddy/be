package com.restaurant.be.common.util

import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.security.Principal
import java.time.LocalDateTime

fun setUpUser(phoneNumber: String, userRepository: UserRepository): User {
    val user = User(
        phoneNumber = phoneNumber,
        profileImageUrl = "",
        nickname = "test_nickname",
        createdAt = LocalDateTime.now()
    )
    userRepository.save(user)

    SecurityContextHolder.getContext().authentication =
        PreAuthenticatedAuthenticationToken(
            Principal { phoneNumber },
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

    return user
}
