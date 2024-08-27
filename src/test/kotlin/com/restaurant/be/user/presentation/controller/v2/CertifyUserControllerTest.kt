package com.restaurant.be.user.presentation.controller.v2

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.domain.entity.Certification
import com.restaurant.be.user.presentation.dto.certification.CertifyUserResponse
import com.restaurant.be.user.repository.UserRepository
import com.restaurant.be.user.repository.v2.certification.CertifyUserRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset
import java.time.LocalDateTime

@IntegrationTest
@Transactional
class CertifyUserControllerTest(
    private val mockMvc: MockMvc,
    private val certifyUserRepository: CertifyUserRepository,
    private val userRepository: UserRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v2/users/certification"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(CertifyUserResponse::class.java))
        this.registerModule(module)
        this.registerModule(JavaTimeModule())
    }

    init {
        val phoneNumber = "01012345678"
        val registeredUUID = "123456"
        val wrongUUID = "654321"

        describe("#certify user when certification registered") {
            it("successfully certify user when certification number is valid") {
                // given
                setUpCertification(phoneNumber, registeredUUID)

                // when
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to phoneNumber,
                                    "certificationNumber" to registeredUUID
                                )
                            )
                        )
                        .contentType("application/json")
                ).also {
                    println(it.andReturn().response.contentAsString)
                }.andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(
                    Charset.forName(
                        "UTF-8"
                    )
                )
                val responseType =
                    object : TypeReference<CommonResponse<CertifyUserResponse>>() {}
                val actualResult: CommonResponse<CertifyUserResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.isAuthenticated shouldBe true
                actualResult.data!!.message shouldBe "인증이 완료되었습니다."

                val lastCertification = certifyUserRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber).get(0)
                lastCertification.verified shouldBe true

                userRepository.findByPhoneNumber(phoneNumber).shouldNotBeNull()
            }

            it("fail certify user when certification number is not valid") {
                // given
                setUpCertification(phoneNumber, registeredUUID)

                // when
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to phoneNumber,
                                    "certificationNumber" to wrongUUID
                                )
                            )
                        )
                        .contentType("application/json")
                ).also {
                    println(it.andReturn().response.contentAsString)
                }.andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(
                    Charset.forName(
                        "UTF-8"
                    )
                )
                val responseType =
                    object : TypeReference<CommonResponse<CertifyUserResponse>>() {}
                val actualResult: CommonResponse<CertifyUserResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.isAuthenticated shouldBe false
                actualResult.data!!.message shouldBe "인증번호가 일치하지 않습니다."

                val lastCertification = certifyUserRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber).get(0)
                lastCertification.verified shouldBe false
                userRepository.findByPhoneNumber(phoneNumber).shouldBeNull()
            }

            it("fail certify user when certification number expired") {
                // given
                val registeredAt = LocalDateTime.of(2022, 8, 1, 13, 0)
                val certificationRequest = Certification(
                    phoneNumber = phoneNumber,
                    certificationNumber = registeredUUID,
                    createdAt = registeredAt
                )
                certifyUserRepository.save(certificationRequest)

                // when
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf(
                                    "phoneNumber" to phoneNumber,
                                    "certificationNumber" to registeredUUID
                                )
                            )
                        )
                        .contentType("application/json")
                ).also {
                    println(it.andReturn().response.contentAsString)
                }.andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(
                    Charset.forName(
                        "UTF-8"
                    )
                )
                val responseType =
                    object : TypeReference<CommonResponse<CertifyUserResponse>>() {}
                val actualResult: CommonResponse<CertifyUserResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.isAuthenticated shouldBe false
                actualResult.data!!.message shouldBe "유효시간이 지난 인증번호입니다. 다시 발급 받아 주세요"

                val lastCertification = certifyUserRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber).get(0)
                lastCertification.verified shouldBe false
                userRepository.findByPhoneNumber(phoneNumber).shouldBeNull()
            }
        }
    }

    private fun setUpCertification(phoneNumber: String, registeredUUID: String) {
        val certificationRequest = Certification(
            phoneNumber = phoneNumber,
            certificationNumber = registeredUUID
        )
        certifyUserRepository.save(certificationRequest)
    }
}
