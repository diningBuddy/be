package com.restaurant.be.home.domain.service

import com.restaurant.be.home.presentation.dto.GetBannerResponse
import com.restaurant.be.home.presentation.dto.GetRecommendationRestaurantsResponse
import com.restaurant.be.home.presentation.dto.HomeRequest
import com.restaurant.be.home.presentation.dto.HomeResponse
import com.restaurant.be.home.presentation.dto.RecommendationType
import com.restaurant.be.kakao.domain.entity.ScrapCategory
import com.restaurant.be.kakao.domain.service.GetPopularRestaurantService
import com.restaurant.be.restaurant.domain.service.GetRestaurantService
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.Sort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val restaurantService: GetRestaurantService,
    private val getPopularRestaurantService: GetPopularRestaurantService
) {
    companion object {
        const val RECOMMENDATION_SIZE = 5
        const val BANNER_MAX_SIZE = 10
    }

    fun getHome(
        request: HomeRequest,
        userId: Long
    ): HomeResponse {
        val categoriesForLunch = arrayOf(
            "한식", "갈비", "감자탕", "곱창", "막창", "국밥", "국수", "닭강정", "닭요리", "도시락",
            "떡볶이", "매운탕", "해물탕", "분식", "삼겹살", "설렁탕", "순대", "실내포장마차", "육류",
            "고기", "족발", "보쌈", "주먹밥", "찌개", "전골", "치킨", "칼국수", "해물", "생선",
            "해장국", "조개", "회", "양식", "이탈리안", "멕시칸", "브라질", "샌드위치", "샐러드",
            "양꼬치", "피자", "패스트푸드", "햄버거", "일식", "돈까스", "우동", "일본식라면",
            "일본식주점", "오뎅바", "참치회", "초밥", "롤", "중식", "중국요리", "아시안", "베트남음식",
            "동남아음식", "카페", "디저트카페", "아이스크림", "제과", "베이커리", "커피전문점", "간식",
            "장어", "철판요리", "호프", "요리주점", "술집", "퓨전요리", "퓨전한식", "온천", "음식점"
        ).distinct()
        val baseRequest = GetRestaurantsRequest(
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
            longitude = request.userLongitude,
            latitude = request.userLatitude
        )
        val categoriesForMidNight = arrayOf(
            "한식", "갈비", "감자탕", "곱창", "막창", "국밥", "국수", "닭강정", "닭요리", "도시락",
            "떡볶이", "매운탕", "해물탕", "분식", "삼겹살", "설렁탕", "순대", "실내포장마차", "육류",
            "고기", "족발", "보쌈", "주먹밥", "찌개", "전골", "치킨", "칼국수", "해물", "생선",
            "해장국", "조개", "회", "피자", "패스트푸드", "햄버거", "일식", "돈까스", "우동", "일본식라면",
            "일본식주점", "오뎅바", "참치회", "초밥", "롤"
        ).toList()
        val lunchRequest = baseRequest.copy(
            categories = categoriesForLunch,
            kakaoRatingAvg = 4.0,
            operationStartTime = "11:00",
            operationEndTime = "15:00"
        )

        val midNightRequest = baseRequest.copy(
            categories = categoriesForMidNight,
            kakaoRatingAvg = 3.5,
            operationEndTime = "02:00"
        )

        val pageable = PageRequest.of(0, RECOMMENDATION_SIZE)

        val lunchResponse = restaurantService.getRestaurants(lunchRequest, pageable, userId)
        val midNightResponse = restaurantService.getRestaurants(midNightRequest, pageable, userId)

        val bannerRestaurants = restaurantService.getPopularRestaurants(
            baseRequest,
            PageRequest.of(0, BANNER_MAX_SIZE),
            userId,
            getPopularRestaurantService
                .getHomeBannerRestaurants(ScrapCategory.ALL)
        )
        return HomeResponse(
            restaurantBanner = bannerRestaurants.restaurants.content
                .filter { !it.representativeImageUrl.isNullOrEmpty() && it.representativeImageUrl != "{}" }
                .map { restaurant ->
                    GetBannerResponse(
                        imageUrl = requireNotNull(restaurant.representativeImageUrl) { "대표 이미지가 없습니다." },
                        title = restaurant.name,
                        subtitle = "맛있는 ${restaurant.name}입니다."
                    )
                }.take(3),
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
}
