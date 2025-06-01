package com.restaurant.be.searchhistory.repository

import com.restaurant.be.searchhistory.domain.entity.SearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchHistoryRepository : JpaRepository<SearchHistory, Long> {
    fun findByKeyword(content: String): SearchHistory?
    fun findTop8ByOrderByCountDesc(): List<SearchHistory>
}
