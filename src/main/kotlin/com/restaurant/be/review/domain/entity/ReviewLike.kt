
package com.restaurant.be.review.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "review_likes")
class ReviewLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "review_id", nullable = false)
    val reviewId: Long
)
