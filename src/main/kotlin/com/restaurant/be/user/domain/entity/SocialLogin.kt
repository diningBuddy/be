package com.restaurant.be.user.domain.entity

import com.restaurant.be.user.domain.entity.enum.Social
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.io.Serializable

@Embeddable
data class SocialLogin(
    @Column(name = "user_id")
    var userId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "social_login_type")
    var type: Social? = null
) : Serializable
