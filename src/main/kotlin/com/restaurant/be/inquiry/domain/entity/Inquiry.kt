package com.restaurant.be.inquiry.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inquiries")
class Inquiry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "email", nullable = false, length = 256)
    var email: String,

    @Column(name = "title", nullable = false, length = 256)
    var title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: InquiryCategory,

    @Column(name = "image_url", nullable = true, length = 512)
    var imageUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)

enum class InquiryCategory {
    MEMBER,
    PAYMENT_REFUND,
    SERVICE
}
