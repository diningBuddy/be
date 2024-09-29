package com.restaurant.be.common.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class LifeEntity {
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()
    var deletedAt: LocalDateTime = LocalDateTime.now()
}
