package com.restaurant.be.user.domain.entity

import com.restaurant.be.user.domain.entity.enum.Social
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class SocialLogin(
    @Column(name = "user_id")
    var userId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "social_login_type")
    var type: Social? = null
) : Serializable
