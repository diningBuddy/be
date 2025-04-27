package com.restaurant.be.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
class S3Properties {
    lateinit var region: String
    lateinit var bucket: String
}
