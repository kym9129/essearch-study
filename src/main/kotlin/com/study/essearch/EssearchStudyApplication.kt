package com.study.essearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class EssearchStudyApplication

fun main(args: Array<String>) {
	runApplication<EssearchStudyApplication>(*args)
}
