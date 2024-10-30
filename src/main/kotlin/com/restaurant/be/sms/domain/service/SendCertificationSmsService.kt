package com.restaurant.be.sms.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient

@Service
@Transactional(readOnly = true)
class SendCertificationSmsService(
    private val smsService: SmsService,
    private val restClient: RestClient,
    private val redisRepository: RedisRepository,
    private val objectMapper: ObjectMapper
) {

    fun sendCertification(request: SendCertificationSmsRequest) {
        val certificationNumber = (1000..9999).random()
        val sendMessage = """
            [다이닝버디]
            인증번호 : $certificationNumber
            """
        redisRepository.saveCertificationNumber(request.phoneNumber, certificationNumber)
        smsService.sendSms(request.phoneNumber, sendMessage)
    }
}
