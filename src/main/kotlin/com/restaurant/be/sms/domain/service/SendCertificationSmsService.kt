package com.restaurant.be.sms.domain.service

import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import com.restaurant.be.sms.repository.SmsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SendCertificationSmsService(
    private val smsRepository: SmsRepository,
    private val redisRepository: RedisRepository
) {

    fun sendCertification(request: SendCertificationSmsRequest) {
        val certificationNumber = (1000..9999).random()
        val sendMessage = """
            [다이닝버디]
            인증번호 : $certificationNumber
            """
        redisRepository.saveCertificationNumber(request.phoneNumber, certificationNumber)
        smsRepository.sendSms(request.phoneNumber, sendMessage)
    }
}
