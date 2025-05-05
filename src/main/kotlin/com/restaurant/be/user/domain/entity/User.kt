package com.restaurant.be.user.domain.entity

import com.restaurant.be.common.converter.SeparatorConverter
import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.common.exception.AlreadySchoolEmailAuthenticationException
import com.restaurant.be.common.exception.NotFoundUserException
import com.restaurant.be.user.domain.constant.Gender
import com.restaurant.be.user.domain.constant.School
import com.restaurant.be.user.presentation.dto.SignUpUserRequest
import com.restaurant.be.user.presentation.dto.UpdateUserRequest
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(
            name = "UK_USERS_PHONE_ACTIVE",
            columnNames = ["phoneNumber", "isDeleted"],
        ),
        UniqueConstraint(
            name = "UK_USERS_NICKNAME_ACTIVE",
            columnNames = ["nickname", "isDeleted"],
        ),
    ],
)
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
    @Column
    @Enumerated(EnumType.STRING)
    var verifiedSchool: School = School.SKKU,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], fetch = LAZY)
    var socialUsers: MutableList<SocialUser> = mutableListOf(),
) : BaseEntity() {
    companion object {
        fun create(
            request: SignUpUserRequest,
            nickname: String,
        ): User =
            User(
                phoneNumber = request.phoneNumber,
                nickname = nickname,
                name = request.name,
                birthday = request.birthday,
                gender = request.gender,
                isTermsAgreed = true,
                roles = listOf("ROLE_USER"),
                verifiedSchool = School.SKKU, // 향후 타학교에도 서비스 제공할때 변경
            )
    }

    fun getId(): Long = id ?: throw NotFoundUserException()

    fun delete() {
        this.isDeleted = true
    }

    fun update(req: UpdateUserRequest) {
        this.nickname = req.nickname
        this.name = req.name
        this.profileImageUrl = req.profileImageUrl
        this.gender = req.gender
    }

    fun schoolEmailAuthentication() {
        verifiedSchool = School.SKKU
    }

    fun isVerifiedSchool() {
        if (verifiedSchool != null) {
            throw AlreadySchoolEmailAuthenticationException()
        }
    }
}
