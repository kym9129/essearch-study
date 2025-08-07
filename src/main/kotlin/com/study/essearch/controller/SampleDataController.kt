package com.study.essearch.controller

import com.study.essearch.service.SampleDataService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sample-data")
class SampleDataController(
    private val sampleDataService: SampleDataService
) {
    
    private val logger = LoggerFactory.getLogger(SampleDataController::class.java)
    
    @PostMapping("/generate")
    fun generateSampleData(): ResponseEntity<Map<String, String>> {
        return try {
            sampleDataService.generateAndIndexSampleData()
            ResponseEntity.ok(mapOf(
                "status" to "success",
                "message" to "샘플 데이터가 성공적으로 생성되고 색인되었습니다."
            ))
        } catch (e: Exception) {
            logger.error("샘플 데이터 생성 중 오류 발생", e)
            ResponseEntity.internalServerError().body(mapOf(
                "status" to "error",
                "message" to "샘플 데이터 생성 중 오류가 발생했습니다: ${e.message}"
            ))
        }
    }
    
    @DeleteMapping("/clear")
    fun clearAllData(): ResponseEntity<Map<String, String>> {
        return try {
            sampleDataService.clearAllData()
            ResponseEntity.ok(mapOf(
                "status" to "success",
                "message" to "모든 데이터가 성공적으로 삭제되었습니다."
            ))
        } catch (e: Exception) {
            logger.error("데이터 삭제 중 오류 발생", e)
            ResponseEntity.internalServerError().body(mapOf(
                "status" to "error",
                "message" to "데이터 삭제 중 오류가 발생했습니다: ${e.message}"
            ))
        }
    }
}