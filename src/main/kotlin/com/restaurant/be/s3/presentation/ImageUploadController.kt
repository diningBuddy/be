package com.restaurant.be.s3.presentation

import com.restaurant.be.s3.domain.service.S3UploadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/v1")
class ImageUploadController(
    private val uploadService: S3UploadService
) {

    @Operation(
        summary = "다중 이미지 업로드 동기",
        description = "이미지 파일을 여러 개 업로드하고, 업로드된 이미지의 URL 리스트를 반환합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [
                    Content(array = ArraySchema(schema = Schema(implementation = String::class)))
                ]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (파일이 없음)"),
            ApiResponse(responseCode = "500", description = "서버 에러")
        ]
    )
    @PostMapping("/upload-image/sync", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun uploadImagesSync(
        principal: Principal,
        @RequestPart files: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        var urls: List<String>
        val time = measureTimeMillis {
            urls = uploadService.uploadImagesSync(files)
        }
        println("🧱 동기 업로드 소요 시간: ${time}ms")
        return ResponseEntity.ok(urls)
    }

    @Operation(
        summary = "다중 이미지 업로드 비동기",
        description = "이미지 파일을 여러 개 업로드하고, 업로드된 이미지의 URL 리스트를 반환합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [
                    Content(array = ArraySchema(schema = Schema(implementation = String::class)))
                ]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (파일이 없음)"),
            ApiResponse(responseCode = "500", description = "서버 에러")
        ]
    )
    @PostMapping("/upload-image/async", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    suspend fun uploadImagesAsync(
        principal: Principal,
        @RequestPart files: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        var urls: List<String>
        val time = measureTimeMillis {
            urls = runBlocking {
                uploadService.uploadImagesAsync(files)
            }
        }
        println("⚡ 비동기(코루틴) 업로드 소요 시간: ${time}ms")
        return ResponseEntity.ok(urls)
    }
}
