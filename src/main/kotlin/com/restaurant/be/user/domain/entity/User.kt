package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true)
    var phoneNumber: String,
    @Column(unique = true)
    var nickname: String,
    @Column
    var name: String,
    @Column
    var birthday: LocalDate,
    @Column
    var gender: Gender,
    @Column
    var isTermsAgreed: Boolean,
    @Column(columnDefinition = "boolean default false")
    var isDeleted: Boolean = false,
    @Convert(converter = SeparatorConverter::class)
    var roles: List<String> = listOf(),
    @Column
    var profileImageUrl: String? = null,
    @OneToMany(mappedBy = "user", fetch = LAZY)
    var socialUsers: MutableList<SocialUser> = mutableListOf()
) : BaseEntity() {
    companion object {
        fun create(request: SignUpUserRequest, nickname: String): User {
            return User(
                phoneNumber = request.phoneNumber,
                nickname = nickname,
                name = request.name,
                birthday = request.birthday,
                gender = request.gender,
                isTermsAgreed = true
            )
        }
    }

    fun delete() {
        this.isDeleted = true
    }
}
