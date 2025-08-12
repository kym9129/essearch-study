package com.study.essearch.consumer

import com.study.essearch.dto.AdStatisticsMessage
import com.study.essearch.service.SampleDataService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class AdStatisticsConsumer(
    private val sampleDataService: SampleDataService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["ad-statistics-aggregated"],
        groupId = "ad-statistics-consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeAdStatistics(
        @Payload message: AdStatisticsMessage,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        try {
            log.info("카프카 메시지 수신: 토픽=$topic, 파티션=$partition, 오프셋=$offset")
            log.debug("메시지 내용: $message")

            // SampleDataService에서 실제 처리 로직 수행
            val savedEntity = sampleDataService.processAdStatisticsFromKafka(message)
            log.info("광고 통계 처리 및 저장 성공: ${savedEntity.id}")

            // 메시지 처리 완료 확인
            acknowledgment.acknowledge()

        } catch (e: Exception) {
            log.error("광고 통계 메시지 처리 실패: $message", e)
            // 필요에 따라 DLQ(Dead Letter Queue) 처리 또는 재시도 로직 추가
            throw e
        }
    }

    /**
     * 배치 처리를 위한 리스너 (선택적)
     */
    @KafkaListener(
        topics = ["ad-statistics-batch"],
        groupId = "ad-statistics-batch-consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeAdStatisticsBatch(
        @Payload messages: List<AdStatisticsMessage>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        acknowledgment: Acknowledgment
    ) {
        try {
            log.info("배치 메시지 수신: ${messages.size}개, 토픽=$topic")

            // SampleDataService에서 배치 처리
            val savedEntities = sampleDataService.processBatchAdStatisticsFromKafka(messages)
            log.info("배치 광고 통계 처리 및 저장 성공: ${savedEntities.size}개")

            // 메시지 처리 완료 확인
            acknowledgment.acknowledge()

        } catch (e: Exception) {
            log.error("배치 광고 통계 메시지 처리 실패", e)
            throw e
        }
    }
}