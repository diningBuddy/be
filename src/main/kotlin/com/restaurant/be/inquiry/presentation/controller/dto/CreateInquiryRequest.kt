package com.restaurant.be.inquiry.presentation.controller.dto

import com.restaurant.be.inquiry.domain.entity.InquiryCategory
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

import com.fasterxml.jackson.annotation.JsonIgnore

data class CreateInquiryRequest(
    @Schema(description = "이메일", example = "user@example.com")
    var email: String,

    @Schema(description = "제목", example = "문의 제목")
    var title: String,

    @Schema(description = "내용", example = "문의 내용")
    var content: String,

    @Schema(description = "카테고리", example = "MEMBER")
    var category: InquiryCategory,

    @Schema(description = "이미지 파일", type = "string", format = "binary", required = false)
    @JsonIgnore
    var image: MultipartFile?
)