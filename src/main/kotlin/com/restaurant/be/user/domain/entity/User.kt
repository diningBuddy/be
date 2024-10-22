package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.constant.Gender
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true)
    var phoneNumber: String = "",

    @Column(unique = true)
    var nickname: String = "",

    @Column
    var name: String = "",

    @Column
    var birthday: LocalDate = LocalDate.now(),

    @Column
    var gender: Gender = Gender.MAN,

    @Column(columnDefinition = "boolean default false")
    var isTermsAgreed: Boolean = false,

    @Column(columnDefinition = "boolean default false")
    var isDeleted: Boolean = false,

    @Convert(converter = SeparatorConverter::class)
    var roles: List<String> = listOf(),

    @Column
    var profileImageUrl: String? = null,

    @OneToMany(mappedBy = "user", fetch = LAZY)
    var socialUserList: MutableList<SocialUser> = mutableListOf()

) : BaseEntity() {
    fun delete() {
        this.isDeleted = true
    }
}
