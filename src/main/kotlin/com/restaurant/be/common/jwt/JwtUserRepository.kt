package com.restaurant.be.common.jwt

interface JwtUserRepository {

    fun validTokenByPhoneNumber(phoneNumber: String): Boolean
    fun userRolesByPhoneNumber(phoneNumber: String): List<String>
}
