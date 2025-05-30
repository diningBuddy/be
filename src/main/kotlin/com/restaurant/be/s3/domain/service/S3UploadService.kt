package com.restaurant.be.s3.domain.service

import com.restaurant.be.common.config.S3Properties
import com.restaurant.be.s3.domain.UploadPath
import com.restaurant.be.s3.util.ImageResizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDate
import java.util.UUID

@Service
class S3UploadService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) {
    fun uploadImagesAsync(path: UploadPath, files: List<MultipartFile>): List<String> {
        return runBlocking {
            files.map { file ->
                async(Dispatchers.IO) {
                    val resizedImageBytes = ImageResizeUtil.resizeAndCompressWithFallback(file, 1280)
                    val uuid = UUID.randomUUID().toString()
                    val fullPath = "$path/${LocalDate.now()}/$uuid.jpg"

                    val putObjectRequest = PutObjectRequest.builder()
                        .bucket(s3Properties.bucket)
                        .key(fullPath)
                        .contentType("image/jpeg")
                        .build()

                    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(resizedImageBytes))

                    "https://${s3Properties.bucket}.s3.${s3Properties.region}.amazonaws.com/$fullPath"
                }
            }.awaitAll()
        }
    }
}
