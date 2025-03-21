package com.restaurant.be.restaurant.domain.entity

import com.restaurant.be.restaurant.presentation.controller.dto.common.MenuDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "menus")
class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long,

    @Column(name = "restaurant_id", nullable = false)
    var restaurantId: Long,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "price")
    var price: Int,

    @Column(name = "description")
    var description: String,

    @Column(name = "is_representative")
    var isRepresentative: Boolean,

    @Column(name = "image_url", columnDefinition = "varchar(500)")
    var imageUrl: String
) {
    fun toDto(): MenuDto {
        return MenuDto(
            name = this.name,
            price = this.price,
            description = this.description,
            isRepresentative = isRepresentative,
            imageUrl = this.imageUrl
        )
    }
}
