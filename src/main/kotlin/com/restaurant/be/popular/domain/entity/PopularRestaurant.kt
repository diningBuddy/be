package com.restaurant.be.popular.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "popular_restaurants")
class PopularRestaurant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var rank: Long,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "original_categories", nullable = false, length = 64)
    var originalCategories: String,

    @Column(name = "longitude")
    var longitude: Double,

    @Column(name = "latitude")
    var latitude: Double,

    @Column(name = "rating_avg")
    var ratingAvg: Double? = 3.0,

    @Column(name = "rating_count")
    var ratingCount: Long?,

    @Column(name = "representative_image_url", length = 300)
    var representativeImageUrl: String

)
