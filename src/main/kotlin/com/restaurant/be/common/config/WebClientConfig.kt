package com.restaurant.be.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun aligoClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://apis.aligo.in/send/")
            .build()
    }
}
