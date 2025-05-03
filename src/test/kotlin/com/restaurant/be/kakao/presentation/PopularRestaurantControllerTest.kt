package com.restaurant.be.kakao.presentation

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
import com.restaurant.be.kakao.domain.entity.PopularRestaurant
import com.restaurant.be.kakao.domain.entity.ScrapCategory
import com.restaurant.be.kakao.repository.PopularRestaurantRepository
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
class PopularRestaurantControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val elasticsearchOperations: ElasticsearchOperations,
    private val popularRestaurantRepository: PopularRestaurantRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/kakao/popular-restaurants"
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

            val categories = listOf(
                ScrapCategory.ALL,
                ScrapCategory.KOREAN,
                ScrapCategory.WESTERN,
                ScrapCategory.JAPANESE,
                ScrapCategory.CHINESE,
                ScrapCategory.ASIAN,
                ScrapCategory.CAFE
            )

            categories.forEachIndexed { index, category ->
                popularRestaurantRepository.save(
                    PopularRestaurant(
                        id = (index + 1).toLong(),
                        restaurantId = 1L,
                        scrapCategory = category,
                        rankNumber = 1
                    )
                )
            }
        }

        afterEach {
            val indexOperations = elasticsearchOperations.indexOps(RestaurantDocument::class.java)
            if (indexOperations.exists()) {
                indexOperations.delete()
            }
        }

        describe("#getPopularRestaurants basic test") {
            it("should return popular restaurants for ALL category") {
                // when
                val result = mockMvc.perform(
                    get(baseUrl)
                        .param("categoryParam", "ALL")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
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

            it("should return popular restaurants for KOREAN category") {
                // when
                val result = mockMvc.perform(
                    get(baseUrl)
                        .param("categoryParam", "KOREAN")
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

            it("should support pagination for popular restaurants") {
                // when
                val result = mockMvc.perform(
                    get(baseUrl)
                        .param("categoryParam", "ALL")
                        .param("page", "0")
                        .param("size", "10")
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

            it("should support location-based sorting") {
                // when
                val result = mockMvc.perform(
                    get(baseUrl)
                        .param("categoryParam", "ALL")
                        .param("longitude", "126.970140339367")
                        .param("latitude", "37.2977142440725")
                        .param("customSort", "CLOSELY_DESC")
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
