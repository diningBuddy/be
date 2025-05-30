package com.restaurant.be.user.repository

import com.restaurant.be.common.exception.KaKaoGetTokenException
import com.restaurant.be.user.repository.dto.KakaoTokenResponse
import com.restaurant.be.user.repository.dto.KakaoUserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Repository
@Transactional(readOnly = true)
class KakaoRepository(
    @Value("\${kakao.key}")
    private val kakaoApiKey: String,

    @Value("\${kakao.redirect-uri}")
    private val kakaoRedirectUrl: String,

    private val restClient: RestClient
) {

    fun getKakaoKey(kakaoToken: String): String {
        return restClient
            .get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer $kakaoToken")
            .retrieve()
            .body(KakaoUserInfo::class.java)?.id ?: throw KaKaoGetTokenException()
    }

    private fun getkakaoToken(kakaoCode: String): KakaoTokenResponse {
        return restClient
            .post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
            .body(
                LinkedMultiValueMap<String, String>().apply {
                    add("grant_type", "authorization_code")
                    add("client_id", kakaoApiKey)
                    add("redirect_uri", kakaoRedirectUrl)
                    add("code", kakaoCode)
                }
            )
            .retrieve()
            .body(KakaoTokenResponse::class.java) ?: throw KaKaoGetTokenException()
    }
}
