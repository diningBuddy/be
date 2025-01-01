package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.constant.SocialType
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "social_users")
class SocialUser(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column
    @Enumerated(EnumType.STRING)
    var socialType: SocialType,
    @Column(unique = true)
    var socialKey: String,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User? = null
) : BaseEntity() {
}
