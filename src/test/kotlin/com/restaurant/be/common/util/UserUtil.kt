package com.restaurant.be.common.util

import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.domain.constant.SocialType
import com.restaurant.be.user.domain.entity.SocialUser
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.repository.SocialUserRepository
import com.restaurant.be.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.security.Principal
import java.time.LocalDate

fun setUpUser(
    phoneNumber: String,
    userRepository: UserRepository
): User {
    val user =
        User(
            phoneNumber = phoneNumber,
            nickname = "test_nickname",
            name = "test_name",
            gender = Gender.MAN,
            birthday = LocalDate.now(),
            isTermsAgreed = true
        )
    userRepository.save(user)

    SecurityContextHolder.getContext().authentication =
        PreAuthenticatedAuthenticationToken(
            Principal { user.id.toString() },
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

    return user
}

fun setUpUser(
    phoneNumber: String,
    nickName: String,
    userRepository: UserRepository
): User {
    val user =
        User(
            phoneNumber = phoneNumber,
            nickname = nickName,
            name = "test_name",
            gender = Gender.MAN,
            birthday = LocalDate.now(),
            isTermsAgreed = true
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

fun setUpSocialUser(
    phoneNumber: String,
    kakaoKey: String,
    userRepository: UserRepository,
    socialUserRepository: SocialUserRepository
): User {
    val user = setUpUser(phoneNumber, userRepository)
    val socialUser = SocialUser(
        user = user,
        socialKey = kakaoKey,
        socialType = SocialType.KAKAO
    )
    socialUserRepository.save(socialUser)
    return user
}
