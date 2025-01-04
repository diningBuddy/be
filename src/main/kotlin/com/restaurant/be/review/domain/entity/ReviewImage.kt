package com.restaurant.be.review.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "review_images")
class ReviewImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 300)
    val imageUrl: String
)
