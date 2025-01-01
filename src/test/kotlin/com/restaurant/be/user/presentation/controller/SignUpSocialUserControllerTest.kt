package com.restaurant.be.user.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.setUpSocialUser
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.presentation.dto.SignUpSocialUserRequest
import com.restaurant.be.user.repository.SocialUserRepository
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset
import java.time.LocalDate

@IntegrationTest
@Transactional
class SignUpSocialUserControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val socialUserRepository: SocialUserRepository,
    private var redisRepository: RedisRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/users"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
        this.registerModule(module)
        this.registerModule(JavaTimeModule())
    }

    init {
        beforeEach {
            setUpSocialUser("01012341234", "kakao_key1", userRepository, socialUserRepository)
            redisRepository.saveSocialKey("code1", "kakao_key1")
            redisRepository.saveSocialKey("code2", "kakao_key2")
        }

        describe("카카오 회원가입 테스트") {
            it("카카오 회원가입 성공 테스트") {
                // given
                val request = SignUpSocialUserRequest(
                    socialCode = "code2",
                    phoneNumber = "01011111111",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up/social/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
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
                actualResult.result shouldBe CommonResponse.Result.SUCCESS
            }

            it("카카오 회원가입 실패 테스트") {
                // given
                val request = SignUpSocialUserRequest(
                    socialCode = "code1",
                    phoneNumber = "01011111111",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up/social/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isBadRequest)
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
                actualResult.message shouldBe "이미 존재 하는 소셜 회원 입니다."
                actualResult.errorCode shouldBe "DuplicateSocialUserException"
            }

            it("Redis 에 소셜 키가 존재하지 않는 경우 테스트") {
                // given
                val request = SignUpSocialUserRequest(
                    socialCode = "code3",
                    phoneNumber = "01011111111",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up/social/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    .andExpect(status().isNotFound)
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
                actualResult.message shouldBe "소셜 로그인 키가 존재하지 않습니다."
                actualResult.errorCode shouldBe "NotFoundSocialKeyException"
            }
        }
    }
}
