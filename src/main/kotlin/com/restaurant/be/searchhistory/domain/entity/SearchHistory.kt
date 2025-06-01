package com.restaurant.be.searchhistory.domain.entity

import com.restaurant.be.common.entity.BaseEntity
import com.restaurant.be.common.exception.NotFoundRecentSearchException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class SearchHistory(
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var keyword: String,

    @Column
    var count: Long = 1,

    ) : BaseEntity() {
    companion object {
        fun create(
            keyword: String,
        ): SearchHistory =
            SearchHistory(
                keyword = keyword
            )
    }

    fun getId(): Long {
        return id ?: throw NotFoundRecentSearchException()
    }
}
