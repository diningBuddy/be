package com.restaurant.be.inquiry.presentation.controller.dto

import com.restaurant.be.inquiry.domain.entity.InquiryCategory
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class CreateInquiryResponse(
    @Schema(description = "문의 ID", example = "1")
    val id: Long,

    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @Schema(description = "제목", example = "문의 제목")
    val title: String,

    @Schema(description = "내용", example = "문의 내용")
    val content: String,

    @Schema(description = "카테고리", example = "MEMBER")
    val category: InquiryCategory,

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg", nullable = true)
    val imageUrl: String?,

    @Schema(description = "생성 시간", example = "2023-01-01T00:00:00")
    val createdAt: LocalDateTime
)
