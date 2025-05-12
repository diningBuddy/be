package com.restaurant.be.recent.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.recent.domain.service.RecentSearchService
import com.restaurant.be.recent.presentation.dto.DeleteRecentQueriesRequest
import com.restaurant.be.recent.presentation.dto.RecentQueriesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "04. Recent Info", description = "최근검색어 서비스")
@RestController
@RequestMapping("/v1/recents")
class RecentQueryController(
    private val recentSearchService: RecentSearchService
) {
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "최근검색어 조회 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = RecentQueriesResponse::class))]
    )
    fun getRecentQueries(principal: Principal): CommonResponse<RecentQueriesResponse> {
        val response = recentSearchService.getRecentQueries(principal.name.toLong())
        return CommonResponse.success(response)
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "최근검색어 삭제 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = RecentQueriesResponse::class))]
    )
    fun deleteRecentQueries(
        principal: Principal,
        @Valid @ModelAttribute
        request: DeleteRecentQueriesRequest
    ): CommonResponse<Unit> {
        recentSearchService.deleteRecentQueries(principal.name.toLong(), request)
        return CommonResponse.success()
    }
}
