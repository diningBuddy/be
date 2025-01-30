package com.restaurant.be.sms.domain.service

import com.restaurant.be.common.exception.InvalidCertificationNumberException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.sms.presentation.dto.SendCertificationSmsRequest
import com.restaurant.be.sms.presentation.dto.VerifyCertificationSmsRequest
import com.restaurant.be.sms.repository.SmsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CertificationSmsService(
    private val smsRepository: SmsRepository,
    private val redisRepository: RedisRepository
) {
    fun sendCertificationNumber(request: SendCertificationSmsRequest) {
//        val certificationNumber = (1000..9999).random()
//        val sendMessage = """
//            [다이닝버디]
//            인증번호 : $certificationNumber
//            """
        // 당분간은 인증번호 고정
        val certificationNumber = 1111
        redisRepository.saveCertificationNumber(request.phoneNumber, certificationNumber)
//        smsRepository.sendSms(request.phoneNumber, sendMessage)
    }

    fun verifyCertificationNumber(request: VerifyCertificationSmsRequest) {
        val certificationNumber = redisRepository.getCertificationNumber(request.phoneNumber)
        if (request.certificationNumber != certificationNumber) {
            throw InvalidCertificationNumberException()
        }
    }
}
