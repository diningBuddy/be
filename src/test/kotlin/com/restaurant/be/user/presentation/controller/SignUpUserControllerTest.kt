package com.restaurant.be.user.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import com.restaurant.be.user.repository.UserRepository
import com.restaurant.be.user.util.NickNameGenerateUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
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
import java.time.LocalDate

@IntegrationTest
@Transactional
class SignUpUserControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository
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
            setUpUser("01012341234", userRepository)
        }

        describe("회원가입 테스트") {
            it("회원가입 성공 테스트") {
                // given
                val request = SignUpUserRequest(
                    phoneNumber = "01011111111",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up")
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

            it("1개의 중복 닉네임 테스트") {
                // given
                mockkObject(NickNameGenerateUtil)

                val request = SignUpUserRequest(
                    phoneNumber = "01022222222",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                every { NickNameGenerateUtil.generateNickname() } returns "test_nickname"

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up")
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
                unmockkAll()
            }

            it("2개 이상의 중복 닉네임 테스트") {
                // given
                mockkObject(NickNameGenerateUtil)
                setUpUser("01056785678", "test_nickname1", userRepository)

                val request = SignUpUserRequest(
                    phoneNumber = "01022222222",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                every { NickNameGenerateUtil.generateNickname() } returns "test_nickname"

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up")
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
                userRepository.findByPhoneNumber("01022222222")?.nickname shouldBe "test_nickname2"
            }

            it("회원가입 중복 핸드폰 번호 테스트") {
                // given
                val request = SignUpUserRequest(
                    phoneNumber = "01012341234",
                    name = "test_name",
                    birthday = LocalDate.now(),
                    gender = Gender.MAN
                )

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/sign-up")
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
                actualResult.message shouldBe "이미 가입된 핸드폰번호 입니다."
                actualResult.errorCode shouldBe "DuplicateUserPhoneNumberException"
            }
        }
    }
}
