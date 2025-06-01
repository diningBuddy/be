package com.restaurant.be.searchhistory.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.searchhistory.domain.service.SearchHistoryService
import com.restaurant.be.searchhistory.presentation.dto.TopSearchHistoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "08. 검색 기록", description = "검색 기록 서비스")
@RestController
@RequestMapping("/v1/search-history")
class SearchHistoryController(
    private val searchHistoryService: SearchHistoryService
) {
    @GetMapping("/top")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "인기 검색어 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = TopSearchHistoryResponse::class))]
    )
    fun getTopSearchHistory(): CommonResponse<TopSearchHistoryResponse> {
        val response = searchHistoryService.getTopSearchHistory()
        return CommonResponse.success(response)
    }
}
