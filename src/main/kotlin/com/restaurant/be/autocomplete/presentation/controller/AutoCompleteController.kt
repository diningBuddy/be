package com.restaurant.be.autocomplete.presentation.controller

import com.restaurant.be.autocomplete.presentation.dto.AutoCompleteResponse
import com.restaurant.be.autocomplete.service.AutoCompleteService
import com.restaurant.be.common.response.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "Auto-Complete", description = "자동완성 서비스")
@RestController
@RequestMapping("/v1/autocomplete")
class AutoCompleteController(
    private val autoCompleteService: AutoCompleteService
) {
    private val MIN_INPUT_LENGTH = 2

    @GetMapping
    @Operation(summary = "자동완성 API", description = "입력된 접두사에 기반한 자동완성 제안을 제공합니다.")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = AutoCompleteResponse::class))]
    )
    fun getAutoCompleteSuggestions(
        @RequestParam prefix: String,
        principal: Principal?
    ): CommonResponse<AutoCompleteResponse> {
        if (prefix.length < MIN_INPUT_LENGTH) {
            return CommonResponse.success(AutoCompleteResponse(emptyList()))
        }

        val userId = principal?.name?.toLongOrNull()
        val suggestions = autoCompleteService.getSuggestions(prefix, userId)



        return CommonResponse.success(AutoCompleteResponse(suggestions))
    }
}
