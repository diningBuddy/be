package com.restaurant.be.common.jwt

interface JwtUserRepository {
    fun validTokenById(userId: Long): Boolean

    fun userRolesById(userId: Long): List<String>
}
