package com.restaurant.be.user.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.presentation.dto.CheckNicknameResponse
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class UpdateUserControllerTest(
    private val mockMvc: MockMvc,
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
            setUpUser("01012345678", userRepository)
        }

        describe("update user basic test") {
            it("success update") {
                // test data
                var test_data = UpdateUserRequest(id = 1, profileImageUrl = "example.url", nickname = "tuji", name = "inseok", gender = Gender.MAN)
                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/check-nickname")
                                .param("nickname", "test_data")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<CheckNicknameResponse>>() {}
                val actualResult: CommonResponse<CheckNicknameResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.isDuplicate shouldBe false
            }

            it("failed reason 1: update data fault") {
                // given
                val existedUserNickname = userRepository.findByPhoneNumber("01012345678")?.nickname ?: ""

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/check-nickname")
                                .param("nickname", existedUserNickname)
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.result").value("FAIL"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<CheckNicknameResponse>>() {}
                val actualResult: CommonResponse<CheckNicknameResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.message shouldBe "이미 존재 하는 닉네임 입니다."
            }
        }
    }
}
