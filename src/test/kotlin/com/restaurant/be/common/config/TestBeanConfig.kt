package com.restaurant.be.common.config

import com.restaurant.be.sms.domain.service.SmsService
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestBeanConfig {
    @Bean
    @Primary
    fun mockSmsService() = mockk<SmsService>(relaxed = true) {
        every { sendSms(any<String>(), any<String>()) } returns Unit
    }
}
