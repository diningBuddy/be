package com.restaurant.be.restaurant.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantEsDocument(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("address") val address: String,
    @SerialName("original_category") val originalCategory: String,
    @SerialName("review_count") val reviewCount: Long?,
    @SerialName("rating_avg") val ratingAvg: Double?,
    @SerialName("bookmark_count") val bookmark_count: Long?,
    @SerialName("rating_count") val ratingCount: Long?,
    @SerialName("facility_infos") val facilityInfos: FacilityInfoEsDocument,
    @SerialName("operation_infos") val operationInfos: OperationInfoEsDocument,
    @SerialName("operation_times") val operationTimeInfos: List<OperationTimeInfosEsDocument>,
    @SerialName("kakao_rating_count") val kakaoRatingCount: Long?,
    @SerialName("kakao_rating_avg") val kakaoRatingAvg: Double?,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("category") val category: String,
    @SerialName("discount_content") val discountContent: String?,
    @SerialName("menus") val menus: List<MenuEsDocument>
)

@Serializable
data class MenuEsDocument(
    @SerialName("menu_name") val menuName: String,
    @SerialName("price") val price: Int,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class FacilityInfoEsDocument(
    @SerialName("pet") val pet: String?,
    @SerialName("wifi") val wifi: String?,
    @SerialName("nursery") val nursery: String?,
    @SerialName("parking") val parking: String?,
    @SerialName("for_disabled") val forDisabled: String?,
    @SerialName("smoking_room") val smokingRoom: String?
)

@Serializable
data class OperationInfoEsDocument(
    @SerialName("package") val packagee: String?,
    @SerialName("delivery") val delivery: String?,
    @SerialName("appointment") val appointment: String?
)

@Serializable
data class OperationTimeInfosEsDocument(
    @SerialName("day_of_the_week") val dayOfTheWeek: String,
    @SerialName("operation_time_info") val operationTimeInfo: OperationTimeInfoEsDocument
)

@Serializable
data class OperationTimeInfoEsDocument(
    @SerialName("start_time") val startTime: String?,
    @SerialName("end_time") val endTime: String?,
    @SerialName("break_start_time") val breakStartTime: String?,
    @SerialName("break_end_time") val breakEndTime: String?,
    @SerialName("last_order") val lastOrder: String?
)
