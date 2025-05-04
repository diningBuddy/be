package com.restaurant.be.autocomplete.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema

data class AutoCompleteResponse(
    @Schema(description = "접두사에 의한 연관 검색어")
    val suggestions: List<String>
)
