package com.restaurant.be.search.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.entity.User
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class RecentSearch(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var keyword: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User
) : BaseEntity() {
    companion object {
        fun create(
            keyword: String,
            user: User
        ): RecentSearch =
            RecentSearch(
                keyword = keyword,
                user = user
            )
    }
}
