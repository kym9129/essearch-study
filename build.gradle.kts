plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.test"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Web MVC 스타터
	implementation("org.springframework.boot:spring-boot-starter-web")
	
	// Elasticsearch 스타터
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	// Kafka 스타터
	implementation("org.springframework.kafka:spring-kafka")

	// Jackson Kotlin 모듈 (JSON 처리)
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	
	// Kotlin 리플렉션
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	
	// 테스트 의존성
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}