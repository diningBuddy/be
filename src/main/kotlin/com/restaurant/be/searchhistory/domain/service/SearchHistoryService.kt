package com.restaurant.be.searchhistory.domain.service

import com.restaurant.be.searchhistory.domain.entity.SearchHistory
import com.restaurant.be.searchhistory.presentation.dto.TopSearchHistoryResponse
import com.restaurant.be.searchhistory.repository.SearchHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchHistoryService(private val searchHistoryRepository: SearchHistoryRepository) {

    @Transactional
    fun saveSearchHistory(keyword: String) {
        searchHistoryRepository.findByKeyword(keyword)?.let {
            it.count += 1
            searchHistoryRepository.save(it)
        } ?: run {
            val searchHistory = SearchHistory.create(keyword = keyword)
            searchHistoryRepository.save(searchHistory)
        }
    }

    fun getTopSearchHistory(): TopSearchHistoryResponse {
        return TopSearchHistoryResponse.create(searchHistoryRepository.findTop8ByOrderByCountDesc())
    }
}
