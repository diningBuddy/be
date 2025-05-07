package com.restaurant.be.recent.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.recent.domain.entity.RecentSearch
import com.restaurant.be.recent.presentation.dto.RecentQueriesDto
import com.restaurant.be.recent.presentation.dto.RecentQueriesResponse
import com.restaurant.be.recent.repository.RecentSearchRepository
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RecentSearchService(
    private val recentSearchRepository: RecentSearchRepository,
    private val userRepository: UserRepository
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

    fun getRecentQueries(userId: Long): RecentQueriesResponse {
        val user = userRepository.findById(userId).orElseThrow { NotFoundUserException() }
        val queries = recentSearchRepository.findAllByUserOrderByCreatedAt(user)
        return RecentQueriesResponse(recentQueries = queries.map { RecentQueriesDto(it.getId(), it.keyword) })
    }
}
