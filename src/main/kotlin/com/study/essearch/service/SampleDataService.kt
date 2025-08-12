package com.study.essearch.service

import com.study.essearch.dto.AdStatisticsMessage
import com.study.essearch.entity.AdProduct
import com.study.essearch.entity.AdStatistics
import com.study.essearch.repository.AdProductRepository
import com.study.essearch.repository.AdStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class SampleDataService(
    private val adProductRepository: AdProductRepository,
    private val adStatisticsRepository: AdStatisticsRepository,
    private val elasticsearchOperations: ElasticsearchOperations
) {
    
    private val logger = LoggerFactory.getLogger(SampleDataService::class.java)
    
    /**
     * 카프카 메시지로부터 AdStatistics 생성 및 저장
     */
    fun processAdStatisticsFromKafka(message: AdStatisticsMessage): AdStatistics {
        logger.debug("카프카 메시지 처리 중: 광고상품ID ${message.adProductId}")
        
        val adStatistics = createAdStatisticsFromMessage(message)
        val savedEntity = adStatisticsRepository.save(adStatistics)
        
        logger.info("카프카 메시지로부터 광고 통계 저장 완료: ID=${savedEntity.id}, 광고상품ID=${message.adProductId}")
        return savedEntity
    }
    
    /**
     * 여러 메시지를 배치로 처리
     */
    fun processBatchAdStatisticsFromKafka(messages: List<AdStatisticsMessage>): List<AdStatistics> {
        logger.info("카프카 배치 메시지 처리 중: ${messages.size}개")
        
        val adStatisticsList = messages.map { createAdStatisticsFromMessage(it) }
        val savedEntities = adStatisticsRepository.saveAll(adStatisticsList)
        
        logger.info("카프카 배치 메시지로부터 광고 통계 저장 완료: ${savedEntities.count()}개")
        return savedEntities.toList()
    }
    
    /**
     * AdStatisticsMessage에서 AdStatistics 엔티티 생성
     */
    private fun createAdStatisticsFromMessage(message: AdStatisticsMessage): AdStatistics {
        val metrics = AdStatistics.calculateMetrics(
            message.impressions,
            message.clicks,
            message.conversions,
            message.cost
        )
        
        return AdStatistics(
            adProductId = message.adProductId,
            date = message.date,
            impressions = message.impressions,
            clicks = message.clicks,
            conversions = message.conversions,
            cost = message.cost,
            ctr = metrics["ctr"]!!,
            cvr = metrics["cvr"]!!,
            cpc = metrics["cpc"]!!,
            cpa = metrics["cpa"]!!
        )
    }
    
    fun generateAndIndexSampleData() {
        logger.info("샘플 데이터 생성 시작...")
        
        // 1. 광고 상품 데이터 생성 및 색인
        val adProducts = generateAdProducts()
        logger.info("광고 상품 ${adProducts.size}개 생성 완료")
        indexAdProductsBulk(adProducts)
        logger.info("광고 상품 일괄 색인 완료")
        
        // 2. 통계 데이터 생성 및 색인
        val statistics = generateAdStatistics(adProducts)
        logger.info("통계 데이터 ${statistics.size}개 생성 완료")
        indexStatisticsBulk(statistics)
        logger.info("통계 데이터 일괄 색인 완료")
        
        logger.info("샘플 데이터 생성 및 색인 완료!")
    }
    
    private fun generateAdProducts(): List<AdProduct> {
        val sampleData = SampleDataConstants
        
        return (1..50).map { index ->
            val companyName = sampleData.COMPANY_NAMES.random()
            val productType = sampleData.PRODUCT_TYPES.random()
            val adType = sampleData.AD_TYPES.random()
            
            AdProduct(
                id = "ad-product-$index",
                name = "${companyName}_${productType}_${adType}_캠페인_$index",
                adType = adType,
                budget = Random.nextLong(100_000, 10_000_000),
                status = sampleData.STATUSES.random(),
                description = "${companyName}의 ${productType}을 위한 ${adType} 광고 캠페인입니다. 타겟 고객층에게 효과적으로 노출하여 높은 전환율을 목표로 합니다.",
                createdAt = LocalDateTime.now().minusDays(Random.nextLong(1, 90)),
                updatedAt = LocalDateTime.now().minusDays(Random.nextLong(0, 30))
            )
        }
    }
    
    private fun generateAdStatistics(adProducts: List<AdProduct>): List<AdStatistics> {
        val statistics = mutableListOf<AdStatistics>()
        val startDate = LocalDate.now().minusDays(30)
        val endDate = LocalDate.now()
        
        adProducts.forEach { adProduct ->
            statistics.addAll(generateStatisticsForProduct(adProduct, startDate, endDate))
        }
        
        return statistics
    }
    
    /**
     * 특정 광고 상품에 대한 기간별 통계 생성
     */
    private fun generateStatisticsForProduct(
        adProduct: AdProduct, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<AdStatistics> {
        val statistics = mutableListOf<AdStatistics>()
        var currentDate = startDate
        var statisticIndex = 1
        
        while (currentDate <= endDate) {
            // ACTIVE 상태가 아닌 광고는 낮은 확률로만 데이터 생성
            if (shouldSkipDataGeneration(adProduct.status)) {
                currentDate = currentDate.plusDays(1)
                continue
            }
            
            val statistic = createStatisticForDate(adProduct, currentDate, statisticIndex)
            statistics.add(statistic)
            
            currentDate = currentDate.plusDays(1)
            statisticIndex++
        }
        
        return statistics
    }
    
    /**
     * 데이터 생성을 스킵할지 결정
     */
    private fun shouldSkipDataGeneration(status: String): Boolean {
        return status != "ACTIVE" && Random.nextDouble() > 0.3
    }
    
    /**
     * 특정 날짜에 대한 통계 데이터 생성
     */
    private fun createStatisticForDate(
        adProduct: AdProduct, 
        date: LocalDate, 
        index: Int
    ): AdStatistics {
        val (impressions, clicks, cost) = generateBaseMetrics(adProduct.adType)
        val conversions = Random.nextLong(0, clicks / 10 + 1)
        val calculatedMetrics = AdStatistics.calculateMetrics(impressions, clicks, conversions, cost)
        
        return AdStatistics(
            id = "${adProduct.id}_${date}_${index}",
            adProductId = adProduct.id!!,
            date = date,
            impressions = impressions,
            clicks = clicks,
            conversions = conversions,
            ctr = String.format("%.2f", calculatedMetrics["ctr"]).toDouble(),
            cvr = String.format("%.2f", calculatedMetrics["cvr"]).toDouble(),
            cost = cost,
            cpc = String.format("%.0f", calculatedMetrics["cpc"]).toDouble(),
            cpa = String.format("%.0f", calculatedMetrics["cpa"]).toDouble()
        )
    }
    
    /**
     * 광고 타입별 기본 메트릭 생성
     */
    private fun generateBaseMetrics(adType: String): Triple<Long, Long, Long> {
        val (baseImpressions, baseClicks, baseCost) = when (adType) {
            "VIDEO" -> Triple(
                Random.nextLong(5_000, 50_000),
                Random.nextLong(100, 1_500),
                Random.nextLong(100_000, 1_000_000)
            )
            "BANNER" -> Triple(
                Random.nextLong(10_000, 100_000),
                Random.nextLong(50, 800),
                Random.nextLong(50_000, 500_000)
            )
            "SEARCH" -> Triple(
                Random.nextLong(1_000, 20_000),
                Random.nextLong(50, 1_000),
                Random.nextLong(80_000, 800_000)
            )
            "NATIVE" -> Triple(
                Random.nextLong(3_000, 30_000),
                Random.nextLong(80, 1_200),
                Random.nextLong(60_000, 600_000)
            )
            else -> Triple( // TEXT
                Random.nextLong(8_000, 80_000),
                Random.nextLong(40, 600),
                Random.nextLong(40_000, 400_000)
            )
        }
        
        val impressions = baseImpressions
        val clicks = minOf(baseClicks, impressions) // 클릭수는 노출수보다 작아야 함
        
        return Triple(impressions, clicks, baseCost)
    }
    
    private fun indexAdProductsBulk(adProducts: List<AdProduct>) {
        performBulkIndex(adProducts, "ad_products") { 
            "광고 상품 일괄 색인 실패" 
        }
    }
    
    private fun indexStatisticsBulk(statistics: List<AdStatistics>) {
        performBulkIndex(statistics, "ad_statistics") { 
            "통계 데이터 일괄 색인 실패" 
        }
    }
    
    /**
     * 공통 Bulk 색인 로직
     */
    private fun <T> performBulkIndex(
        data: List<T>, 
        indexName: String, 
        errorMessageProvider: () -> String
    ) {
        try {
            // 대량 데이터를 배치로 나누어서 처리
            data.chunked(1000).forEach { batch ->
                elasticsearchOperations.save(batch, IndexCoordinates.of(indexName))
            }
        } catch (e: Exception) {
            logger.error(errorMessageProvider(), e)
            throw e
        }
    }
    
    fun clearAllData() {
        logger.info("모든 데이터 삭제 시작...")
        adProductRepository.deleteAll()
        adStatisticsRepository.deleteAll()
        logger.info("모든 데이터 삭제 완료")
    }
    
    /**
     * 상수들을 별도 객체로 분리
     */
    private object SampleDataConstants {
        val AD_TYPES = listOf("BANNER", "VIDEO", "TEXT", "NATIVE", "SEARCH")
        val STATUSES = listOf("ACTIVE", "INACTIVE", "PAUSED")
        val COMPANY_NAMES = listOf(
            "테크스타트업", "이커머스", "게임회사", "금융서비스", "교육플랫폼",
            "푸드딜리버리", "여행서비스", "뷰티브랜드", "패션몰", "헬스케어"
        )
        val PRODUCT_TYPES = listOf(
            "앱 설치", "웹사이트 방문", "상품 구매", "회원가입", "브랜딩",
            "리드 생성", "동영상 시청", "이벤트 참여", "쿠폰 다운로드", "뉴스레터 구독"
        )
    }
}