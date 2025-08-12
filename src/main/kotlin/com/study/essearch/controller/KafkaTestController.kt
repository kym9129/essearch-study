package com.study.essearch.controller

import com.study.essearch.service.KafKaTestDataProducerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class KafkaTestController(
    private val kafKaTestDataProducerService: KafKaTestDataProducerService
) {

    @PostMapping("/kafka/send-messages")
    fun sendTestMessages(@RequestParam(defaultValue = "10") count: Int): ResponseEntity<String> {
        kafKaTestDataProducerService.sendTestAdStatistics(count)
        return ResponseEntity.ok("카프카로 ${count}개 테스트 메시지 전송 완료")
    }

    @PostMapping("/kafka/send-batch-messages")
    fun sendBatchTestMessages(
        @RequestParam(defaultValue = "5") batchSize: Int,
        @RequestParam(defaultValue = "2") batchCount: Int
    ): ResponseEntity<String> {
        kafKaTestDataProducerService.sendTestAdStatisticsBatch(batchSize, batchCount)
        return ResponseEntity.ok("카프카로 ${batchCount}개 배치 (배치당 ${batchSize}개 메시지) 전송 완료")
    }
}