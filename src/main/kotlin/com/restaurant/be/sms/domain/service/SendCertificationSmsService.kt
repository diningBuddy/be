package com.restaurant.be.sms.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.be.common.exception.AligoSendSmsException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.sms.domain.dto.AligoSendSmsRequest
import com.restaurant.be.sms.domain.dto.AligoSendSmsResponse
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient

@Service
class SendCertificationSmsService(
    @Value("\${aligo.key}") private val key: String,
    @Value("\${aligo.user-id}") private val userId: String,
    @Value("\${aligo.sender}") private val sender: String,
    @Value("\${aligo.test-mode}") private val testmodeYn: String,

    private val restClient: RestClient,
    private val redisRepository: RedisRepository,
    private val objectMapper: ObjectMapper
) {

    fun sendCertification(request: SendCertificationSmsRequest) {
        val certificationNumber = (1000..9999).random()
        val aligoRequest = AligoSendSmsRequest.createSendCertificationSmsRequest(
            key = key,
            user_id = userId,
            sender = sender,
            testmode_yn = testmodeYn,
            receiver = request.phoneNumber,
            certificationNumber = certificationNumber
        )

        redisRepository.saveCertificationNumber(request.phoneNumber, certificationNumber)
        aligoSendCertification(aligoRequest)
    }

    private fun aligoSendCertification(aligoRequest: MultiValueMap<String, String>) {
        restClient
            .post()
            .uri("https://apis.aligo.in/send/")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(aligoRequest)
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
