package com.restaurant.be.user.repository

import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.domain.entity.User
import kotlinx.datetime.LocalDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(phoneNumber: String): User?

    fun findByNickname(nickname: String): User?

    fun findByNicknameStartingWith(nickname: String): List<User>

    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname, u.name = :name, u.profileImageUrl = :profileImageUrl, u.gender = :gender WHERE u.id = :id")
    fun updateUser(
            @Param("id") id: Long,
            @Param("profileImageUrl") profileImageUrl: String,
            @Param("nickname") nickname: String,
            @Param("name") name: String,
            @Param("gender") gender: Gender
    ): Int
}
