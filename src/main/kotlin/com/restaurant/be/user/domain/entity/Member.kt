package com.restaurant.be.user.domain.entity

import com.restaurant.be.point.domain.PointDetail
import com.restaurant.be.user.domain.Gender
import com.restaurant.be.user.domain.entity.embed.AccountPeriod
import com.restaurant.be.user.domain.entity.embed.Privacy
import java.util.*
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Member(
    @Id
    @GeneratedValue
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "nick_name")
    var nickname: String? = null,

    @Column(name = "points")
    var points: Long? = PointDetail.REGISTER.deltaPoint,

    @Column(name = "image_path")
    var imagePath: String? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Embedded
    var privacy: Privacy,

    @Embedded
    var accountPeriod: AccountPeriod
) {
    fun isDeleted(): Boolean {
        return Objects.isNull(accountPeriod.deletedAt)
    }
}
