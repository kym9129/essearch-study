package com.study.essearch.entity

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "ad_products")
data class AdProduct(
    @Id
    val id: String? = null,
    
    @Field(type = FieldType.Text, analyzer = "standard")
    val name: String,
    
    @Field(type = FieldType.Keyword)
    val adType: String, // BANNER, VIDEO, TEXT 등
    
    @Field(type = FieldType.Long)
    val budget: Long, // 예산 (원)
    
    @Field(type = FieldType.Keyword)
    val status: String, // ACTIVE, INACTIVE, PAUSED
    
    @Field(type = FieldType.Text)
    val description: String? = null,
    
    @Field(type = FieldType.Date)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Field(type = FieldType.Date)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)