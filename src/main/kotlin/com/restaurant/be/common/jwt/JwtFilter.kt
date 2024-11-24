package com.restaurant.be.common.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SignatureException

class JwtFilter(
    private val tokenProvider: TokenProvider,
    private val jwtUserRepository: JwtUserRepository
) : OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI

        val accessToken = tokenProvider.resolveToken(request.getHeader(AUTHORIZATION_HEADER))

        if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) { // 토큰의 유효성이 검증됐을 경우,
            if (jwtUserRepository.validTokenByPhoneNumber(tokenProvider.getIdFromToken(accessToken!!))) {
                val authentication: Authentication =
                    tokenProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI)
            request.setAttribute("exception", SignatureException())
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
