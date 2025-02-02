package com.restaurant.be.home.domain.service

import com.restaurant.be.home.presentation.dto.*
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.FacilityInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfosJsonEntity
import com.restaurant.be.restaurant.domain.service.GetRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.common.MenuDto
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDetailDto
import com.restaurant.be.restaurant.presentation.controller.dto.common.RestaurantDto
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val restaurantService: GetRestaurantService
) {

    fun getHomePage(
        request: HomeRequest,
        userId: Long
    ): HomeResponse {
//        val lunchRequest: GetRestaurantsRequest = GetRestaurantsRequest(
//            //필요한 필터.
//            ratingAvg = 4.0,
//            categories = //포하만 가능
//        )
//        val midNightRequest: GetRestaurantsRequest = GetRestaurantsRequest(
//            //필요한 필터.
//            ratingAvg = 4.0,
//            categories =
//        )
        //paging propose:5 pagnation객체

//        val lunchResponse = restaurantService.getRestaurants(lunchRequest,5,userId)
//        val yasikResponse = restaurantService.getRestaurants(midNightRequest,5,userId)
        //배너관련...별도서비스?배너서비스..!
        //TODO: 가짜객체는 반환되게-> 식당을 먼저 이후 여기

        val testHomeResponse = HomeResponse(
            restaurantBanner = listOf(
                GetBannerResponse(
                    imageUrl = "https://example.com/image1.jpg",
                    title = "율전점 고깃집",
                    subtitle = "맛있는 고기를 파는 율전점의 고깃집입니다."
                )
            ),
            //null이면 충분하다.
            restaurantRecommendations = listOf(
                GetRecommendationRestaurantsResponse(
                    recommendationType = RecommendationType.LAUNCH,
                    restaurants = listOf(
                        RestaurantDto(
                            id = 1L,
                            representativeImageUrl = "https://example.com/image1.jpg",
                            name = "맛있는 식당",
                            ratingAvg = 4.5,
                            ratingCount = 100L,
                            facilityInfos = FacilityInfoJsonEntity(
                                "Y",
                                "Y",
                                "Y",
                                "Y",
                                "Y",
                                "Y"
                            ),
                            operationInfos = OperationInfoJsonEntity(
                                "Y",
                                "Y",
                                "Y"
                            ),
                            operationTimes = listOf(
                                OperationTimeInfosJsonEntity(
                                    dayOfTheWeek = "월요일",
                                    operationTimeInfo = OperationTimeInfoJsonEntity(
                                        startTime = "11:00",
                                        endTime = "12:30",
                                        breakStartTime = null,
                                        breakEndTime = null,
                                        lastOrder = null
                                    )
                                )
                            ),
                            reviewCount = 50L,
                            bookmarkCount = 30L,
                            categories = listOf("한식", "분식"),
                            representativeMenu = MenuDto(
                                name = "하이볼",
                                price = 8000,
                                description = "맛있는 하이볼",
                                isRepresentative = false,
                                imageUrl = "https://example.com/image1.jpg",
                            ),
                            representativeReviewContent = "정말 맛있어요! 또 방문하고 싶은 맛집입니다.",
                            isBookmarked = false,
                            discountContent = "런치 메뉴 10% 할인",
                            longitude = 127.1086228,
                            latitude = 37.4012191,
                            kakaoRatingAvg = 4.3,
                            kakaoRatingCount = 200L,
                            detailInfo = RestaurantDetailDto(
                                contactNumber = "0507-1464-9295",
                                address = "경기 수원시 장안구 화산로233번길 46 1층",
                                menus = listOf(
                                    MenuDto(
                                        name = "하이볼",
                                        price = 8000,
                                        description = "맛있는 하이볼",
                                        isRepresentative = false,
                                        imageUrl = "https://example.com/image1.jpg",
                                    )
                                )
                            )
                        )
                    )
                ),
                GetRecommendationRestaurantsResponse(
                    recommendationType = RecommendationType.LATE_NIGHT,
                    restaurants = listOf(
                        RestaurantDto(
                            id = 2L,
                            representativeImageUrl = "https://example.com/image2.jpg",
                            name = "야식의 신",
                            ratingAvg = 4.7,
                            ratingCount = 150L,
                            facilityInfos = FacilityInfoJsonEntity(
                                "Y",
                                "Y",
                                "Y",
                                "Y",
                                "Y",
                                "Y"
                            ),
                            operationInfos = OperationInfoJsonEntity(
                                "Y",
                                "Y",
                                "Y"
                            ),
                            operationTimes = listOf(
                                OperationTimeInfosJsonEntity(
                                    dayOfTheWeek = "월요일",
                                    operationTimeInfo = OperationTimeInfoJsonEntity(
                                        startTime = "11:00",
                                        endTime = "12:30",
                                        breakStartTime = null,
                                        breakEndTime = null,
                                        lastOrder = null
                                    )
                                )
                            ),
                            reviewCount = 75L,
                            bookmarkCount = 45L,
                            categories = listOf("치킨", "야식"),
                            representativeMenu = MenuDto(
                                name = "하이볼",
                                price = 8000,
                                description = "맛있는 하이볼",
                                isRepresentative = false,
                                imageUrl = "https://example.com/image1.jpg",
                            ),
                            representativeReviewContent = "새벽에도 맛있는 음식을 먹을 수 있어요!",
                            isBookmarked = true,
                            discountContent = "배달비 무료",
                            longitude = 127.1087228,
                            latitude = 37.4013191,
                            kakaoRatingAvg = 4.5,
                            kakaoRatingCount = 300L,
                            detailInfo = RestaurantDetailDto(
                                contactNumber = "0507-1464-9295",
                                address = "경기 수원시 장안구 화산로233번길 46 1층",
                                menus = listOf(
                                    MenuDto(
                                        name = "하이볼",
                                        price = 8000,
                                        description = "맛있는 하이볼",
                                        isRepresentative = false,
                                        imageUrl = "https://example.com/image1.jpg",
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        return testHomeResponse
    }


}
