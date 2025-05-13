package com.restaurant.be.autocomplete.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteEsDocument(
    @SerialName("word") val word: String,
    @SerialName("chosung")val chosung: String,
    @SerialName("mapped_hangul")val mapped_hangul: String,
    @SerialName("frequency") val frequency: Int = 1,
    @SerialName("last_updated") val lastUpdated: Long = System.currentTimeMillis()
)
