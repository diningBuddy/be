package com.restaurant.be.user.repository

import com.restaurant.be.user.domain.entity.SocialUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialUserRepository : JpaRepository<SocialUser, Long> {
    fun findBySocialKey(socialId: String): SocialUser?
}
