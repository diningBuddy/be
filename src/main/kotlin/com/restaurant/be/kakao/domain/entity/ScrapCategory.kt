package com.restaurant.be.kakao.domain.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ScrapCategory(private val displayName: String) {
    ALL("전체"), KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"),
    CHINESE("중식"), ASIAN("아시안"), CAFE("카페"), PORK_CUTLET("돈가스"),
    NOODLES("칼국수"), SUSHI("초밥"), TTEOKBOKKI("떡볶이"), PIZZA("피자"),
    BURGER("햄버거"), DUMPLING("만두"), JJAMPPONG("짬뽕"), RICE_NOODLE("쌀국수"),
    MALATANG("마라탕"), LAMB_SKEWER("양꼬치"), RAW_FISH("회");

    @JsonValue
    override fun toString(): String = displayName

}
