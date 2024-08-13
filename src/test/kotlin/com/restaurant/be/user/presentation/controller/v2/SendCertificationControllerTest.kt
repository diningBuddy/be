package com.restaurant.be.user.presentation.controller.v2

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ninjasquad.springmockk.MockkBean
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.user.presentation.dto.certification.SendCertificationResponse
import com.restaurant.be.user.presentation.dto.certification.SendMessageResponse
import com.restaurant.be.user.repository.v2.certification.CertifyUserRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.Charset

@IntegrationTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SendCertificationControllerTest(
    private val mockMvc: MockMvc,
    private val certifyUserRepository: CertifyUserRepository,
    @MockkBean private val webClient: WebClient
) : CustomDescribeSpec() {

    private val baseUrl = "/v2/users/certification"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(SendCertificationResponse::class.java))
        this.registerModule(module)
        this.registerModule(JavaTimeModule())
    }
    private val MAX_REQUESTS = CertifyUserRepository.MAX_MESSAGE_REQUESTS

    init {
        beforeEach {
            mockingSendMessage()
        }

        describe("#send Certification Number To User") {
            it("should successfully send certification number (within limit)") {
                // given
                val phoneNumber = "01012345678"
                val requestBody = mapOf("phoneNumber" to phoneNumber)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl/request")
                        .content(objectMapper.writeValueAsString(requestBody))
                        .contentType("application/json")
                ).also {
                    println(it.andReturn().response.contentAsString)
                }
                    // then
                    .andExpect(status().isOk)
                certifyUserRepository.findByPhoneNumberAndValid(phoneNumber, true) shouldHaveSize 1
            }

            it("should throw TooManyCertifyRequestException when exceeding limit") {
                // given
                val phoneNumber = "01012345678"
                sendUpToMaxRequests(phoneNumber)
                certifyUserRepository.countDayRequests(phoneNumber) shouldBe MAX_REQUESTS

                // when
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("$baseUrl/request")
                        .content(objectMapper.writeValueAsString(mapOf("phoneNumber" to phoneNumber)))
                        .contentType("application/json")
                ).andExpect(status().isBadRequest)
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<SendCertificationResponse>>() {}
                val actualResult: CommonResponse<SendCertificationResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )
                // then
                actualResult.message shouldBe "하루 인증번호 요청 개수를 초과하였습니다. 관리자에게 문의해주세요"
            }
        }
    }

    private fun mockingSendMessage() {
        val requestBodyUriSpec = mockk<WebClient.RequestBodyUriSpec>()
        val requestBodySpec = mockk<WebClient.RequestBodySpec>()
        val responseSpec = mockk<WebClient.ResponseSpec>()
        val response = SendMessageResponse(
            0,
            "Success"
        )
        val responseToString = objectMapper.writeValueAsString(response)

        every { webClient.post() } returns requestBodyUriSpec
        every { requestBodyUriSpec.body(any()) } returns requestBodySpec
        every { requestBodySpec.retrieve() } returns responseSpec
        every { responseSpec.bodyToMono(String::class.java) } returns Mono.just(
            responseToString
        )
    }

    private fun sendUpToMaxRequests(phoneNumber: String) {
        for (count in 1..MAX_REQUESTS) {
            mockMvc.perform(
                MockMvcRequestBuilders.post("$baseUrl/request").content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "phoneNumber" to phoneNumber
                        )
                    )
                ).contentType("application/json")
            )
        }
    }
}
