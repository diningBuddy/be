package com.restaurant.be.user.repository

import com.restaurant.be.common.exception.KaKaoGetTokenException
import com.restaurant.be.user.repository.dto.KakaoTokenResponse
import com.restaurant.be.user.repository.dto.KakaoUserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
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

    private val restClient: RestClient,
) {

    fun getKakaoKey(kakaoCode: String): String {
        val kakaoToken = getkakaoToken(kakaoCode)
        return restClient
            .get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer ${kakaoToken.access_token}")
            .retrieve()
            .body(KakaoUserInfo::class.java)?.id ?: throw KaKaoGetTokenException()
    }

    private fun getkakaoToken(kakaoCode: String): KakaoTokenResponse {
        return restClient
            .post()
            .uri("https://kauth.kakao.com/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("client_id", kakaoApiKey)
                add("redirect_uri", kakaoRedirectUrl)
                add("code", kakaoCode)
            })
            .retrieve()
            .body(KakaoTokenResponse::class.java) ?: throw KaKaoGetTokenException()
    }
}
