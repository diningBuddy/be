package com.restaurant.be.kakao.presentation.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.restaurant.be.kakao.domain.entity.ScrapCategory

enum class CategoryParam(val displayName: String) {
    ALL("전체"), KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"),
    CHINESE("중식"), ASIAN("아시안"), CAFE("카페"), PORK_CUTLET("돈가스"),
    NOODLES("칼국수"), SUSHI("초밥"), TTEOKBOKKI("떡볶이"), PIZZA("피자"),
    BURGER("햄버거"), DUMPLING("만두"), JJAMPPONG("짬뽕"), RICE_NOODLE("쌀국수"),
    MALATANG("마라탕"), LAMB_SKEWER("양꼬치"), RAW_FISH("회");

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
        PORK_CUTLET -> ScrapCategory.PORK_CUTLET
        NOODLES -> ScrapCategory.NOODLES
        SUSHI -> ScrapCategory.SUSHI
        TTEOKBOKKI -> ScrapCategory.TTEOKBOKKI
        PIZZA -> ScrapCategory.PIZZA
        BURGER -> ScrapCategory.BURGER
        DUMPLING -> ScrapCategory.DUMPLING
        JJAMPPONG -> ScrapCategory.JJAMPPONG
        RICE_NOODLE -> ScrapCategory.RICE_NOODLE
        MALATANG -> ScrapCategory.MALATANG
        LAMB_SKEWER -> ScrapCategory.LAMB_SKEWER
        RAW_FISH -> ScrapCategory.RAW_FISH
    }
}
