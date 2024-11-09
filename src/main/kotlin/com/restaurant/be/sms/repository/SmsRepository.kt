package com.restaurant.be.sms.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.be.common.exception.AligoSendSmsException
import com.restaurant.be.sms.domain.dto.AligoSendSmsRequest
import com.restaurant.be.sms.domain.dto.AligoSendSmsResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient

@Service
@Transactional(readOnly = true)
class SmsRepository(
    @Value("\${aligo.key}") private val key: String,
    @Value("\${aligo.user-id}") private val userId: String,
    @Value("\${aligo.sender}") private val sender: String,
    @Value("\${aligo.test-mode}") private val testmodeYn: String,

    private val restClient: RestClient,
    private val objectMapper: ObjectMapper
) {

    fun sendSms(receiver: String, sendMessage: String) {
        restClient
            .post()
            .uri("https://apis.aligo.in/send/")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(
                AligoSendSmsRequest(
                    key = key,
                    user_id = userId,
                    sender = sender,
                    receiver = receiver,
                    msg = sendMessage,
                    testmode_yn = testmodeYn
                ).toMultipartData()
            )
            .retrieve()
            .toEntity(String::class.java)
            .body?.let {
                val aligoResponse = objectMapper.readValue(it, AligoSendSmsResponse::class.java)
                if (aligoResponse.result_code < 0) {
                    throw AligoSendSmsException()
                }
            } ?: throw AligoSendSmsException()
    }
}
