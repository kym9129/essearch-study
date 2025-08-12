package com.study.essearch.service

import com.study.essearch.dto.AdStatisticsMessage
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.random.Random

@Service
class KafKaTestDataProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val singleTopicName = "ad-statistics-aggregated"
    private val batchTopicName = "ad-statistics-batch"

    fun sendTestAdStatistics(count: Int = 10) {
        repeat(count) { i ->
            val message = createTestMessage()

            kafkaTemplate.send(singleTopicName, message.adProductId, message)
                .thenAccept { result ->
                    log.info("개별 메시지 전송 성공 ${i + 1}: ${result.recordMetadata}")
                }
                .exceptionally { ex ->
                    log.error("개별 메시지 전송 실패 ${i + 1}", ex)
                    null
                }
        }

        log.info("테스트 메시지 전송 완료: 토픽=$singleTopicName, 개수=$count")
    }

    fun sendTestAdStatisticsBatch(batchSize: Int = 5, batchCount: Int = 2) {
        repeat(batchCount) { batchIndex ->
            val messages = (1..batchSize).map { createTestMessage() }

            kafkaTemplate.send(batchTopicName, "batch-${batchIndex}", messages)
                .thenAccept { result ->
                    log.info("배치 전송 성공 ${batchIndex + 1} (${messages.size}개 메시지): ${result.recordMetadata}")
                }
                .exceptionally { ex ->
                    log.error("배치 전송 실패 ${batchIndex + 1}", ex)
                    null
                }
        }

        log.info("테스트 배치 메시지 전송 완료: 토픽=$batchTopicName, 배치개수=$batchCount, 배치크기=$batchSize")
    }

    private fun createTestMessage(): AdStatisticsMessage {
        return AdStatisticsMessage(
            adProductId = "ad-product-${Random.Default.nextInt(1, 101)}",
            date = LocalDate.now().minusDays(Random.Default.nextLong(0, 30)),
            impressions = Random.Default.nextLong(1000, 100000),
            clicks = Random.Default.nextLong(10, 1000),
            conversions = Random.Default.nextLong(1, 50),
            cost = Random.Default.nextLong(10000, 1000000)
        )
    }
}