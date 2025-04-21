package com.restaurant.be.s3.presentation

import com.restaurant.be.s3.domain.service.S3UploadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1")
class ImageUploadController(
    private val uploadService: S3UploadService
) {

    @Operation(
        summary = "다중 이미지 업로드",
        description = "이미지 파일을 여러 개 업로드하고, 업로드된 이미지의 URL 리스트를 반환합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "성공", content = [
                Content(array = ArraySchema(schema = Schema(implementation = String::class)))
            ]),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (파일이 없음)"),
            ApiResponse(responseCode = "500", description = "서버 에러")
        ]
    )
    @PostMapping("/upload-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImages(
        @RequestPart files: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        if (files.isEmpty()) return ResponseEntity.badRequest().build()
        val urls = files.map { uploadService.uploadImage(it) }
        return ResponseEntity.ok(urls)
    }
}
