package com.restaurant.be.restaurant.presentation.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.be.common.CustomDescribeSpec
import com.restaurant.be.common.IntegrationTest
import com.restaurant.be.common.PageDeserializer
import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.common.util.RestaurantUtil
import com.restaurant.be.common.util.setUpUser
import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import com.restaurant.be.restaurant.presentation.controller.dto.BookmarkRestaurantResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import com.restaurant.be.restaurant.repository.RestaurantBookmarkRepository
import com.restaurant.be.restaurant.repository.RestaurantRepository
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.domain.entity.User
import com.restaurant.be.user.repository.UserRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.Charset
import java.time.LocalDate

@IntegrationTest
@Transactional
class BookmarkRestaurantControllerTest(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val restaurantLikeRepository: RestaurantBookmarkRepository,
    private val restaurantRepository: RestaurantRepository
) : CustomDescribeSpec() {
    private val baseUrl = "/v1/restaurants"
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

        describe("#getMyLikeRestaurants basic test") {
            it("when no saved should return empty") {
                // given
                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 0
            }

            it("when user's like saved should return saved like") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
                actualResult.data!!
                    .restaurants.content[0]
                    .name shouldBe "목구멍 율전점"
                actualResult.data!!
                    .restaurants.content[0]
                    .isBookmarked shouldBe true
            }

            it("when another user's like saved should return empty") {
                // given
                val anotherUser =
                    userRepository.save(
                        User(
                            phoneNumber = "01099999999",
                            nickname = "test_like_nickname",
                            name = "test_name",
                            gender = Gender.MAN,
                            birthday = LocalDate.now(),
                            isTermsAgreed = true
                        )
                    )
                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = anotherUser.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 0
            }
        }

        describe("#getMyLikeRestaurants pagination test") {
            it("when 1 data and set size 1 should return 1 data") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks?page=0&size=1")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
                actualResult.data!!
                    .restaurants.content[0]
                    .name shouldBe "목구멍 율전점"
                actualResult.data!!
                    .restaurants.content[0]
                    .isBookmarked shouldBe true
            }

            it("when 2 data and set size 1 should return 1 data") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity1 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                val restaurantEntity2 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점2"
                    )
                restaurantRepository.save(restaurantEntity1)
                restaurantRepository.save(restaurantEntity2)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity1.id
                    )
                )
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity2.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                                .param("page", "0")
                                .param("size", "1")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
                actualResult.data!!
                    .restaurants.content[0]
                    .name shouldBe "목구멍 율전점2"
                actualResult.data!!
                    .restaurants.content[0]
                    .isBookmarked shouldBe true
            }

            it("when 2 data and set size 1 page 0 should return 1's restaurant") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity1 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                val restaurantEntity2 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점2"
                    )
                restaurantRepository.save(restaurantEntity1)
                restaurantRepository.save(restaurantEntity2)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity1.id
                    )
                )
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity2.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                                .param("page", "0")
                                .param("size", "1")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
                actualResult.data!!
                    .restaurants.content[0]
                    .name shouldBe "목구멍 율전점2"
                actualResult.data!!
                    .restaurants.content[0]
                    .isBookmarked shouldBe true
            }

            it("when 2 data and set size 1 page 1 should return 2's restaurant") {
                // given
                val newUser = userRepository.findByPhoneNumber("01012345678")
                val restaurantEntity1 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                val restaurantEntity2 =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점2"
                    )
                restaurantRepository.save(restaurantEntity1)
                restaurantRepository.save(restaurantEntity2)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity1.id
                    )
                )
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity2.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                                .param("page", "1")
                                .param("size", "1")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
                actualResult.data!!
                    .restaurants.content[0]
                    .name shouldBe "목구멍 율전점"
                actualResult.data!!
                    .restaurants.content[0]
                    .isBookmarked shouldBe true
            }
        }

        describe("#getMyLikeRestaurants cartesian product bug test") {
            it("when two user like same restaurant should return only one") {
                // given
                val newUser =
                    userRepository.save(
                        User(
                            phoneNumber = "01099999999",
                            nickname = "test_like_nickname",
                            name = "test_name",
                            gender = Gender.MAN,
                            birthday = LocalDate.now(),
                            isTermsAgreed = true
                        )
                    )
                val originalUser = userRepository.findByPhoneNumber("01012345678")!!

                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = originalUser.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            get("$baseUrl/bookmarks")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<GetRestaurantsResponse>>() {}
                val actualResult: CommonResponse<GetRestaurantsResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!
                    .restaurants.content.size shouldBe 1
            }
        }

        describe("#likeRestaurant basic test") {
            it("when like restaurant should success like") {
                // given
                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)

                // when
                val result = mockMvc.perform(
                    post("$baseUrl/${restaurantEntity.id}/bookmark")
                        .content(
                            objectMapper.writeValueAsString(
                                mapOf("isLike" to true)
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
                    object : TypeReference<CommonResponse<BookmarkRestaurantResponse>>() {}
                val actualResult: CommonResponse<BookmarkRestaurantResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.restaurant.shouldNotBeNull()
                actualResult.data!!.restaurant.name shouldBe "목구멍 율전점"
                actualResult.data!!.restaurant.isBookmarked shouldBe true
                actualResult.data!!.restaurant.bookmarkCount shouldBe 1
            }

            it("when unlike restaurant should success unlike") {
                // given
                val restaurantEntity =
                    RestaurantUtil.generateRestaurantEntity(
                        name = "목구멍 율전점"
                    )
                restaurantRepository.save(restaurantEntity)
                val newUser = userRepository.findByPhoneNumber("01012345678")
                restaurantLikeRepository.save(
                    RestaurantBookmark(
                        userId = newUser?.id ?: 0,
                        restaurantId = restaurantEntity.id
                    )
                )

                // when
                val result =
                    mockMvc
                        .perform(
                            post("$baseUrl/${restaurantEntity.id}/bookmark")
                                .content(
                                    objectMapper.writeValueAsString(
                                        mapOf(
                                            "isLike" to false
                                        )
                                    )
                                ).contentType("application/json")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isOk)
                        .andExpect(jsonPath("$.result").value("SUCCESS"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<BookmarkRestaurantResponse>>() {}
                val actualResult: CommonResponse<BookmarkRestaurantResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.data!!.restaurant.shouldNotBeNull()
                actualResult.data!!.restaurant.name shouldBe "목구멍 율전점"
                actualResult.data!!.restaurant.isBookmarked shouldBe false
                actualResult.data!!.restaurant.bookmarkCount shouldBe -1
            }

            it("when not exist restaurant should return not found") {
                // given
                // when
                val result =
                    mockMvc
                        .perform(
                            post("$baseUrl/1/bookmark")
                                .content(
                                    objectMapper.writeValueAsString(
                                        mapOf(
                                            "isLike" to true
                                        )
                                    )
                                ).contentType("application/json")
                        ).also {
                            println(it.andReturn().response.contentAsString)
                        }.andExpect(status().isNotFound)
                        .andExpect(jsonPath("$.result").value("FAIL"))
                        .andReturn()

                val responseContent = result.response.getContentAsString(Charset.forName("UTF-8"))
                val responseType =
                    object : TypeReference<CommonResponse<BookmarkRestaurantResponse>>() {}
                val actualResult: CommonResponse<BookmarkRestaurantResponse> =
                    objectMapper.readValue(
                        responseContent,
                        responseType
                    )

                // then
                actualResult.message shouldBe "해당 식당 정보가 존재하지 않습니다."
            }
        }
    }
}
