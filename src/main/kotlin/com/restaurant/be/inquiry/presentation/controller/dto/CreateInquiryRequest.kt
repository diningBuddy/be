package com.restaurant.be.inquiry.presentation.controller.dto

import com.restaurant.be.inquiry.domain.entity.InquiryCategory
import io.swagger.v3.oas.annotations.media.Schema

data class CreateInquiryRequest(
    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @Schema(description = "제목", example = "문의 제목")
    val title: String,

    @Schema(description = "내용", example = "문의 내용")
    val content: String,

    @Schema(description = "카테고리", example = "MEMBER", allowableValues = ["MEMBER", "PAYMENT_REFUND", "SERVICE"])
    val category: InquiryCategory
)
