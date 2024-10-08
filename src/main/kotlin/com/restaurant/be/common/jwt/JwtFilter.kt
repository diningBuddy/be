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
        val refreshToken = tokenProvider.resolveToken(request.getHeader(REFRESH_TOKEN_HEADER))

        if (refreshToken == null) {
            if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) { // 토큰의 유효성이 검증됐을 경우,
                if (jwtUserRepository.validTokenByEmail(tokenProvider.getEmailFromToken(accessToken!!))) {
                    val authentication: Authentication =
                        tokenProvider.getAuthentication(accessToken)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI)
                request.setAttribute("exception", SignatureException())
            }
        } else {
            tokenProvider.validateToken(refreshToken)

            if (accessToken == null) {
                log.debug("accessToken 이 존재하지 않습니다., uri: {}", requestURI)
                return
            }

            val newAccessToken = tokenProvider.tokenReissue(accessToken, refreshToken)

            val header = response.getHeader(AUTHORIZATION_HEADER)
            if (header == null || "" == header) {
                response.addHeader(AUTHORIZATION_HEADER, newAccessToken.accessToken)
            }
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val REFRESH_TOKEN_HEADER = "Refresh-Token"
    }
}
