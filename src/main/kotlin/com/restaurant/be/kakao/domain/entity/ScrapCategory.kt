package com.restaurant.be.kakao.domain.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ScrapCategory(private val displayName: String) {
    ALL("전체"), KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"),
    CHINESE("중식"), ASIAN("아시안"), CAFE("카페")
    ;

    @JsonValue
    override fun toString(): String = displayName
}
