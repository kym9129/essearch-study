package com.study.essearch.repository

import com.study.essearch.entity.AdProduct
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface AdProductRepository : ElasticsearchRepository<AdProduct, String> {
    
    fun findByStatus(status: String): List<AdProduct>
    
    fun findByAdType(adType: String): List<AdProduct>
    
    fun findByNameContaining(name: String): List<AdProduct>
    
    fun findByBudgetBetween(minBudget: Long, maxBudget: Long): List<AdProduct>
}