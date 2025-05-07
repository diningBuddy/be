package com.restaurant.be.recent.service

import com.restaurant.be.common.redis.RedisRepository
import org.springframework.stereotype.Service

@Service
class RecentQueryService(
    private val redisRepository: RedisRepository
) {
//    fun getRecentQueries(userId: Long): RecentQueriesResponse {
//        val queries = redisRepository.getSearchQueries(userId)
//
//        return RecentQueriesResponse(
//            recentQueries = queries?.map { RecentQueriesDto(it) } ?: listOf()
//        )
//    }
//
//    fun deleteRecentQueries(
//        userId: Long,
//        request: DeleteRecentQueriesRequest
//    ): RecentQueriesResponse {
//        if (request.query == null) {
//            redisRepository.deleteSearchQueries(userId)
//        } else {
//            redisRepository.deleteSpecificQuery(userId, request.query)
//        }
//
//        val queries = redisRepository.getSearchQueries(userId)
//
//        return RecentQueriesResponse(
//            recentQueries = queries?.map { RecentQueriesDto(it) } ?: listOf()
//        )
//    }
}
