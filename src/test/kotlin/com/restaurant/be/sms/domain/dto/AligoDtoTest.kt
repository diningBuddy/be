package com.restaurant.be.sms.domain.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AligoDtoTest : DescribeSpec({
    describe("알리고 Dto 테스트") {
        it("알리고 Request 생성 테스트") {
            // Given
            val request = AligoSendSmsRequest(
                key = "key",
                user_id = "userId",
                sender = "01011111111",
                receiver = "01022222222",
                msg = "테스트",
                testmode_yn = "N"
            ).toMultipartData()

            // When

            // Then
            request["key"]!![0] shouldBe "key"
            request["user_id"]!![0] shouldBe "userId"
            request["sender"]!![0] shouldBe "01011111111"
            request["receiver"]!![0] shouldBe "01022222222"
            request["msg"]!![0] shouldBe "테스트"
            request["testmode_yn"]!![0] shouldBe "N"
        }

        it("알리고 Response 생성 테스트") {
            // Given
            val response = AligoSendSmsResponse(
                result_code = 1,
                message = "",
                msg_id = 123456789,
                success_cnt = 2,
                error_cnt = 0,
                msg_type = "SMS"
            )

            // When

            // Then
            response.result_code shouldBe 1
            response.message shouldBe ""
            response.msg_id shouldBe 123456789
            response.success_cnt shouldBe 2
            response.error_cnt shouldBe 0
            response.msg_type shouldBe "SMS"
        }
    }
})
