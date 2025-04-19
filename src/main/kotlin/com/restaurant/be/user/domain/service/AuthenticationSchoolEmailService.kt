package com.restaurant.be.user.domain.service

import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.common.exception.SkkuEmailException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.user.presentation.dto.SendAuthenticationSchoolEmailRequest
import com.restaurant.be.user.repository.UserRepository
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthenticationSchoolEmailService(
    @Value("\${spring.mail.username}") private val fromAddress: String,
    private val mailSender: JavaMailSender,
    private val redisRepository: RedisRepository,
    private val userRepository: UserRepository
) {
    fun sendAuthenticationSchoolEmail(userId: Long, baseUrl: String, request: SendAuthenticationSchoolEmailRequest) {
        if (!(request.email.endsWith("@skku.edu") || request.email.endsWith("@g.skku.edu"))) {
            throw SkkuEmailException()
        }

        val authenticationSchoolEmailUuid = UUID.randomUUID().toString()
        val user = userRepository.findById(userId).orElseThrow {
            NotFoundUserException()
        }

        user.isVerifiedSchool()

        redisRepository.saveAuthenticationSchoolEmail(userId, authenticationSchoolEmailUuid)

        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val to = request.email

        // 이메일 설정
        helper.setFrom(fromAddress)
        helper.setTo(to)
        helper.setSubject("[Dining Buddy] 이메일 주소 인증")

        // HTML 콘텐츠 직접 작성
        val resource = this::class.java.classLoader.getResource("templates/email-verification.html")
        val htmlContent = resource!!.readText(Charsets.UTF_8)
            .replace("\${username}", "최대현")
            .replace("\${email}", to)
            .replace(
                "\${verificationLink}",
                "$baseUrl/v1/users/school-email/authentication?authenticationSchoolEmailUuid=$authenticationSchoolEmailUuid&userId=$userId"
            )

        // 이메일 내용 설정 (HTML 형식)
        helper.setText(htmlContent, true)

        // 이메일 발송
        mailSender.send(message)
    }

    @Transactional
    fun schoolEmailAuthentication(authenticationSchoolEmailUuid: String, userId: Long) {
        redisRepository.authenticationSchoolEmail(userId, authenticationSchoolEmailUuid)
        val user = userRepository.findById(userId).orElseThrow {
            NotFoundUserException()
        }
        user.schoolEmailAuthentication()
    }
}
