package com.restaurant.be.common.redis

import com.restaurant.be.common.exception.ExpiredCertificationNumberException
import com.restaurant.be.common.exception.InvalidEmailCodeException
import com.restaurant.be.common.exception.InvalidTokenException
import com.restaurant.be.common.exception.NotFoundAuthenticationSchoolEmailCodeException
import com.restaurant.be.common.exception.NotFoundSocialKeyException
import com.restaurant.be.common.exception.SendEmailException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Component
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.refresh-token-validity-in-seconds}")
    private val refreshTokenValidityInSeconds: Long
) {
    companion object {
        private const val SEARCH_PREFIX = "SR:" // 검색어를 저장할 때 사용할 키 접두사
        private const val MAX_HISTORY = 5 // 저장할 최대 검색어 수
        private const val RECOMMENDATION_PREFIX = "RECOMMENDATION:"
        const val CERTIFICATION_PREFIX = "CERTIFICATION:"
        const val REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:"
        const val SOCIAL_USER_PREFIX = "SOCIAL_USER:"
        const val AUTHENTICATION_SCHOOL_EMAIL_PREFIX = "AUTHENTICATION_SCHOOL_EMAIL:"
    }

    // 사용자별 추천 식당을 조회하는 메서드
    fun getRecommendation(userId: Long): List<Long> {
        val values = redisTemplate.opsForValue()
        val key = "$RECOMMENDATION_PREFIX$userId"
        val recommendations = values.get(key)
        if (recommendations != null) {
            return recommendations.split(",").map { it.toLong() }
        }

        val defaultKey = RECOMMENDATION_PREFIX + "0"
        val defaultRecommendations = values.get(defaultKey)
        if (defaultRecommendations != null) {
            return defaultRecommendations.split(",").map { it.toLong() }
        }

        return emptyList()
    }

    // 사용자별 검색어를 추가하는 메서드
    fun addSearchQuery(
        userId: Long,
        query: String
    ) {
        val key = "$SEARCH_PREFIX$userId" // 사용자별 고유 키 생성
        val existingQueries = getSearchQueries(userId) ?: emptyList()

        if (!existingQueries.contains(query)) {
            redisTemplate.opsForList().leftPush(key, query) // 리스트의 왼쪽(앞)에 쿼리 추가
            redisTemplate.opsForList().trim(key, 0, (MAX_HISTORY - 1).toLong()) // 리스트의 크기를 5개로 제한
            redisTemplate.expire(key, 30, TimeUnit.DAYS) // 키의 만료 시간을 30일로 설정
        }
    }

    // 사용자별 저장된 검색어 목록을 불러오는 메서드
    fun getSearchQueries(userId: Long): List<String>? {
        val key = "$SEARCH_PREFIX$userId"
        return redisTemplate.opsForList().range(key, 0, (MAX_HISTORY - 1).toLong()) // 저장된 검색어 목록 반환
    }

    // 특정 사용자의 모든 검색어 데이터를 삭제하는 메서드
    fun deleteSearchQueries(userId: Long) {
        val key = "$SEARCH_PREFIX$userId"
        redisTemplate.delete(key)
    }

    // 특정 검색어만 삭제하는 메서드
    fun deleteSpecificQuery(
        userId: Long,
        queryToRemove: String
    ) {
        val key = "$SEARCH_PREFIX$userId"
        val listOperations = redisTemplate.opsForList()
        val count = listOperations.remove(key, 0, queryToRemove) // 지정된 요소를 리스트에서 모두 제거
        if (count != null) {
            if (count > 0) {
                println("$count instances of '$queryToRemove' removed from the list.")
            } else {
                println("No instance of '$queryToRemove' found in the list.")
            }
        }
    }

    fun saveCertificationNumber(
        phoneNumber: String,
        certificationNumber: Int
    ) {
        val key = "$CERTIFICATION_PREFIX$phoneNumber"
        setValue(key, certificationNumber.toString(), 3, TimeUnit.MINUTES)
    }

    fun getCertificationNumber(phoneNumber: String): String {
        val key = "$CERTIFICATION_PREFIX$phoneNumber"
        return getValue(key) ?: throw ExpiredCertificationNumberException()
    }

    fun saveRefreshToken(
        userId: Long,
        refreshToken: String
    ) {
        val key = "$REFRESH_TOKEN_PREFIX$userId"
        setValue(key, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS)
    }

    fun getRefreshToken(userId: Long): String {
        val key = "$REFRESH_TOKEN_PREFIX$userId"
        return getValue(key) ?: throw InvalidTokenException()
    }

    @Transactional
    fun deleteRefreshToken(userId: Long) {
        val key = "$REFRESH_TOKEN_PREFIX$userId"
        redisTemplate.delete(key)
    }

    fun setValue(
        key: String,
        value: String,
        timeout: Long,
        unit: TimeUnit
    ) {
        val values = redisTemplate.opsForValue()
        values.set(key, value, timeout, unit)
    }

    fun getValue(key: String): String? {
        val values = redisTemplate.opsForValue()
        return values.get(key)
    }

    fun delValue(key: String) {
        redisTemplate.delete(key)
    }

    fun saveSocialKey(kakaoCode: String, kakaoKey: String) {
        val key = "$SOCIAL_USER_PREFIX$kakaoCode"
        setValue(key, kakaoKey, 1, TimeUnit.HOURS)
    }

    fun getSocialKey(socialCode: String): String {
        val key = "$SOCIAL_USER_PREFIX$socialCode"
        return getValue(key) ?: throw NotFoundSocialKeyException()
    }

    fun saveAuthenticationSchoolEmail(userId: Long, uuid: String) {
        val key = "$AUTHENTICATION_SCHOOL_EMAIL_PREFIX$userId"
        val existingValue = getValue(key)
        if (existingValue != null) {
            val parts = existingValue.split(":")
            val currentCount = parts.getOrNull(1)?.toIntOrNull() ?: 0
            if (currentCount >= 5) {
                throw SendEmailException()
            }
            val newCount = currentCount + 1
            val newValue = "$uuid:$newCount"
            setValue(key, newValue, 1, TimeUnit.HOURS)
        } else {
            val newValue = "$uuid:1"
            setValue(key, newValue, 1, TimeUnit.HOURS)
        }
    }

    fun authenticationSchoolEmail(userId: Long, authenticationSchoolEmailUuid: String) {
        val key = "$AUTHENTICATION_SCHOOL_EMAIL_PREFIX$userId"
        val value = getValue(key) ?: throw NotFoundAuthenticationSchoolEmailCodeException()
        val uuid = value.split(":").first()
        if (uuid != authenticationSchoolEmailUuid) {
            throw InvalidEmailCodeException()
        }
        delValue(key)
    }
}
