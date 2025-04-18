package com.restaurant.be.restaurant.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.category.domain.entity.Category
import com.restaurant.be.category.repository.CategoryRepository
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.RestaurantDocument
import com.restaurant.be.common.util.RestaurantUtil
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import com.restaurant.be.restaurant.domain.entity.RestaurantCategory
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.restaurant.repository.RestaurantBookmarkRepository
import com.restaurant.be.restaurant.repository.RestaurantCategoryRepository
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.shouldBe
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.Charset

@IntegrationTest
@Transactional
class GetRestaurantControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val elasticsearchOperations: ElasticsearchOperations,
    private val restaurantRepository: RestaurantRepository,
    private val categoryRepository: CategoryRepository,
    private val restaurantCategoryRepository: RestaurantCategoryRepository,
    private val restaurantBookmarkRepository: RestaurantBookmarkRepository
) : CustomDescribeSpec() {
    private val restaurantUrl = "/v1/restaurants"
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

        afterEach {
            val indexOperations = elasticsearchOperations.indexOps(RestaurantDocument::class.java)
            if (indexOperations.exists()) {
                indexOperations.delete()
            }
        }

        describe("#get restaurants basic test") {
            it("when no saved should return empty") {
                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when saved should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(name = "목구멍 율전점")
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }
        }

        describe("#get restaurants simple filter test") {
            it("when category filter should return restaurant") {
                // given
                val category = categoryRepository.save(Category(name = "한식"))
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                restaurantCategoryRepository.save(
                    RestaurantCategory(
                        restaurant = restaurantEntity,
                        category = category
                    )
                )
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    categories = listOf("한식")
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("categories", "한식")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when category filter should return empty") {
                // given
                val category = categoryRepository.save(Category(name = "한식"))
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                restaurantCategoryRepository.save(
                    RestaurantCategory(
                        restaurant = restaurantEntity,
                        category = category
                    )
                )
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    categories = listOf("한식")
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("categories", "양식")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when category filter in default setting should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("categories", "한식")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when discount filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    discountContent = "성대생 할인 10%"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    discountContent = "성대생 할인 10%"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("discountForSkku", "true")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when discount filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    discountContent = "성대생 할인 10%"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    discountContent = "성대생 할인 10%"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("discountForSkku", "false")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when discount filter in default setting should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    discountContent = null
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("discountForSkku", "true")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when discount filter in default setting should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    discountContent = null
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("discountForSkku", "false")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when like filter should return restaurant") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")

                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("like", "true")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when like filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("like", "false")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when like filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("bookmark", "true")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when like filter should return empty") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("bookmark", "false")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when no like filter should return restaurant") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMax filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMax", "10000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMax filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMax", "20000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMax filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMax", "5000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when priceMin filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMin", "10000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMin filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMin", "20000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMin filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMin", "30000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when priceMin and priceMax filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMin", "10000")
                        .param("priceMax", "20000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when priceMin and priceMax filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("priceMin", "20001")
                        .param("priceMax", "30000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when query filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when query filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "피자")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when ratingAvg filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    ratingAvg = 4.5
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    ratingAvg = 4.5
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("ratingAvg", "4.5")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when ratingAvg filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    ratingAvg = 4.5
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    ratingAvg = 4.5
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("ratingAvg", "5.0")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when reviewCount filter should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    reviewCount = 100
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    reviewCount = 100
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("reviewCount", "100")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when reviewCount filter should return empty") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    reviewCount = 100
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    reviewCount = 100
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("reviewCount", "200")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }
        }

        describe("#get restaurants composite filter test") {
            it("when all filter should return restaurant") {
                // given
                val user = userRepository.findByPhoneNumber("01012345678")
                val category = categoryRepository.save(Category(name = "한식"))
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    ratingAvg = 4.5,
                    reviewCount = 100,
                    discountContent = "성대생 할인 10%",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                restaurantCategoryRepository.save(
                    RestaurantCategory(
                        restaurant = restaurantEntity,
                        category = category
                    )
                )
                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = user?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    categories = listOf("한식"),
                    ratingAvg = 4.5,
                    reviewCount = 100,
                    discountContent = "성대생 할인 10%",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("categories", "한식")
                        .param("discountContent", "true")
                        .param("like", "true")
                        .param("ratingAvg", "4.5")
                        .param("reviewCount", "100")
                        .param("priceMin", "10000")
                        .param("priceMax", "20000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when all filter should return empty") {
                // given
                val user = userRepository.findByPhoneNumber("01012345678")
                val category = categoryRepository.save(Category(name = "한식"))
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점",
                    ratingAvg = 4.5,
                    reviewCount = 100,
                    discountContent = "성대생 할인 10%",
                    menus = mutableListOf(
                        RestaurantUtil.generateMenuJsonEntity(price = 10000),
                        RestaurantUtil.generateMenuJsonEntity(price = 20000)
                    )
                )
                restaurantRepository.save(restaurantEntity)
                restaurantCategoryRepository.save(
                    RestaurantCategory(
                        restaurant = restaurantEntity,
                        category = category
                    )
                )
                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = user?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점",
                    categories = listOf("한식"),
                    ratingAvg = 4.4,
                    reviewCount = 100,
                    discountContent = "성대생 할인 10%",
                    menus = listOf(
                        RestaurantUtil.generateMenuDocument(
                            price = 10000
                        ),
                        RestaurantUtil.generateMenuDocument(
                            price = 20000
                        )
                    )
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("query", "목구멍 율전점")
                        .param("categories", "한식")
                        .param("discountContent", "true")
                        .param("like", "true")
                        .param("ratingAvg", "4.5")
                        .param("reviewCount", "100")
                        .param("priceMin", "10000")
                        .param("priceMax", "20000")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }
        }

        describe("#get restaurants pagination test") {
            it("when no data and set size 1 should return empty") {
                // given
                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("size", "1")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 0
            }

            it("when 1 data and set size 1 should return 1") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("size", "1")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점"
            }

            it("when 2 data and set size 1 page 0 should return 1's restaurant") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1"
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1"
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2"
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2"
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("size", "1")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점1"
            }

            it("when 2 data and set size 1 page 1 should return 2's restaurant") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1"
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1"
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2"
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2"
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("size", "1")
                        .param("page", "1")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content.size shouldBe 1
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점2"
            }
        }

        describe("#get restaurants sort test") {
            it("when basic sort should return sorted restaurants by _score") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1"
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1"
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2"
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2"
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("customSort", "BASIC")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점1"
                actualResult.data!!.restaurants.content[1].name shouldBe "목구멍 율전점2"
            }

            it("when closely_desc sort should return sorted restaurants by closely_desc") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1",
                    longitude = 127.123457,
                    latitude = 37.123457
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1",
                    longitude = 127.123457,
                    latitude = 37.123457
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2",
                    ratingAvg = 4.0,
                    longitude = 127.123456,
                    latitude = 37.123456
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2",
                    longitude = 127.123456,
                    latitude = 37.123456
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("customSort", "CLOSELY_DESC")
                        .param("longitude", "127.123456")
                        .param("latitude", "37.123456")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점2"
                actualResult.data!!.restaurants.content[1].name shouldBe "목구멍 율전점1"
            }

            it("when rating_desc sort should return sorted restaurants by rating_desc") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1",
                    ratingAvg = 4.0
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1",
                    ratingAvg = 4.0
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2",
                    ratingAvg = 4.5
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2",
                    ratingAvg = 4.5
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("customSort", "RATING_DESC")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점2"
                actualResult.data!!.restaurants.content[1].name shouldBe "목구멍 율전점1"
            }

            it("when review_desc sort should return sorted restaurants by review_desc") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1",
                    reviewCount = 100
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1",
                    reviewCount = 100
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2",
                    reviewCount = 200
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2",
                    reviewCount = 200
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("customSort", "REVIEW_COUNT_DESC")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점2"
                actualResult.data!!.restaurants.content[1].name shouldBe "목구멍 율전점1"
            }

            it("when bookmark_count_desc sort should return sorted restaurants by bookmark_count_desc") {
                // given
                val restaurantEntity1 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점1"
                )
                restaurantRepository.save(restaurantEntity1)
                val restaurantDocument1 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity1.id,
                    name = "목구멍 율전점1"
                )
                elasticsearchOperations.save(restaurantDocument1)

                val restaurantEntity2 = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점2",
                    bookmarkCount = 1
                )
                restaurantRepository.save(restaurantEntity2)
                val restaurantDocument2 = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity2.id,
                    name = "목구멍 율전점2",
                    bookmarkCount = 1
                )
                elasticsearchOperations.save(restaurantDocument2)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                val user = userRepository.findByPhoneNumber("01012345678")
                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = user?.id ?: 0,
                        restaurantId = restaurantEntity2.id
                    )
                )

                // when
                val result = mockMvc.perform(
                    get(restaurantUrl)
                        .param("customSort", "BOOKMARK_COUNT_DESC")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurants.content[0].name shouldBe "목구멍 율전점2"
                actualResult.data!!.restaurants.content[1].name shouldBe "목구멍 율전점1"
            }
        }

        describe("#get restaurant test") {
            it("when restaurant exist should return restaurant") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                val restaurantDocument = RestaurantUtil.generateRestaurantDocument(
                    id = restaurantEntity.id,
                    name = "목구멍 율전점"
                )
                elasticsearchOperations.save(restaurantDocument)
                elasticsearchOperations.indexOps(RestaurantDocument::class.java).refresh()

                // when
                val result = mockMvc.perform(
                    get("$restaurantUrl/${restaurantEntity.id}")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurant.name shouldBe "목구멍 율전점"
            }

            it("when restaurant not exist should return empty") {
                // given
                // when
                val result = mockMvc.perform(
                    get("$restaurantUrl/1")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.result").value("FAIL"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data shouldBe null
                actualResult.message shouldBe "해당 식당 정보가 존재하지 않습니다."
            }

            it("when liked restaurant should return liked true") {
                // given
                val user = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)
                restaurantBookmarkRepository.save(
                    RestaurantBookmark(
                        userId = user?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                val result = mockMvc.perform(
                    get("$restaurantUrl/${restaurantEntity.id}")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurant.isBookmarked shouldBe true
            }

            it("when not liked restaurant should return liked false") {
                // given
                val restaurantEntity = RestaurantUtil.generateRestaurantEntity(
                    name = "목구멍 율전점"
                )
                restaurantRepository.save(restaurantEntity)

                val result = mockMvc.perform(
                    get("$restaurantUrl/${restaurantEntity.id}")
                )
                    .also {
                        println(it.andReturn().response.contentAsString)
                    }
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.result").value("SUCCESS"))
                    .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantResponse> = objectMapper.readValue(
                    responseContent,
                    responseType
                )

                // then
                actualResult.data!!.restaurant.isBookmarked shouldBe false
            }
        }
    }
}
