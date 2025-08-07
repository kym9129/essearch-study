package com.study.essearch.repository

import com.study.essearch.entity.AdStatistics
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AdStatisticsRepository : ElasticsearchRepository<AdStatistics, String> {
    
    fun findByAdProductId(adProductId: String): List<AdStatistics>
    
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<AdStatistics>
    
    fun findByAdProductIdAndDateBetween(
        adProductId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AdStatistics>
}