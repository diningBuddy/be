package com.restaurant.be.hello.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "hellos")
data class Hello(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var name: String
) : BaseEntity()
