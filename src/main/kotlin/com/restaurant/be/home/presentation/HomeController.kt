package com.restaurant.be.home.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.home.domain.service.HomeService
import com.restaurant.be.home.presentation.dto.HomeRequest
import com.restaurant.be.home.presentation.dto.HomeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Tag(name = "06. Home Info", description = "홈 서비스")
@RestController
@RequestMapping("/v1/home")
class HomeController(
    private val homeService: HomeService
) {

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "메인 페이지 API")
    @ApiResponse(
        responseCode = "200",
        description = "성공",
        content = [Content(schema = Schema(implementation = HomeResponse::class))]
    )
    fun getRecommendations(
        principal: Principal,
        @ModelAttribute request: HomeRequest
    ): CommonResponse<HomeResponse> {
        val response = homeService.getHome(
            request,
            principal.name.toLong()
        )
        return CommonResponse.success(response)
    }
    /*
    * 큰 페이지들이 나올 수 있도록하자
    * */
    // TODO 랭킹 페이지 나오도록 하는 API ???
    // TODO 홈 하단 섹션 자세히 나오도록하는 API // 처음 20개, 이후 10개씩
}
