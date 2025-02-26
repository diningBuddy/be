package com.restaurant.be.common.util

import com.restaurant.be.restaurant.repository.dto.FacilityInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.OperationInfoEsDocument
import com.restaurant.be.restaurant.repository.dto.OperationTimeInfosEsDocument
import jakarta.persistence.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.GeoPointField
import org.springframework.data.elasticsearch.core.geo.GeoPoint

@Document(indexName = "restaurant")
data class RestaurantDocument(
    @Id
    @Field(type = FieldType.Long, name = "id")
    val id: Long,

    @Field(type = FieldType.Text, name = "name")
    val name: String,

    @Field(type = FieldType.Text, name = "address")
    val address: String,

    @Field(type = FieldType.Keyword, name = "original_category")
    val originalCategory: String,

    @Field(type = FieldType.Long, name = "review_count")
    val reviewCount: Long? = 0,

    @Field(type = FieldType.Double, name = "rating_avg")
    val ratingAvg: Double?,

    @Field(type = FieldType.Long, name = "rating_count")
    val ratingCount: Long? = 0,

    @Field(type = FieldType.Object, name = "facility_infos")
    val facilityInfos: FacilityInfoEsDocument,

    @Field(type = FieldType.Object, name = "operation_infos")
    val operationInfos: OperationInfoEsDocument,

    @Field(type = FieldType.Nested, name = "operation_times")
    val operationTimes: List<OperationTimeInfosEsDocument>,

    @Field(type = FieldType.Double, name = "kakao_rating_avg")
    val kakaoRatingAvg: Double?,

    @Field(type = FieldType.Long, name = "kakao_rating_count")
    val kakaoRatingCount: Long? = 0,

    @Field(type = FieldType.Text, name = "number")
    val number: String,

    @Field(type = FieldType.Text, name = "image_url")
    val imageUrl: String?,

    @Field(type = FieldType.Text, name = "categories")
    val categories: List<String>,

    @Field(type = FieldType.Text, name = "discount_content")
    val discountContent: String?,

    @Field(type = FieldType.Nested, name = "menus")
    val menus: List<MenuDocument>,

    @Field(type = FieldType.Long, name = "bookmark_count")
    val bookmarkCount: Long,

    @GeoPointField
    val location: GeoPoint

)

data class MenuDocument(
    @Field(type = FieldType.Text, name = "menu_name")
    val menuName: String,

    @Field(type = FieldType.Integer, name = "price")
    val price: Int,

    @Field(type = FieldType.Text, name = "description")
    val description: String,

    @Field(type = FieldType.Text, name = "is_representative")
    val isRepresentative: String,

    @Field(type = FieldType.Text, name = "image_url")
    val imageUrl: String
)
