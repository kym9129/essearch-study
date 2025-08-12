package com.study.essearch.controller

import com.study.essearch.service.HealthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController(
    private val healthService: HealthService
) {

    @GetMapping
    fun getHealth(): ResponseEntity<Map<String, Any>> {
        val health = healthService.getSystemHealth()
        return ResponseEntity.ok(health)
    }
}