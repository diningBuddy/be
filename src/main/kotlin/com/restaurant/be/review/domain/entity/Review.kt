package com.restaurant.be.review.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.common.exception.InvalidLikeCountException
import com.restaurant.be.review.presentation.dto.UpdateReviewRequest
import com.restaurant.be.user.domain.entity.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "restaurant_reviews")
class Review(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val restaurantId: Long,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var rating: Double,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "review_id")
    var images: MutableList<ReviewImage> = mutableListOf()

) : BaseEntity() {
    fun addImage(reviewImage: ReviewImage) {
        images.add(reviewImage)
    }

    fun updateReview(request: UpdateReviewRequest) {
        val updateRequest = request.review
        this.content = updateRequest.content
        this.rating = updateRequest.rating
        this.images.clear()
        updateRequest.imageUrls.forEach {
            this.addImage(
                ReviewImage(
                    imageUrl = it
                )
            )
        }
    }

    fun incrementViewCount() {
        this.viewCount++
    }

    fun incrementLikeCount() {
        this.likeCount++
    }

    fun decrementLikeCount() {
        if (this.likeCount == 0L) {
            throw InvalidLikeCountException()
        }
        this.likeCount--
    }
}
