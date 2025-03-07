package com.restaurant.be.hello.presentation.controller

import com.restaurant.be.common.response.CommonResponse
import com.restaurant.be.hello.domain.service.HelloService
import com.restaurant.be.hello.presentation.dto.HelloRequest
import com.restaurant.be.hello.presentation.dto.HelloResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val helloService: HelloService
) {
    @GetMapping("/hello")
    @Operation(summary = "Hello API")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    fun hello(): CommonResponse<HelloResponse> {
        val response = helloService.findHellos()
        return CommonResponse.success(response)
    }

    @PostMapping("/hello")
    @Operation(summary = "Hello API")
    @ApiResponse(
        responseCode = "200",
        description = "성공"
    )
    fun hello(
        @RequestBody request: HelloRequest
    ): CommonResponse<HelloResponse> {
        val response = helloService.saveHello(request)
        return CommonResponse.success(response)
    }
}
