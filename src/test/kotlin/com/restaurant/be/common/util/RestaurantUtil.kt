package com.restaurant.be.common.util

import com.restaurant.be.restaurant.domain.entity.Restaurant
import com.restaurant.be.restaurant.domain.entity.jsonentity.MenuJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.FacilityInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationInfoJsonEntity
import com.restaurant.be.restaurant.domain.entity.kakaoinfo.OperationTimeInfosJsonEntity
import com.restaurant.be.restaurant.repository.dto.FacilityInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.OperationInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.OperationTimeInfosEsDocument
import org.springframework.data.elasticsearch.core.geo.GeoPoint

object RestaurantUtil {

    fun generateRestaurantDocument(
        id: Long,
        name: String = "default_name",
        originalCategory: String = "default_category",
        address: String = "default_address",
        naverReviewCount: Long = 0,
        naverRatingAvg: Double = 0.0,
        reviewCount: Long = 0,
        ratingAvg: Double = 0.0,
        bookmarkCount: Long = 0,
        number: String = "default_number",
        imageUrl: String = "default_image_url",
        category: String = "default_category",
        discountContent: String? = "default_discount_content",
        menus: List<MenuDocument> = emptyList(),
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        facilityInfos: FacilityInfoEsDocument = FacilityInfoEsDocument("N", "N", "N", "N", "N", "N"),
        operationInfos: OperationInfoEsDocument = OperationInfoEsDocument("N", "N", "N"),
        operationTimes: List<OperationTimeInfosEsDocument> = emptyList(),
        kakaoRatingAvg: Double = 0.0,
        kakaoRatingCount: Long = 0,
        ratingCount: Long = 0

    ): RestaurantDocument {
        return RestaurantDocument(
            id = id,
            name = name,
            originalCategory = originalCategory,
            address = address,
            reviewCount = reviewCount,
            ratingAvg = ratingAvg,
            ratingCount = ratingCount,
            number = number,
            imageUrl = imageUrl,
            category = category,
            discountContent = discountContent,
            menus = menus,
            location = GeoPoint(latitude, longitude),
            facilityInfos = facilityInfos,
            operationInfos = operationInfos,
            operationTimes = operationTimes,
            kakaoRatingAvg = kakaoRatingAvg,
            kakaoRatingCount = kakaoRatingCount
        )
    }

    fun generateRestaurantEntity(
        id: Long = 0,
        name: String = "default_name",
        originalCategories: String = "default_category",
        reviewCount: Long = 0,
        bookmarkCount: Long = 0,
        address: String = "default_address",
        contactNumber: String = "default_number",
        ratingAvg: Double = 0.0,
        representativeImageUrl: String = "default_image_url",
        viewCount: Long = 0,
        discountContent: String? = null,
        menus: MutableList<MenuJsonEntity> = mutableListOf(),
        longitude: Double = 0.0,
        latitude: Double = 0.0,
        facilityInfos: FacilityInfoJsonEntity = FacilityInfoJsonEntity("N", "N", "N", "N", "N", "N"),
        operationInfos: OperationInfoJsonEntity = OperationInfoJsonEntity("N", "N", "N"),
        operationTimes: MutableList<OperationTimeInfosJsonEntity> = mutableListOf(),

        kakaoRatingAvg: Double = 0.0,
        kakaoRatingCount: Long = 0,
        ratingCount: Long = 0
    ): Restaurant {
        return Restaurant(
            id = id,
            name = name,
            originalCategories = originalCategories,
            reviewCount = reviewCount,
            bookmarkCount = bookmarkCount,
            address = address,
            contactNumber = contactNumber,
            ratingAvg = ratingAvg,
            representativeImageUrl = representativeImageUrl,
            viewCount = viewCount,
            discountContent = discountContent,
            menus = menus,
            longitude = longitude,
            latitude = latitude,
            facilityInfos = facilityInfos,
            operationInfos = operationInfos,
            operationTimes = operationTimes,
            naverRatingAvg = 0.0,
            naverReviewCount = 0,
            kakaoRatingAvg = 0.0,
            kakaoRatingCount = 0,
            ratingCount = 0
        )
    }

    fun generateMenuJsonEntity(
        name: String = "default_name",
        price: Int = 0,
        description: String = "default_description",
        isRepresentative: Boolean = false,
        imageUrl: String = "default_image_url"
    ): MenuJsonEntity {
        return MenuJsonEntity(
            menuName = name,
            price = price,
            description = description,
            isRepresentative = isRepresentative,
            imageUrl = imageUrl
        )
    }

    fun generateMenuDocument(
        menuName: String = "default_menu_name",
        price: Int = 0,
        description: String = "default_description",
        isRepresentative: String = "default_is_representative",
        imageUrl: String = "default_image_url"
    ): MenuDocument {
        return MenuDocument(
            menuName = menuName,
            price = price,
            description = description,
            isRepresentative = isRepresentative,
            imageUrl = imageUrl
        )
    }
}
