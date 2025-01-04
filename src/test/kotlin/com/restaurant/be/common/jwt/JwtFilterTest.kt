package com.restaurant.be.common.jwt

import com.restaurant.be.common.jwt.JwtFilter.Companion.AUTHORIZATION_HEADER
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class JwtFilterTest :
    DescribeSpec({

        val tokenProvider = mockk<TokenProvider>()
        val jwtUserRepository = mockk<JwtUserRepository>()
        val request = mockk<HttpServletRequest>(relaxed = true)
        val response = mockk<HttpServletResponse>(relaxed = true)
        val filterChain = mockk<FilterChain>(relaxed = true)
        val jwtFilter = JwtFilter(tokenProvider, jwtUserRepository)

        beforeEach {
            clearAllMocks()
            val securityContext = mockk<SecurityContext>()
            SecurityContextHolder.setContext(securityContext)
        }

        describe("doFilterInternal") {

            it("when refresh token is null should validate access token") {
                val accessToken = "validAccessToken"

                every { request.getHeader(AUTHORIZATION_HEADER) } returns accessToken
                every { tokenProvider.resolveToken(accessToken) } returns accessToken
                every { tokenProvider.validateToken(accessToken) } returns true
                every { tokenProvider.getUserIdFromToken(accessToken) } returns 1
                every { jwtUserRepository.validTokenById(any()) } returns true
                val authentication = mockk<Authentication>()
                every { tokenProvider.getAuthentication(accessToken) } returns authentication

                val securityContext = SecurityContextHolder.getContext()
                every { securityContext.authentication = authentication } just Runs

                jwtFilter.doFilterInternal(request, response, filterChain)

                verify { filterChain.doFilter(request, response) }
                verify { securityContext.authentication = authentication }
            }

            it("when refreshToken is null and invalid accessToken should signatureException") {
                val accessToken = "invalidAccessToken"

                every { request.getHeader(AUTHORIZATION_HEADER) } returns accessToken
                every { tokenProvider.resolveToken(accessToken) } returns accessToken
                every { tokenProvider.validateToken(accessToken) } returns false
            }

            it("when accessToken is null should return and not call doFilter") {
                val accessToken = null

                every { request.getHeader(AUTHORIZATION_HEADER) } returns accessToken
                every { tokenProvider.resolveToken(accessToken) } returns accessToken

                jwtFilter.doFilterInternal(request, response, filterChain)
            }
        }
    })
