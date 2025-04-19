package com.restaurant.be.home.domain.service

import com.restaurant.be.home.presentation.dto.GetBannerResponse
import com.restaurant.be.home.presentation.dto.GetRecommendationRestaurantsResponse
import com.restaurant.be.home.presentation.dto.HomeRequest
import com.restaurant.be.home.presentation.dto.HomeResponse
import com.restaurant.be.home.presentation.dto.RecommendationType
import com.restaurant.be.restaurant.domain.service.GetRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import com.restaurant.be.restaurant.presentation.controller.dto.Sort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val restaurantService: GetRestaurantService
) {
    companion object {
        const val RECOMMENDATION_SIZE = 5

        private val LUNCH_CATEGORIES = arrayOf(
            "한식", "갈비", "감자탕", "곱창", "막창", "국밥", "국수", "닭강정", "닭요리", "도시락",
            "떡볶이", "매운탕", "해물탕", "분식", "삼겹살", "설렁탕", "순대", "실내포장마차", "육류",
            "고기", "족발", "보쌈", "주먹밥", "찌개", "전골", "치킨", "칼국수", "해물", "생선",
            "해장국", "조개", "회", "양식", "이탈리안", "멕시칸", "브라질", "샌드위치", "샐러드",
            "양꼬치", "피자", "패스트푸드", "햄버거", "일식", "돈까스", "우동", "일본식라면",
            "일본식주점", "오뎅바", "참치회", "초밥", "롤", "중식", "중국요리", "아시안", "베트남음식",
            "동남아음식", "카페", "디저트카페", "아이스크림", "제과", "베이커리", "커피전문점", "간식",
            "장어", "철판요리", "호프", "요리주점", "술집", "퓨전요리", "퓨전한식", "온천", "음식점"
        ).distinct()

        private val MIDNIGHT_CATEGORIES = arrayOf(
            "한식", "갈비", "감자탕", "곱창", "막창", "국밥", "국수", "닭강정", "닭요리", "도시락",
            "떡볶이", "매운탕", "해물탕", "분식", "삼겹살", "설렁탕", "순대", "실내포장마차", "육류",
            "고기", "족발", "보쌈", "주먹밥", "찌개", "전골", "치킨", "칼국수", "해물", "생선",
            "해장국", "조개", "회", "피자", "패스트푸드", "햄버거", "일식", "돈까스", "우동", "일본식라면",
            "일본식주점", "오뎅바", "참치회", "초밥", "롤"
        ).toList()
    }

    private fun createLunchRequest(baseRequest: GetRestaurantsRequest): GetRestaurantsRequest {
        return baseRequest.copy(
            categories = LUNCH_CATEGORIES,
            kakaoRatingAvg = 4.0,
            operationStartTime = "11:00",
            operationEndTime = "15:00"
        )
    }

    private fun createMidnightRequest(baseRequest: GetRestaurantsRequest): GetRestaurantsRequest {
        return baseRequest.copy(
            categories = MIDNIGHT_CATEGORIES,
            kakaoRatingAvg = 3.5,
            operationEndTime = "02:00"
        )
    }

    fun getHome(
        request: HomeRequest,
        userId: Long
    ): HomeResponse {
        val baseRequest = createBaseRequest(request.userLongitude, request.userLatitude)

        val pageable = PageRequest.of(0, RECOMMENDATION_SIZE)
        val lunchResponse = restaurantService.getRestaurants(
            createLunchRequest(baseRequest),
            pageable,
            userId
        )
        val midNightResponse = restaurantService.getRestaurants(
            createMidnightRequest(baseRequest),
            pageable,
            userId
        )

        return HomeResponse(
            restaurantBanner = listOf(
                GetBannerResponse(
                    imageUrl = "http://t1.daumcdn.net/place/F3A68FC964E949C4828CBF47A6297921",
                    title = "율전점 고깃집",
                    subtitle = "맛있는 고기를 파는 율전점의 고깃집입니다."
                )
            ),
            restaurantRecommendations = listOf(
                GetRecommendationRestaurantsResponse(
                    RecommendationType.LAUNCH,
                    lunchResponse.restaurants.content
                ),
                GetRecommendationRestaurantsResponse(
                    RecommendationType.LATE_NIGHT,
                    midNightResponse.restaurants.content
                )
            )
        )
    }

    fun getLunchSectionDetails(
        userId: Long,
        page: Int = 0,
        userLongitude: Double?,
        userLatitude: Double?,
        customPageSize: Int? = null
    ): GetRestaurantsResponse {
        val pageSize = customPageSize ?: if (page == 0) 20 else 10
        val pageable = PageRequest.of(page, pageSize)

        val baseRequest = createBaseRequest(userLongitude, userLatitude)

        return restaurantService.getRestaurants(createLunchRequest(baseRequest), pageable, userId)
        }

    fun getMidnightSectionDetails(
        userId: Long,
        page: Int = 0,
        userLongitude: Double?,
        userLatitude: Double?,
        customPageSize: Int? = null
    ): GetRestaurantsResponse {
        val pageSize = customPageSize ?: if (page == 0) 20 else 10
        val pageable = PageRequest.of(page, pageSize)

        val baseRequest = createBaseRequest(userLongitude, userLatitude)

        return restaurantService.getRestaurants(createMidnightRequest(baseRequest), pageable, userId)
    }

    private fun createBaseRequest(longitude: Double?, latitude: Double?): GetRestaurantsRequest {
        return GetRestaurantsRequest(
            query = null,
            categories = null,
            discountForSkku = null,
            ratingAvg = null,
            reviewCount = null,
            priceMin = null,
            priceMax = null,
            customSort = Sort.CLOSELY_DESC,
            ratingCount = null,
            hasWifi = null,
            hasPet = null,
            hasParking = null,
            hasNursery = null,
            hasSmokingRoom = null,
            hasDisabledFacility = null,
            hasAppointment = null,
            hasDelivery = null,
            hasPackagee = null,
            operationDay = null,
            operationStartTime = null,
            operationEndTime = null,
            breakStartTime = null,
            breakEndTime = null,
            lastOrder = null,
            kakaoRatingAvg = null,
            kakaoRatingCount = null,
            bookmark = null,
            longitude = longitude,
            latitude = latitude
        )
    }
}


