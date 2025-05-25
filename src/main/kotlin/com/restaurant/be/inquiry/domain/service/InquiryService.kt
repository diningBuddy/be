package com.restaurant.be.inquiry.domain.service

import com.restaurant.be.inquiry.domain.entity.Inquiry
import com.restaurant.be.inquiry.presentation.controller.dto.CreateInquiryRequest
import com.restaurant.be.inquiry.presentation.controller.dto.CreateInquiryResponse
import com.restaurant.be.inquiry.repository.InquiryRepository
import com.restaurant.be.s3.domain.UploadPath
import com.restaurant.be.s3.domain.service.S3UploadService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class InquiryService(
    private val inquiryRepository: InquiryRepository,
    private val s3UploadService: S3UploadService
) {
    @Transactional
    fun createInquiry(request: CreateInquiryRequest, image: MultipartFile?): CreateInquiryResponse {
        var imageUrl: String? = null

        if (image != null && !image.isEmpty) {
            val uploadedUrls = s3UploadService.uploadImagesAsync(UploadPath.EDIT_INQUIRE, listOf(image))
            if (uploadedUrls.isNotEmpty()) {
                imageUrl = uploadedUrls.first()
            }
        }

        val inquiry = Inquiry(
            email = request.email,
            title = request.title,
            content = request.content,
            category = request.category,
            imageUrl = imageUrl
        )

        val savedInquiry = inquiryRepository.save(inquiry)

        return CreateInquiryResponse(
            id = savedInquiry.id!!,
            email = savedInquiry.email,
            title = savedInquiry.title,
            content = savedInquiry.content,
            category = savedInquiry.category,
            imageUrl = savedInquiry.imageUrl,
            createdAt = savedInquiry.createdAt
        )
    }
}
