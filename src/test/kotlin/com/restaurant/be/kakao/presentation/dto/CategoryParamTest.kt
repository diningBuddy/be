package com.restaurant.be.kakao.presentation.dto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CategoryParamTest : DescribeSpec({

    describe("CategoryParam") {
        describe("from") {
            it("should convert string to CategoryParam") {
                // When/Then
                CategoryParam.from("전체") shouldBe CategoryParam.ALL
                CategoryParam.from("한식") shouldBe CategoryParam.KOREAN
                CategoryParam.from("양식") shouldBe CategoryParam.WESTERN
                CategoryParam.from("일식") shouldBe CategoryParam.JAPANESE
                CategoryParam.from("중식") shouldBe CategoryParam.CHINESE
                CategoryParam.from("아시안") shouldBe CategoryParam.ASIAN
                CategoryParam.from("카페") shouldBe CategoryParam.CAFE
            }

            it("should throw exception for unknown value") {
                // When/Then
                shouldThrow<IllegalArgumentException> {
                    CategoryParam.from("unknown")
                }.message shouldBe "Unknown value: unknown"
            }
        }

        describe("toDomain") {
            it("should convert to ScrapCategory") {
                // When/Then
                CategoryParam.ALL.toDomain().name shouldBe "ALL"
                CategoryParam.KOREAN.toDomain().name shouldBe "KOREAN"
                CategoryParam.WESTERN.toDomain().name shouldBe "WESTERN"
                CategoryParam.JAPANESE.toDomain().name shouldBe "JAPANESE"
                CategoryParam.CHINESE.toDomain().name shouldBe "CHINESE"
                CategoryParam.ASIAN.toDomain().name shouldBe "ASIAN"
                CategoryParam.CAFE.toDomain().name shouldBe "CAFE"
            }
        }

        describe("display name") {
            it("should have correct display names") {
                // When/Then
                CategoryParam.ALL.displayName shouldBe "전체"
                CategoryParam.KOREAN.displayName shouldBe "한식"
                CategoryParam.WESTERN.displayName shouldBe "양식"
                CategoryParam.JAPANESE.displayName shouldBe "일식"
                CategoryParam.CHINESE.displayName shouldBe "중식"
                CategoryParam.ASIAN.displayName shouldBe "아시안"
                CategoryParam.CAFE.displayName shouldBe "카페"
            }
        }
    }
})
