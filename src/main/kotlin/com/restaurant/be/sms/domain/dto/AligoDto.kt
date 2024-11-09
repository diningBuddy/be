package com.restaurant.be.sms.domain.dto

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.reflect.full.memberProperties

data class AligoSendSmsRequest(

    // 인증용 API Key
    val key: String,

    // 사용자 ID
    val user_id: String = "",

    // 발신자 전화번호 (최대 16bytes)
    val sender: String,

    // 수신자 전화번호 - 컴마(,)분기 입력으로 최대 1천명
    val receiver: String,

    // 메시지 내용
    val msg: String,

    val testmode_yn: String?
) {
    fun toMultipartData(): MultiValueMap<String, String> =
        LinkedMultiValueMap<String, String>().apply {
            this@AligoSendSmsRequest::class.memberProperties.forEach { prop ->
                prop.getter.call(this@AligoSendSmsRequest)?.let { value ->
                    add(prop.name, value.toString())
                }
            }
        }
}

data class AligoSendSmsResponse(

    // 결과코드
    val result_code: Int,

    // 결과 메시지( result_code 가 0 보다 작은경우 실패사유 표기)
    val message: String,

    // 메시지 고유 ID
    val msg_id: Int?,

    // 요청성공 건수
    val success_cnt: Int?,

    // 요청실패 건수
    val error_cnt: Int?,

    // 메시지 타입 (1. SMS, 2.LMS, 3. MMS)
    val msg_type: String?
)
