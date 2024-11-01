package com.restaurant.be.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.be.common.jwt.JwtAuthenticationEntryPoint
import com.restaurant.be.common.jwt.JwtFilter
import com.restaurant.be.common.jwt.JwtUserRepository
import com.restaurant.be.common.jwt.TokenProvider
import com.restaurant.be.common.redis.RedisRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    val redisRepository: RedisRepository,
    val objectMapper: ObjectMapper,
    val tokenProvider: TokenProvider,
    val jwtUserRepository: JwtUserRepository
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/v1/users/email/sign-up",
                        "/v1/users/email/sign-in",
                        "/v1/users/email/send",
                        "/v1/users/email/validate",
                        "/v1/users/password",
                        "/v1/sms/send-certification-number",
                        "/hello",
                        "/profile",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/webjars/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic { }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }

        val jwtFilter = JwtFilter(tokenProvider, jwtUserRepository)
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
