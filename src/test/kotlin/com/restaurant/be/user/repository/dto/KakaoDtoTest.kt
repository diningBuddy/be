package com.restaurant.be.user.repository.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class KakaoDtoTest : DescribeSpec({
    describe("카카오 Dto 테스트") {
        it("KakaoTokenResponse 생성 테스트") {
            // Given
            val response = KakaoTokenResponse(
                token_type = "token_type",
                access_token = "access_token",
                expires_in = 0,
                refresh_token = "refresh_token",
                refresh_token_expires_in = 0,
                scope = "scope"
            )

            // When

            // Then
            response.token_type shouldBe "token_type"
            response.access_token shouldBe "access_token"
            response.expires_in shouldBe 0
            response.refresh_token shouldBe "refresh_token"
            response.refresh_token_expires_in shouldBe 0
            response.scope shouldBe "scope"
        }

        it("KakaoUserInfo 생성 테스트") {
            // Given
            val response = KakaoUserInfo(
                id = "id"
            )

            // When

            // Then
            response.id shouldBe "id"
        }
    }
})
