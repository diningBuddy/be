package com.restaurant.be.s3.util

import net.coobird.thumbnailator.Thumbnails
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ImageResizeUtil {

    fun resizeAndCompressWithFallback(file: MultipartFile, maxWidth: Int = 1280): ByteArray {
        val originalImage = ImageIO.read(file.inputStream)

        if (originalImage == null) {
            throw IllegalArgumentException("이미지를 읽을 수 없습니다.")
        }

        val width = originalImage.width
        val height = originalImage.height

        if (width <= maxWidth && height <= maxWidth) {
            return compressImage(file.bytes, width, height)
        }

        val resizedBytes = resizeImage(originalImage, maxWidth)
        val compressedBytes = compressImage(resizedBytes, maxWidth, maxWidth)

        return if (compressedBytes.size < resizedBytes.size) {
            compressedBytes
        } else {
            resizedBytes
        }
    }

    private fun resizeImage(image: BufferedImage, maxWidth: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(image)
            .size(maxWidth, maxWidth)
            .outputFormat("jpg")
            .toOutputStream(outputStream)
        return outputStream.toByteArray()
    }

    private fun compressImage(inputBytes: ByteArray, width: Int, height: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(ByteArrayInputStream(inputBytes))
            .size(width, height)
            .outputFormat("jpg")
            .outputQuality(0.8)
            .toOutputStream(outputStream)
        return outputStream.toByteArray()
    }
}
