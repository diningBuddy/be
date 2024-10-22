package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.constant.SocialType
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

@Entity
@Table(name = "social_users")
class SocialUser(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var socialType: SocialType = SocialType.KAKAO,

    @Column(unique = true)
    var socialKey: String = "",

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,

    ) : BaseEntity()