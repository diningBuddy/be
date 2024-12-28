package com.restaurant.be.common.jwt

interface JwtUserRepository {

    fun validTokenById(phoneNumber: String): Boolean
    fun userRolesById(phoneNumber: String): List<String>
}
