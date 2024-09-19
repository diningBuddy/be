package com.restaurant.be.user.domain.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

@Entity
@Table(name = "accounts")
class Account(
    @EmbeddedId
    var id: SocialLogin,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User

)
