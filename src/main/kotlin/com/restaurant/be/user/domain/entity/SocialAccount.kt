package com.restaurant.be.user.domain.entity

import com.restaurant.be.user.domain.entity.enum.Social
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "social_accounts")
class SocialAccount(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,

    var socialType: Social,

    @Column(unique = true, nullable = false)
    var email: String = ""
)
