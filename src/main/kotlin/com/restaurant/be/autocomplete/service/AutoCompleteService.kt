package com.restaurant.be.autocomplete.service

import com.restaurant.be.autocomplete.repository.AutoCompleteEsRepository
import com.restaurant.be.common.redis.RedisRepository
import org.springframework.stereotype.Service

@Service
class AutoCompleteService(
    private val autoCompleteEsRepository: AutoCompleteEsRepository,
    private val redisRepository: RedisRepository
) {
    private val maxSuggestions = 10
    private val MIN_INPUT_LENGTH = 2

    fun getSuggestions(prefix: String, userId: Long? = null): List<String> {
        if (prefix.length < MIN_INPUT_LENGTH) return emptyList()

        userId?.let {
            redisRepository.addSearchQuery(it, prefix)
        }

        return autoCompleteEsRepository.getSuggestions(prefix, maxSuggestions)
            .map { it.word }
    }

    fun recordSearchQuery(query: String, userId: Long) {
        if (query.length < MIN_INPUT_LENGTH) return
        redisRepository.addSearchQuery(userId, query)
    }
}
