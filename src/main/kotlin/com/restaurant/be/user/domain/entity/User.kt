package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.user.domain.entity.enum.Gender
import java.time.LocalDateTime
import java.util.*
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

    @Column
    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(nullable = false, unique = true)
    var phoneNumber: String,

    @Column
    var realName: String? = null,

    @Column
    var birthDay: LocalDateTime? = null,

    @Column
    @Convert(converter = SeparatorConverter::class)
    var roles: List<String> = listOf(),

    @Column
    var profileImageUrl: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column
    var deletedAt: LocalDateTime? = null
) {
    fun isDeleted(): Boolean {
        return Objects.nonNull(deletedAt)
    }
}
