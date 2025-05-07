package com.restaurant.be.search.domain.service

import com.restaurant.be.search.domain.entity.RecentSearch
import com.restaurant.be.search.repository.RecentSearchRepository
import com.restaurant.be.user.domain.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RecentSearchService(
    private val recentSearchRepository: RecentSearchRepository
) {

    @Transactional
    fun saveRecentSearch(user: User, keyword: String) {
        if (keyword.length > 13) {
            return
        }

        val recentSearch = RecentSearch.create(keyword = keyword, user = user)
        val recentSearchList = recentSearchRepository.findAllByUserOrderByCreatedAt(user)
        if (recentSearchList.size >= 8) {
            recentSearchRepository.delete(recentSearchList.first())
        }
        recentSearchRepository.save(recentSearch)
    }
}
