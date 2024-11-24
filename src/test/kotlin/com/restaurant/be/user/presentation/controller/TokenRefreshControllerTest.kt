package com.restaurant.be.user.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import org.hamcrest.Matchers
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class TokenRefreshControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val redisRepository: RedisRepository,
    private val tokenProvider: TokenProvider
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/users"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
        this.registerModule(module)
        this.registerModule(JavaTimeModule())
    }

    init {
        describe("엑세스 토큰 재발급 테스트") {
            it("엑세스 토큰 재발급 성공 테스트") {
                // given
                val testUser = setUpUser("01012341234", userRepository)
                val tokens = tokenProvider.createTokens(testUser.id.toString(), testUser.roles)
                redisRepository.saveRefreshToken(testUser.id!!, tokens.refreshToken)

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("RefreshToken", tokens.refreshToken)
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andExpect(header().exists("Authorization"))
                    .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<Unit>>() {}
                val actualResult: CommonResponse<Unit> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )
                val authorizationHeader = result.response.getHeader("Authorization")?.substring(7)

                // then
                actualResult.result shouldBe CommonResponse.Result.SUCCESS
                authorizationHeader shouldNotBe null
                authorizationHeader shouldMatch Regex("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")
            }

            it("엑세스 토큰 재발급 실패 테스트") {
                // given
                val testUser = setUpUser("01012341234", userRepository)
                val failTokens = tokenProvider.createTokens("1", testUser.roles)
                val tokens = tokenProvider.createTokens(testUser.id.toString(), testUser.roles)
                redisRepository.saveRefreshToken(testUser.id!!, tokens.refreshToken)

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("RefreshToken", failTokens.refreshToken)
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isUnauthorized)
                    .andExpect(jsonPath("$.result").value("FAIL"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<Unit>>() {}
                val actualResult: CommonResponse<Unit> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.result shouldBe CommonResponse.Result.FAIL
                actualResult.message shouldBe "유효하지 않은 토큰입니다."
                actualResult.errorCode shouldBe "InvalidTokenException"
            }
        }

        describe("리프레시 토큰 재발급 테스트") {
            it("리프레시 토큰 재발급 성공 테스트") {
                // given
                val testUser = setUpUser("01012341234", userRepository)
                val tokens = tokenProvider.createTokens(testUser.id.toString(), testUser.roles)
                redisRepository.saveRefreshToken(testUser.id!!, tokens.refreshToken)

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/refresh-token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("RefreshToken", tokens.refreshToken)
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andExpect(header().exists("Authorization"))
                    .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                    .andExpect(header().exists("RefreshToken"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<Unit>>() {}
                val actualResult: CommonResponse<Unit> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )
                val authorizationHeader = result.response.getHeader("Authorization")?.substring(7)
                val refreshTokenHeader = result.response.getHeader("RefreshToken")

                // then
                actualResult.result shouldBe CommonResponse.Result.SUCCESS
                authorizationHeader shouldNotBe null
                authorizationHeader shouldMatch Regex("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")
                refreshTokenHeader shouldNotBe null
                refreshTokenHeader shouldMatch Regex("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")
            }

            it("리프래시 토큰 재발급 실패 테스트") {
                // given
                val testUser = setUpUser("01012341234", userRepository)
                val failTokens = tokenProvider.createTokens("1", testUser.roles)
                val tokens = tokenProvider.createTokens(testUser.id.toString(), testUser.roles)
                redisRepository.saveRefreshToken(testUser.id!!, tokens.refreshToken)

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/refresh-token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("RefreshToken", failTokens.refreshToken)
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isUnauthorized)
                    .andExpect(jsonPath("$.result").value("FAIL"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<Unit>>() {}
                val actualResult: CommonResponse<Unit> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.result shouldBe CommonResponse.Result.FAIL
                actualResult.message shouldBe "유효하지 않은 토큰입니다."
                actualResult.errorCode shouldBe "InvalidTokenException"
            }
        }
    }
}
