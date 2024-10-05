package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.entity.enum.Gender
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToMany(mappedBy = "user")
    var socialAccounts: MutableList<SocialAccount> = mutableListOf(),

    @Column(unique = true)
    var nickname: String = "",

    @Column
    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(nullable = false, unique = true)
    var phoneNumber: String,

    @Column
    var name: String? = null,

    @Column
    var birthDay: LocalDateTime? = null,

    @Column
    @Convert(converter = SeparatorConverter::class)
    var roles: List<String> = listOf(),

    @Column
    var profileImageUrl: String? = null,

    @Column(columnDefinition = "boolean default false")
    var withdrawal: Boolean = false

) : BaseEntity() {
    fun delete() {
        this.withdrawal = true
    }
}
