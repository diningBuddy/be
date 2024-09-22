package com.restaurant.be.user.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
class Account(
    @EmbeddedId
    var id: SocialLogin,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(unique = true, nullable = false)
    var email: String = ""
)
