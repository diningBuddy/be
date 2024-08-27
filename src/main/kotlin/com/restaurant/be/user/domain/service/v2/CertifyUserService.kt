package com.restaurant.be.user.domain.service.v2

import com.restaurant.be.common.exception.DuplicateCertificationException
import com.restaurant.be.user.domain.entity.Certification
import com.restaurant.be.user.presentation.dto.certification.CertifyUserRequest
import com.restaurant.be.user.presentation.dto.certification.CertifyUserResponse
import com.restaurant.be.user.repository.UserRepository
import com.restaurant.be.user.repository.v2.certification.CertifyUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CertifyUserService(
    private val sendCertificationRepository: CertifyUserRepository,
    private val userRepository: UserRepository
) {
    fun certify(request: CertifyUserRequest): CertifyUserResponse {
        val phoneNumber = request.phoneNumber
        val certifications = sendCertificationRepository.findByPhoneNumberAndValid(phoneNumber, true)

        if (certifications.size > 1 || certifications.isEmpty()) {
            throw DuplicateCertificationException()
        }

        val certification = certifications.get(0)

        if (isSameCertificationNumber(certification, request)) {
            val response = validate(certification.expiredAt, LocalDateTime.now())
            if (response.isAuthenticated) {
                certification.verified = true
                userRepository.save(request.toUserEntity())
            }
            return response
        } else {
            return CertifyUserResponse(
                isAuthenticated = false,
                message = "인증번호가 일치하지 않습니다."
            )
        }
    }
    private fun validate(expiredTime: LocalDateTime, currentTime: LocalDateTime): CertifyUserResponse {
        if (expiredTime.isBefore(currentTime)) {
            return CertifyUserResponse(
                isAuthenticated = false,
                message = "유효시간이 지난 인증번호입니다. 다시 발급 받아 주세요"
            )
        }
        return CertifyUserResponse(
            isAuthenticated = true,
            message = "인증이 완료되었습니다."
        )
    }
    private fun isSameCertificationNumber(
        certification: Certification,
        request: CertifyUserRequest
    ) = certification.certificationNumber.equals(request.certificationNumber)
}
