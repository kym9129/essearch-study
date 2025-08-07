package com.study.essearch.controller

import com.study.essearch.service.AdProductService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ads")
class SampleAdController(
    private val adProductService: AdProductService,
) {

    @GetMapping("/test")
    fun test(): String {
        return "hello";
    }

}