package com.study.essearch.service

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
    
    fun generateAndIndexSampleData() {
        logger.info("샘플 데이터 생성 시작...")
        
        // 1. 광고 상품 데이터 생성
        val adProducts = generateAdProducts()
        logger.info("광고 상품 ${adProducts.size}개 생성 완료")
        
        // 2. Bulk로 광고 상품 색인
        indexAdProductsBulk(adProducts)
        logger.info("광고 상품 Bulk 색인 완료")
        
        // 3. 통계 데이터 생성
        val statistics = generateAdStatistics(adProducts)
        logger.info("통계 데이터 ${statistics.size}개 생성 완료")
        
        // 4. Bulk로 통계 데이터 색인
        indexStatisticsBulk(statistics)
        logger.info("통계 데이터 Bulk 색인 완료")
        
        logger.info("샘플 데이터 생성 및 색인 완료!")
    }
    
    private fun generateAdProducts(): List<AdProduct> {
        val adTypes = listOf("BANNER", "VIDEO", "TEXT", "NATIVE", "SEARCH")
        val statuses = listOf("ACTIVE", "INACTIVE", "PAUSED")
        val companyNames = listOf("테크스타트업", "이커머스", "게임회사", "금융서비스", "교육플랫폼", 
                                  "푸드딜리버리", "여행서비스", "뷰티브랜드", "패션몰", "헬스케어")
        val productTypes = listOf("앱 설치", "웹사이트 방문", "상품 구매", "회원가입", "브랜딩", 
                                  "리드 생성", "동영상 시청", "이벤트 참여", "쿠폰 다운로드", "뉴스레터 구독")
        
        return (1..50).map { index ->
            val companyName = companyNames.random()
            val productType = productTypes.random()
            val adType = adTypes.random()
            
            AdProduct(
                id = "ad-product-$index",
                name = "${companyName}_${productType}_${adType}_캠페인_$index",
                adType = adType,
                budget = Random.nextLong(100_000, 10_000_000), // 10만원 ~ 1천만원
                status = statuses.random(),
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
            // 각 광고 상품에 대해 최근 30일간의 통계 데이터 생성
            var currentDate = startDate
            var statisticIndex = 1
            
            while (currentDate <= endDate) {
                // ACTIVE 상태가 아닌 광고는 낮은 확률로만 데이터 생성
                if (adProduct.status != "ACTIVE" && Random.nextDouble() > 0.3) {
                    currentDate = currentDate.plusDays(1)
                    continue
                }
                
                // 기본 통계값 생성 (광고 타입별로 다른 범위)
                val (baseImpressions, baseClicks, baseCost) = when (adProduct.adType) {
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
                val conversions = Random.nextLong(0, clicks / 10 + 1) // 전환수는 클릭수의 10% 이하
                val cost = baseCost
                
                // 계산된 지표들
                val metrics = AdStatistics.calculateMetrics(impressions, clicks, conversions, cost)
                
                statistics.add(
                    AdStatistics(
                        id = "${adProduct.id}_${currentDate}_${statisticIndex}",
                        adProductId = adProduct.id!!,
                        date = currentDate,
                        impressions = impressions,
                        clicks = clicks,
                        conversions = conversions,
                        ctr = String.format("%.2f", metrics["ctr"]).toDouble(),
                        cvr = String.format("%.2f", metrics["cvr"]).toDouble(),
                        cost = cost,
                        cpc = String.format("%.0f", metrics["cpc"]).toDouble(),
                        cpa = String.format("%.0f", metrics["cpa"]).toDouble()
                    )
                )
                
                currentDate = currentDate.plusDays(1)
                statisticIndex++
            }
        }
        
        return statistics
    }
    
    private fun indexAdProductsBulk(adProducts: List<AdProduct>) {
        try {
            elasticsearchOperations.save(adProducts, IndexCoordinates.of("ad_products"))
        } catch (e: Exception) {
            logger.error("광고 상품 Bulk 색인 실패", e)
            throw e
        }
    }
    
    private fun indexStatisticsBulk(statistics: List<AdStatistics>) {
        try {
            // 대량 데이터를 배치로 나누어서 처리
            statistics.chunked(1000).forEach { batch ->
                elasticsearchOperations.save(batch, IndexCoordinates.of("ad_statistics"))
            }
        } catch (e: Exception) {
            logger.error("통계 데이터 Bulk 색인 실패", e)
            throw e
        }
    }
    
    fun clearAllData() {
        logger.info("모든 데이터 삭제 시작...")
        adProductRepository.deleteAll()
        adStatisticsRepository.deleteAll()
        logger.info("모든 데이터 삭제 완료")
    }
}