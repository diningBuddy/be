package com.restaurant.be.home.presentation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.RestaurantDocument
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.home.presentation.dto.HomeResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class HomeControllerTest(
    private val mockMvc: MockMvc,
    private val elasticsearchOperations: ElasticsearchOperations,
    private val userRepository: UserRepository
) : CustomDescribeSpec() {

    private val baseUrl = "/v1/home"
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply {
        val module = SimpleModule()
        module.addDeserializer(Page::class.java, PageDeserializer(RestaurantDto::class.java))
        this.registerModule(module)
    }

    init {
        beforeEach {
            setUpUser("01012345678", userRepository)
            val indexOperations = elasticsearchOperations.indexOps(RestaurantDocument::class.java)
            if (indexOperations.exists()) {
                indexOperations.delete()
            }
            indexOperations.create()
            indexOperations.putMapping(indexOperations.createMapping())
        }

        describe("#getRecommendations basic test") {
            it("should return home data") {
                // when
                val result = mockMvc.perform(
                    get(baseUrl)
                        .param("userLatitude", "37.2977142440725")
                        .param("userLongitude", "126.970140339367")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType = object : TypeReference<CommonResponse<HomeResponse>>() {}
                val actualResult: CommonResponse<HomeResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.result shouldBe CommonResponse.Result.SUCCESS
            }
        }

        describe("#getSectionDetails basic test") {
            it("should return section details") {
                // when
                val result = mockMvc.perform(
                    get("$baseUrl/section")
                        .param("type", "LUNCH")
                        .param("userLatitude", "37.2977142440725")
                        .param("userLongitude", "126.970140339367")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType = object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.result shouldBe CommonResponse.Result.SUCCESS
            }
        }
    }
}
