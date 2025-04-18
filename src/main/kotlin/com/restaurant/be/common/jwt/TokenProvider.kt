package com.restaurant.be.common.jwt

import com.restaurant.be.common.exception.InvalidTokenException
import com.restaurant.be.common.redis.RedisRepository
import com.restaurant.be.common.response.Token
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(
    @param:Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.token-validity-in-seconds}") tokenValidityInMilliseconds: Long,
    @Value("\${jwt.access-token-validity-in-seconds}") accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refresh-token-validity-in-seconds}") refreshTokenValidityInSeconds: Long,
    private val redisRepository: RedisRepository
) : InitializingBean {
    private final val tokenValidityInMilliseconds: Long
    private final val accessTokenValidityInMilliseconds: Long
    final val refreshTokenValidityInMilliseconds: Long
    var key: Key? = null

    init {
        accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000
        refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds
    }

    val log = KotlinLogging.logger {}

    fun createTokens(
        userId: Long,
        roles: List<String>
    ): Token {
        val claims: MutableMap<String, Any?> = HashMap()
        claims[AUTHORITIES_KEY] = roles.joinToString(",")

        return doGenerateToken(claims, userId)
    }

    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secret)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun getAllClaimsFromToken(token: String?): Claims =
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

    fun getUserIdFromToken(token: String): Long = getAllClaimsFromToken(token).subject.toLong()

    fun getIdFromExpiredToken(token: String): Long =
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body.subject
                .toLong()

            // 토큰 파싱 및 ID 추출
        } catch (ex: ExpiredJwtException) {
            // 만료된 토큰이어도 ID는 추출
            ex.claims.subject.toLong()
        }

    fun getRolesFromToken(token: String): String = getAllClaimsFromToken(token)["roles"].toString()

    fun getAuthentication(token: String?): Authentication {
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        val authorities: Collection<GrantedAuthority> =
            Arrays
                .stream(
                    claims[AUTHORITIES_KEY]
                        .toString()
                        .split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                ).map { role: String? ->
                    SimpleGrantedAuthority(
                        role
                    )
                }.collect(Collectors.toList())
        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun resolveToken(token: String?): String? {
        if (token == null) {
            return null
        }

        if (!StringUtils.hasText(token)) {
            return null
        }

        if (!token.startsWith("Bearer ")) {
            return null
        }

        return token.substring(7)
    }

    fun tokenReissue(
        accessToken: String,
        refreshToken: String
    ): Token {
        val createdDate = Date()
        val userId = getUserIdFromToken(accessToken)
        val refreshTokenInDBMS = redisRepository.getValue("RT:$userId")
        if (!refreshTokenInDBMS.equals(refreshToken)) {
            throw InvalidTokenException()
        }
        val userRoles = getRolesFromToken(accessToken)

        val newAccessToken = createAccessToken(userId, listOf(userRoles))
        val authentication: Authentication = getAuthentication(accessToken)
        SecurityContextHolder.getContext().authentication = authentication

        return Token(
            newAccessToken,
            refreshToken,
            "Bearer",
            accessTokenValidityInMilliseconds,
            createdDate
        )
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
        }
        return false
    }

    fun createAccessToken(
        userId: Long,
        roles: List<String>
    ): String {
        val claims: MutableMap<String, Any?> = HashMap()
        val createdDate = Date()
        val accessTokenExpirationDate =
            Date(createdDate.time + accessTokenValidityInMilliseconds * 1000)
        claims[AUTHORITIES_KEY] = roles.joinToString(",")
        return createToken(claims, userId, accessTokenExpirationDate)
    }

    private fun doGenerateToken(
        claims: Map<String, Any?>,
        userId: Long
    ): Token {
        val createdDate = Date()
        val accessTokenExpirationDate =
            Date(createdDate.time + accessTokenValidityInMilliseconds * 1000)
        val refreshTokenExpirationDate =
            Date(createdDate.time + refreshTokenValidityInMilliseconds * 1000)

        val accessToken = createToken(claims, userId, accessTokenExpirationDate)
        val refreshToken = createToken(claims, userId, refreshTokenExpirationDate)

        return Token(
            accessToken,
            refreshToken,
            "Bearer",
            accessTokenValidityInMilliseconds,
            createdDate
        )
    }

    fun createToken(
        claims: Map<String, Any?>,
        userId: Long,
        expirationDate: Date
    ): String {
        val createdDate = Date()

        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userId.toString())
            .setIssuedAt(createdDate)
            .setExpiration(expirationDate)
            .signWith(key)
            .compact()
    }

    companion object {
        private const val AUTHORITIES_KEY = "roles"
    }
}
