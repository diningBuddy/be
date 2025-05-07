package com.restaurant.be.recent.repository

import com.restaurant.be.recent.domain.entity.RecentSearch
import com.restaurant.be.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecentSearchRepository : JpaRepository<RecentSearch, Long> {
    fun findAllByUserOrderByCreatedAt(user: User): List<RecentSearch>
}
