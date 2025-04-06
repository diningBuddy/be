package com.restaurant.be.user.repository

import com.restaurant.be.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(phoneNumber: String): User?

    fun findByNickname(nickname: String): User?

    fun findByNicknameStartingWith(nickname: String): List<User>
}
