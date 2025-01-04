package com.restaurant.be.user.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.presentation.dto.SignInUserRequest
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import org.hamcrest.Matchers
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class SignInUserControllerTest(
    private val mockMvc: MockMvc,
    private val redisRepository: RedisRepository,
    private val userRepository: UserRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/users"
    private val objectMapper: ObjectMapper =
        ObjectMapper().registerModule(KotlinModule()).apply {
            val module = SimpleModule()
            module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
            this.registerModule(module)
        }

    init {
        beforeEach {
            setUpUser("01012341234", userRepository)
            redisRepository.saveCertificationNumber("01012341234", 1111)
        }

        describe("로그인 테스트") {
            it("로그인 성공 테스트") {
                // given
                val request =
                    SignInUserRequest(
                        phoneNumber = "01012341234",
                        certificationNumber = "1111"
                    )

                // when
                val result =
                    mockMvc.perform(
                        post("$baseUrl/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
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

            it("로그인 실패 테스트") {
                // given
                val request =
                    SignInUserRequest(
                        phoneNumber = "01012345678",
                        certificationNumber = "1111"
                    )

                // when
                val result =
                    mockMvc.perform(
                        post("$baseUrl/sign-in")
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
                actualResult.message shouldBe "존재 하지 않는 유저 입니다."
                actualResult.errorCode shouldBe "NotFoundUserException"
            }
        }
    }
}
