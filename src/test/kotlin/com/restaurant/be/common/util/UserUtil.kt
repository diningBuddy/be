package com.restaurant.be.common.util

import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.security.Principal
import java.time.LocalDateTime

fun setUpUser(email: String, userRepository: UserRepository): User {
    val user = User(
        email = email,
        nickname = "nickname",
        createdAt = LocalDateTime.now(),
        profileImageUrl = "example.jpg",
        phoneNumber = "01012345678"
    )
    userRepository.save(user)

    SecurityContextHolder.getContext().authentication =
        PreAuthenticatedAuthenticationToken(
            Principal { email },
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

    return user
}
