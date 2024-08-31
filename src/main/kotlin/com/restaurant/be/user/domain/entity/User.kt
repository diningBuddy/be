package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.point.domain.PointDetail
import com.restaurant.be.user.domain.Gender
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true)
    var nickname: String = "",

    @Column(unique = true)
    var email: String = "",

    @Column(name = "points")
    var points: Long? = PointDetail.REGISTER.deltaPoint,

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(nullable = false)
    var phoneNumber: String,

    @Column(name = "real_name")
    var realName: String? = null,

    @Column(name = "birth_day")
    var birthDay: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @Column(columnDefinition = "boolean default false")
    var withdrawal: Boolean = false,

    @Convert(converter = SeparatorConverter::class)
    var roles: List<String> = listOf(),

    @Column(nullable = false)
    var profileImageUrl: String
) {
    fun updateUser(request: UpdateUserRequest) {
        this.nickname = request.nickname
        this.profileImageUrl = request.profileImageUrl
    }

    fun delete() {
        this.withdrawal = true
        this.deletedAt = LocalDateTime.now()
        this.email = ""
    }

    fun isDeleted(): Boolean {
        return this.withdrawal
    }
}
