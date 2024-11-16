package com.restaurant.be.sms.presentation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class CertificationSmsControllerTest(
    private val mockMvc: MockMvc,
    private val redisRepository: RedisRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/sms"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
        this.registerModule(module)
    }

    init {
        beforeEach {
            redisRepository.saveCertificationNumber("01012341234", 1111)
        }

        describe("인증 번호 문자 전송 테스트") {
            it("when send certification sms should return success") {
                // given
                val phoneNumber = "01012341234"

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/send-certification-number")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to phoneNumber
                                )
                            )
                        )
                        .contentType("application/json")
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
        }

        describe("인증 번호 검증 테스트") {
            it("인증 번호 검증 성공 테스트") {
                // given

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/verify-certification-number")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to "01012341234",
                                    "certificationNumber" to "1111"
                                )
                            )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
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

            it("인증 번호 미일치 테스트") {
                // given

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/verify-certification-number")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to "01012341234",
                                    "certificationNumber" to "2222"
                                )
                            )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
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
                actualResult.message shouldBe "일치하지 않는 인증번호 입니다."
                actualResult.errorCode shouldBe "InvalidCertificationNumberException"
            }

            it("인증 번호 만료 테스트") {
                // given
                redisRepository.delValue("${RedisRepository.CERTIFICATION_PREFIX}01012341234")

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/verify-certification-number")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to "01012341234",
                                    "certificationNumber" to "1111"
                                )
                            )
                        )
                        .contentType(MediaType.APPLICATION_JSON)
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
                actualResult.message shouldBe "인증번호가 만료되었습니다."
                actualResult.errorCode shouldBe "ExpiredCertificationNumberException"
            }
        }
    }
}
