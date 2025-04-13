package com.restaurant.be.home.presentation

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.home.domain.service.HomeService
import com.restaurant.be.home.presentation.dto.HomeRequest
import com.restaurant.be.home.presentation.dto.HomeResponse
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    @Operation(
        summary = "메인 페이지 API",
        parameters = [
            Parameter(
                name = "userLatitude",
                description = "사용자 위치 - 위도",
                required = true,
                example = "37.2977142440725"
            ),
            Parameter(
                name = "userLongitude",
                description = "사용자 위치 - 경도",
                required = true,
                example = "126.970140339367"
            )
        ]
    )
    fun getRecommendations(
        principal: Principal,
        @RequestParam userLatitude: Double,
        @RequestParam userLongitude: Double
    ): CommonResponse<HomeResponse> {
        val request = HomeRequest(
            userLatitude,
            userLongitude
        )
        val response = homeService.getHome(
            request,
            principal.name.toLong()
        )
        return CommonResponse.success(response)
    }

    @GetMapping("/section")  // URL 경로를 /section으로 변경하여 중복을 방지
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "메인 하단 상세 조회 API",
        parameters = [
            Parameter(
                name = "type",
                description = "요청 섹션 (LAUNCH, LATE_NIGHT)",
                required = true,
                example = "LAUNCH"
            ),
            Parameter(
                name = "page",
                description = "페이지 번호 (0부터 시작)",
                required = false,
                example = "0"
            ),
            Parameter(
                name = "userLatitude",
                description = "사용자 위치 - 위도",
                required = true,
                example = "37.2977142440725"
            ),
            Parameter(
                name = "userLongitude",
                description = "사용자 위치 - 경도",
                required = true,
                example = "126.970140339367"
            )
        ]
    )
    fun getSectionDetails(
        principal: Principal,
        @RequestParam type: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam userLatitude: Double,
        @RequestParam userLongitude: Double
    ): CommonResponse<GetRestaurantsResponse> {
        val response = homeService.getSectionDetails(
            type,
            principal.name.toLong(),
            page,
            userLongitude,
            userLatitude
        )
        return CommonResponse.success(response)
    }
}
