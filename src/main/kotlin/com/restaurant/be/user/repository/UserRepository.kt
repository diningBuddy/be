package com.restaurant.be.user.repository

import com.restaurant.be.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(phoneNumber: String): User?

    fun findByNickname(nickname: String): User?
}
