package com.restaurant.be.user.domain.entity

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Certification(
    @Id
    @GeneratedValue
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "certifcation_number", length = 6)
    var certificationNumber: String? = null,

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime = createdAt.plusMinutes(5),

    @Column(name = "valid")
    var valid: Boolean = true,

    @Column(name = "verified")
    var verified: Boolean = false
)
