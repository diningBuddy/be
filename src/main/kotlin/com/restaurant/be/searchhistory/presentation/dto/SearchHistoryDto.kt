package com.restaurant.be.searchhistory.presentation.dto

import com.restaurant.be.searchhistory.domain.entity.SearchHistory
import io.swagger.v3.oas.annotations.media.Schema

data class TopSearchHistoryResponse(
    val recentQueries: List<TopSearchHistoryDto>
) {
    companion object {
        fun create(searchHistoryList: List<SearchHistory>): TopSearchHistoryResponse {
            return TopSearchHistoryResponse(
                recentQueries = searchHistoryList.map {
                    TopSearchHistoryDto(query = it.keyword)
                }
            )
        }
    }
}

data class TopSearchHistoryDto(
    @Schema(description = "인기 검색어")
    val query: String
)
