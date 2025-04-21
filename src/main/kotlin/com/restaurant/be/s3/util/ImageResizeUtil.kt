package com.restaurant.be.s3.util

import net.coobird.thumbnailator.Thumbnails
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageResizeUtil {
    fun resizeImage(inputStream: InputStream, maxWidth: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(inputStream)
            .size(maxWidth, maxWidth)
            .outputFormat("jpg")
            .toOutputStream(outputStream)
        return outputStream.toByteArray()
    }

    fun resizeMultipartFile(file: MultipartFile, maxWidth: Int = 1280): ByteArray {
        return resizeImage(file.inputStream, maxWidth)
    }
}
