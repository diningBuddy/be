package com.restaurant.be.inquiry.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.inquiry.domain.service.InquiryService
import com.restaurant.be.inquiry.presentation.controller.dto.CreateInquiryRequest
import com.restaurant.be.inquiry.presentation.controller.dto.CreateInquiryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "07. Inquiry", description = "문의 서비스")
@RestController
@RequestMapping("/v1/inquiries")
class InquiryController(
    private val inquiryService: InquiryService
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "문의하기 API",
        description = "문의 정보와 함께 이미지 파일을 업로드할 수 있습니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = CreateInquiryResponse::class))]
    )
    fun createInquiry(
        @ModelAttribute request: CreateInquiryRequest
    ): CommonResponse<CreateInquiryResponse> {
        val response = inquiryService.createInquiry(request, request.image)
        return CommonResponse.success(response)
    }
}
