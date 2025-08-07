package com.study.essearch.service

import com.study.essearch.repository.AdProductRepository
import org.springframework.stereotype.Service

@Service
class AdProductService(
    private val adProductRepository: AdProductRepository
) {
}