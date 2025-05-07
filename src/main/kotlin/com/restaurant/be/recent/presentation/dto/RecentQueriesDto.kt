package com.restaurant.be.recent.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class DeleteRecentQueriesRequest(
    @Schema(description = "삭제할 최근 검색어 ID 리스트")
    @field:NotNull(message = "삭제 할 최근 검색어 ID 리스트는 필수입니다.")
    val deleteRecentIdList: List<Long>
)

data class RecentQueriesResponse(
    val recentQueries: List<RecentQueriesDto>
)

data class RecentQueriesDto(
    @Schema(description = "최근 검색어 ID")
    val id: Long,
    @Schema(description = "최근 검색어")
    val query: String
)
