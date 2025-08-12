package com.study.essearch.service

import com.study.essearch.repository.AdStatisticsRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class HealthService(
    private val adStatisticsRepository: AdStatisticsRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun getSystemHealth(): Map<String, Any> {
        return mapOf(
            "elasticsearch" to getElasticsearchHealth(),
            "kafka" to getKafkaHealth(),
            "totalAdStatistics" to adStatisticsRepository.count()
        )
    }

    private fun getElasticsearchHealth(): Map<String, Any> {
        return try {
            val count = adStatisticsRepository.count()
            mapOf("status" to "UP", "documentCount" to count)
        } catch (e: Exception) {
            mapOf("status" to "DOWN", "error" to e.message)
        } as Map<String, Any>
    }

    private fun getKafkaHealth(): Map<String, Any> {
        return try {
            // 간단한 Kafka 연결 테스트
            kafkaTemplate.partitionsFor("ad-statistics-aggregated")
            mapOf("status" to "UP")
        } catch (e: Exception) {
            mapOf("status" to "DOWN", "error" to e.message)
        } as Map<String, Any>
    }
}