package com.restaurant.be.restaurant.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurants")
class Restaurant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long,

    @Column(name = "name", nullable = false, length = 64)
    var name: String,

    @Column(name = "original_categories", nullable = false, length = 64)
    var originalCategories: String,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Long = 0,

    @Column(name = "bookmark_count", nullable = false)
    var bookmarkCount: Long = 0,

    @Column(name = "address", length = 256)
    var address: String,

    @Column(name = "contact_number", length = 32)
    var contactNumber: String,

    @Column(name = "rating_avg")
    var ratingAvg: Double,

    @Column(name = "rating_count")
    var ratingCount: Long,

    @Column(name = "facility_infos")
    @Lob
    var facilityInfos: String,

    @Column(name = "operation_infos")
    @Lob
    var operationInfos: String,

    @Column(name = "operation_times",columnDefinition = "TEXT")
    var operationTimes: String,

    @Column(name = "representative_image_url", length = 300)
    var representativeImageUrl: String,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @Column(name = "discount_content")
    var discountContent: String? = null,

    @Column(name = "longitude")
    var longitude: Double,

    @Column(name = "latitude")
    var latitude: Double,

    @Column(name = "naver_rating_avg")
    var naverRatingAvg: Double,

    @Column(name = "naver_review_count")
    var naverReviewCount: Int,

    @Column(name = "kakao_rating_avg")
    var kakaoRatingAvg: Double,

    @Column(name = "kakao_rating_count")
    var kakaoRatingCount: Long,

    @OneToMany(mappedBy = "restaurantId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var menus: MutableList<Menu> = mutableListOf()
) {
    fun createReview(newRating: Double) {
        val beforeCount = reviewCount
        reviewCount++
        ratingAvg = (ratingAvg * beforeCount + newRating) / (beforeCount + 1)
    }

    fun deleteReview(beforeRating: Double) {
        val beforeCount = reviewCount
        if (beforeCount <= 1) {
            ratingAvg = 0.0
            reviewCount = 0
        } else {
            ratingAvg = (ratingAvg * beforeCount - beforeRating) / (beforeCount - 1)
            reviewCount = beforeCount - 1
        }
    }

    fun updateReview(beforeRating: Double, newRating: Double) {
        ratingAvg = ((ratingAvg * reviewCount) - beforeRating + newRating) / reviewCount
    }
}
