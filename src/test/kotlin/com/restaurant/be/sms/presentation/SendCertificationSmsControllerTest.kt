package com.restaurant.be.sms.presentation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class SendCertificationSmsControllerTest(
    private val mockMvc: MockMvc
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/sms"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
        this.registerModule(module)
    }

    init {
        describe("#sendCertificationSms basic test") {
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
    }
}
