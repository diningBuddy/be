package com.restaurant.be.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    private val s3Properties: S3Properties
) {
    @Value("\${AWS_ACCESS_S3_KEY}")
    lateinit var accessKey: String

    @Value("\${AWS_SECRET_S3_KEY}")
    lateinit var secretKey: String

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(s3Properties.region))
            .credentialsProvider {
                AwsBasicCredentials.create(accessKey, secretKey)
            }
            .build()
    }
}
