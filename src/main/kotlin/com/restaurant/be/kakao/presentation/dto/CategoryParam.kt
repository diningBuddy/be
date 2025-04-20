package com.restaurant.be.kakao.presentation.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.restaurant.be.kakao.domain.entity.ScrapCategory

enum class CategoryParam(val displayName: String) {
    ALL("전체"), KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"),
    CHINESE("중식"), ASIAN("아시안"), CAFE("카페")
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun from(value: String): CategoryParam =
            values().find { it.displayName == value } ?: throw IllegalArgumentException("Unknown value: $value")
    }

    fun toDomain(): ScrapCategory = when (this) {
        ALL -> ScrapCategory.ALL
        KOREAN -> ScrapCategory.KOREAN
        WESTERN -> ScrapCategory.WESTERN
        JAPANESE -> ScrapCategory.JAPANESE
        CHINESE -> ScrapCategory.CHINESE
        ASIAN -> ScrapCategory.ASIAN
        CAFE -> ScrapCategory.CAFE
    }
}
