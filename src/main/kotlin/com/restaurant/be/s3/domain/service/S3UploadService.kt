package com.restaurant.be.s3.domain.service

import com.restaurant.be.common.config.S3Properties
import com.restaurant.be.s3.util.ImageResizeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.function.ServerResponse.async
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDate
import java.util.*

@Service
class S3UploadService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) {
    fun uploadImagesSync(files: List<MultipartFile>): List<String> {
        return files.map { uploadImage(it) }
    }

    suspend fun uploadImagesAsync(files: List<MultipartFile>): List<String> = coroutineScope {
        files.map { file ->
            async(Dispatchers.IO) {
                uploadImage(file)
            }
        }.awaitAll()
    }

    fun uploadImage(file: MultipartFile): String {
        val resizedImageBytes = ImageResizeUtil.resizeAndCompressWithFallback(file, 1280)
        val uuid = UUID.randomUUID().toString()
        val path = "images/${LocalDate.now()}/$uuid.jpg"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(path)
            .contentType("image/jpeg")
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(resizedImageBytes))

        return "https://${s3Properties.bucket}.s3.${s3Properties.region}.amazonaws.com/$path"
    }
}
