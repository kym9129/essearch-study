package com.study.essearch.entity

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate

@Document(indexName = "ad_statistics")
data class AdStatistics(
    @Id
    val id: String? = null,
    
    @Field(type = FieldType.Keyword)
    val adProductId: String, // 광고 상품 ID
    
    @Field(type = FieldType.Date)
    val date: LocalDate,
    
    @Field(type = FieldType.Long)
    val impressions: Long, // 노출수
    
    @Field(type = FieldType.Long)
    val clicks: Long, // 클릭수
    
    @Field(type = FieldType.Long)
    val conversions: Long, // 전환수
    
    @Field(type = FieldType.Double)
    val ctr: Double, // 클릭률 (Click Through Rate)
    
    @Field(type = FieldType.Double)
    val cvr: Double, // 전환률 (Conversion Rate)
    
    @Field(type = FieldType.Long)
    val cost: Long, // 광고 비용 (원)
    
    @Field(type = FieldType.Double)
    val cpc: Double, // 클릭당 비용 (Cost Per Click)
    
    @Field(type = FieldType.Double)
    val cpa: Double // 전환당 비용 (Cost Per Acquisition)
) {
    companion object {
        fun calculateMetrics(impressions: Long, clicks: Long, conversions: Long, cost: Long): Map<String, Double> {
            val ctr = if (impressions > 0) (clicks.toDouble() / impressions * 100) else 0.0
            val cvr = if (clicks > 0) (conversions.toDouble() / clicks * 100) else 0.0
            val cpc = if (clicks > 0) (cost.toDouble() / clicks) else 0.0
            val cpa = if (conversions > 0) (cost.toDouble() / conversions) else 0.0
            
            return mapOf(
                "ctr" to ctr,
                "cvr" to cvr,
                "cpc" to cpc,
                "cpa" to cpa
            )
        }
    }
}