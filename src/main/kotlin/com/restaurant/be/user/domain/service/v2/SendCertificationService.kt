package com.restaurant.be.user.domain.service.v2

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.be.common.exception.MessageServerException
import com.restaurant.be.common.exception.TooManyCertifyRequestException
import com.restaurant.be.user.presentation.dto.certification.SendCertificationRequest
import com.restaurant.be.user.presentation.dto.certification.SendCertificationResponse
import com.restaurant.be.user.presentation.dto.certification.SendMessageResponse
import com.restaurant.be.user.repository.v2.certification.CertifyUserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*
import kotlin.random.Random

@Service
class SendCertificationService(
    private val sendCertificationRepository: CertifyUserRepository,
    private val aligoClient: WebClient,
    private val environment: Environment,
    @Value("\${aligo.key}") private val apiKey: String,
    @Value("\${aligo.userId}") private val userId: String,
    @Value("\${aligo.sender}") private val sender: String
) {

    @Transactional
    fun sendCertificationToUser(request: SendCertificationRequest): SendCertificationResponse {
        val phoneNumber = request.phoneNumber

        handleTooManySendMessages(phoneNumber)
        handleExistingCertifications(phoneNumber)

        val randomUUID = createRandomUUID()
        postMessage(phoneNumber, createMessage(randomUUID))
            .subscribe {
                    response ->
                val messageServerResponse = parsingResponse(response) ?: throw MessageServerException()
                // ToDO(" 결제 이후, notYetPayMessagingServiceHandling(messageServerResponse) 제외할것 ")
                if (notYetPayMessagingServiceHandling(messageServerResponse) || messageServerResponse.resultCode >= 0) {
                    sendCertificationRepository.save(request.toEntity(randomUUID))
                } else if (messageServerResponse.resultCode < 0) {
                    throw MessageServerException(messageServerResponse.message)
                }
            }

        return SendCertificationResponse(phoneNumber)
    }

    private fun notYetPayMessagingServiceHandling(messageServerResponse: SendMessageResponse) =
        messageServerResponse.resultCode == -201

    private fun parsingResponse(response: String): SendMessageResponse? {
        val objectMapper = ObjectMapper()
        return try {
            objectMapper.readValue(String(response.toByteArray(), Charsets.UTF_8), SendMessageResponse::class.java)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Failed to parse response", e)
        }
    }
    private fun isDeployment(): Boolean {
        return Objects.nonNull(environment.activeProfiles)
    }
    private fun handleTooManySendMessages(phoneNumber: String) {
        if (sendCertificationRepository.findWhetherTooManyRequest(phoneNumber)) {
            throw TooManyCertifyRequestException()
        }
    }

    private fun handleExistingCertifications(phoneNumber: String) {
        val existingCertifications = sendCertificationRepository.findByPhoneNumberAndValid(phoneNumber, true)
        if (existingCertifications.isNotEmpty()) {
            for (certification in existingCertifications) {
                certification.valid = false
            }
        }
    }

    private fun createRandomUUID(): String {
        val randomNumber = Random.nextInt(0, 1000000)
        return randomNumber.toString().padStart(6, '0')
    }

    private fun createMessage(randomUUID: String): String {
        return """
            인증번호 [$randomUUID]
            '다이닝버디' 회원가입/로그인 인증번호입니다. 
        """.trimIndent()
    }
    private fun postMessage(
        receiver: String,
        msg: String
    ): Mono<String> {
        return aligoClient.post()
            .body(
                BodyInserters.fromFormData("key", apiKey)
                    .with("user_id", userId)
                    .with("sender", sender)
                    .with("receiver", receiver)
                    .with("msg", msg)
                    .with("testmode_yn", if (isDeployment()) "N" else "Y")
            )
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
