# 과제 2: 샘플 데이터 색인 및 기본 조회 - 3개 과제 (3명 공통)
## 📌 전체 과제 개요
광고 플랫폼의 데이터를 OpenSearch에 색인하고 기본 CRUD 기능을 구현합니다. **3명 모두 동일한 3개 과제를 각자 구현**합니다.
## 📝 **과제 1: 광고 상품(Ad Product) 데이터 관리**
### 🎯 목표
광고 상품 데이터의 매핑 정의, 색인, 기본 CRUD API 구현
### 📋 요구사항
1. **데이터 구조 설계**
    - 필드: `productId`, `productName`, `adType`, `budget`, `status`, `createdAt`
    - `adType`: BANNER, VIDEO, SEARCH, DISPLAY
    - `status`: ACTIVE, INACTIVE, PENDING

2. **API 명세**
``` 
   POST /api/ad-products          # 상품 생성
   GET /api/ad-products/{id}      # 단일 상품 조회
   PUT /api/ad-products/{id}      # 상품 수정
   DELETE /api/ad-products/{id}   # 상품 삭제
   GET /api/ad-products           # 전체 상품 목록 조회
   POST /api/ad-products/bulk     # 대량 상품 등록
```
1. **구현 사항**
    - OpenSearch 매핑 생성
    - Spring Boot Controller 구현
    - 샘플 데이터 15개 생성
    - 기본 예외 처리

### 📝 샘플 데이터 예시
``` json
{
  "productId": "PROD_001",
  "productName": "스마트폰 배너 광고",
  "adType": "BANNER",
  "budget": 1500000,
  "status": "ACTIVE",
  "createdAt": "2025-01-15T10:00:00Z"
}
```
## 📊 **과제 2: 광고 통계(Ad Statistics) 데이터 관리**
### 🎯 목표
광고 통계 데이터의 매핑 정의, 색인, 기본 CRUD API 구현
### 📋 요구사항
1. **데이터 구조 설계**
    - 필드: `statisticsId`, `productId`, `impressions`, `clicks`, `conversions`, `spend`, `date`
    - 일별 통계 데이터 구조

2. **API 명세**
``` 
   POST /api/ad-statistics             # 통계 데이터 생성
   GET /api/ad-statistics/{id}         # 단일 통계 조회
   PUT /api/ad-statistics/{id}         # 통계 수정
   DELETE /api/ad-statistics/{id}      # 통계 삭제
   GET /api/ad-statistics              # 전체 통계 목록 조회
   POST /api/ad-statistics/bulk        # 대량 통계 등록
```
1. **구현 사항**
    - OpenSearch 매핑 생성
    - Spring Boot Controller 구현
    - 최근 10일간 샘플 데이터 15개 생성
    - Bulk API 활용

### 📝 샘플 데이터 예시
``` json
{
  "statisticsId": "STAT_001",
  "productId": "PROD_001",
  "impressions": 12500,
  "clicks": 187,
  "conversions": 15,
  "spend": 68500,
  "date": "2025-01-15"
}
```
## 🎯 **과제 3: 광고 캠페인(Ad Campaign) 데이터 관리**
### 🎯 목표
광고 캠페인 데이터의 매핑 정의, 색인, 기본 CRUD API 구현
### 📋 요구사항
1. **데이터 구조 설계**
    - 필드: `campaignId`, `campaignName`, `targetAudience`, `startDate`, `endDate`, `totalBudget`, `status`
    - `targetAudience`: 20-30, 30-40, 40-50, ALL
    - `status`: RUNNING, PAUSED, COMPLETED, SCHEDULED

2. **API 명세**
``` 
   POST /api/ad-campaigns             # 캠페인 생성
   GET /api/ad-campaigns/{id}         # 단일 캠페인 조회
   PUT /api/ad-campaigns/{id}         # 캠페인 수정
   DELETE /api/ad-campaigns/{id}      # 캠페인 삭제
   GET /api/ad-campaigns              # 전체 캠페인 목록 조회
   POST /api/ad-campaigns/bulk        # 대량 캠페인 등록
```
1. **구현 사항**
    - OpenSearch 매핑 생성
    - Spring Boot Controller 구현
    - 다양한 기간의 샘플 캠페인 15개 생성
    - 날짜 범위 처리

### 📝 샘플 데이터 예시
``` json
{
  "campaignId": "CAMP_001",
  "campaignName": "2025 신제품 론칭 캠페인",
  "targetAudience": "20-30",
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "totalBudget": 5000000,
  "status": "RUNNING"
}
```

