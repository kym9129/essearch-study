package com.study.essearch.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class AdStatisticsMessage(
    @JsonProperty("ad_product_id")
    val adProductId: String,
    
    @JsonProperty("date")
    val date: LocalDate,
    
    @JsonProperty("impressions")
    val impressions: Long,
    
    @JsonProperty("clicks") 
    val clicks: Long,
    
    @JsonProperty("conversions")
    val conversions: Long,
    
    @JsonProperty("cost")
    val cost: Long,
    
    @JsonProperty("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)