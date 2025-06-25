# 📆 공휴일 수집/검색 프로젝트

> 외부 공공 API를 활용하여 전 세계 국가의 공휴일 정보를 수집하고, 연도·국가별로 검색 및 동기화를 지원하는 백엔드 API입니다.
비동기 + 배치 구조를 통해 빠르게 데이터를 적재하며, 분산락을 활용해 데이터 정합성을 보장합니다.
---

# 🚀 프로젝트 빌드 & 실행 방법

본 프로젝트는 `Spring Boot` 기반 애플리케이션으로, 외부 설정 파일과 Docker 기반의 Redis 서버를 이용하여 실행됩니다.

## ✅ 1. GitHub 저장소 Clone

```bash
git clone https://github.com/your-username/holiday-project.git
cd holiday-project
```

## ✅ 2. Redis 서버 실행 (필수)
### Redis 서버는 필수입니다

- `docker-compose.yml`을 통해 Redis 서버를 실행하지 않으면, 애플리케이션에서 **Redisson Lock 처리 시 오류**가 발생
- 반드시 `docker-compose up -d`로 Redis 컨테이너를 띄운 후 실행해주세요요
```bash
docker-compose up -d
```

## ✅ 3. 애플리케이션 실행
### 1️⃣ Gradle 빌드
```bash
./gradlew build
```

### 2️⃣ Spring Boot 서버 실행
```bash
java -jar build/libs/holiday-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml
```

### ⚠️ H2 DB 설정 (`url`)은 반드시 명시되어야 합니다
- 아래 설정처럼 `url: jdbc:h2:tcp://localhost/~/test2`을 **명시적으로 입력**해야 H2 DB에 TCP 방식으로 접근이 가능합니다.
```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/test2
```

- 해당 설정이 없거나 잘못되면, **H2 DB가 정상 연결되지 않아 애플리케이션 실행이 실패**합니다.

### 3️⃣ Swagger API 문서 접속
```
http://localhost:8080/swagger-ui/index.html
```
## 🗂 외부 설정 파일 예시 (`application.yml`)

```yaml
spring:
  profiles:
    active: local # 기본 실행 프로파일

  datasource:
    url: jdbc:h2:tcp://localhost/~/test2
    driver-class-name: org.h2.Driver
    username: sa
    password:

  data:
    redis:
      host: localhost
      port: 6380

---
# local 환경 설정
spring:
  config:
    activate:
      on-profile: local
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true

---
# test 환경 설정
spring:
  config:
    activate:
      on-profile: test
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
```

---

# 📌 프로젝트 개요

- ✅ 외부 API를 통해 109개 국가의 공휴일 정보를 수집
- ✅ 연도/국가 코드로 공휴일 데이터를 검색, 갱신, 삭제
- ✅ 매년 1월 2일 전년도, 금년도 데이터 자동 배치 동기화
- ✅ 데이터 중복 방지와 동시성 제어를 위한 분산락 적용
- ✅ 비동기 적재 구조 및 스케일 확장을 고려한 설계
- ✅ 외부 API의 네트워크 오류 등에 대한 재처리 고려
- ✅ Swagger 기반의 문서화 및 테스트 지원

---

# 🛠️ 기술 스택

| 기술 & 라이브러리 | 사용 이유 |
|------|-----------|
| Java 21 | 최신 언어 기능 및 성능 향상을 위해 |
| Spring Boot 3.4.5 | 생산성과 유연성을 갖춘 애플리케이션 프레임워크 |
| Gradle | 의존성 및 빌드 관리 |
| Spring Data JPA | 객체지향적으로 DB를 다루면 CRUD 생산성 향상상 |
| QueryDSL | 복잡하고 동적인 검색 조건을 안전하게 처리 |
| H2(In-memory DB) | 테스트 환경용 경량 DB |
| Redisson | 	Redis 기반의 분산 락 구현을 통해 Race Condition 및 중복 저장 방지 |
| Spring Retry | 외부 API 요청 시 일시적 장애에 대한 재시도 처리 |
| Swagger (springdoc-openapi) | API 명세 자동화 |
| JUnit5 | 단위, 통합 테스트 작성 |
| Docker | 	Redis 서버를 컨테이너로 구동해 환경 일관성 확보 |
---

# 🧾 핵심 도메인 모델 (Holiday)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| id | Long | 공휴일 ID (PK) |
| date | LocalDate | 공휴일 날짜 |
| localName | String | 로컬 언어 공휴일명 |
| name | String | 공통 공휴일명 (영문) |
| countryCode | String | ISO 국가코드 (예: KR) |
| fixed | Boolean | 매년 고정일 여부 |
| global | Boolean | 전 세계 공통 공휴일 여부 |
| launchYear | Integer | 최초 시행 연도 |
| counties(값 객체) | List<String> | 해당 지역 리스트 |
| types(값 객체) | List<String> | 공휴일 유형 |

---

# 📜 API 명세서

## 1. 최근 5년치 공휴일 적재

| 항목 | 설명 |
|------|------|
| 메서드 | `POST` |
| URL | `/api/holidays/save-recent` |
| 설명 | 109개국의 최근 5년 공휴일을 외부 API에서 수집하여 비동기로 DB에 저장 <br>  |
### ☑️ 응답 예시
```json
{
  "isSuccess": true,
  "result": {
    "message": "최근 5년치 공휴일 적재 요청이 완료되었습니다."
  }
}
```

---

## 2. 공휴일 검색 (페이징 지원)

| 항목 | 설명 |
|------|------|
| 메서드 | `POST` |
| URL | `/api/holidays/search?page={}&size={}` |
| 설명 | 조건에 따라 공휴일을 검색합니다. |

### 🔸 URL 쿼리 파라미터 (RequestParam)
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| page | Integer | X | 페이지 번호 (1부터 시작), 기본값 1 |
| size | Integer | X | 페이지 크기 (한 페이지당 항목 수), 기본값 10 |

### 🔸 요청 바디 (RequestBody)
| 필드명 | 타입 | 필수여부 | 설명 |
|:---|:---|:---|:---|
| year | Integer | 선택<br>(null가능) | 조회할 연도 |
| fromDate | LocalDate | 선택<br>(null가능) | 시작 날짜 (yyyy-MM-dd) |
| toDate | LocalDate | 선택<br>(null가능) | 종료 날짜 (yyyy-MM-dd) |
| name | String | 선택<br>(null가능) | 공휴일의 공식 이름 (예: New Year's Day) |
| localName | String | 선택<br>(null가능) | 해당 국가의 로컬 이름 (예: 신정) |
| countryCode | String | 선택<br>(null가능) | ISO 국가 코드 (예: KR, US) |
| fixed | Boolean | 선택<br>(null가능) | 매년 같은 날짜인지 여부 |
| global | Boolean | 선택<br>(null가능) | 글로벌 공휴일 여부 |
| launchYear | Integer | 선택<br>(null가능) | 최초 도입 연도 |
| counties | List<String> | 선택<br>(null가능) | 적용 지역 코드 리스트 |
| types | List<String> | 선택<br>(null가능) | 공휴일 유형 리스트 (예: ["Public"]) |
### ☑️ 요청 예시
```json
{
  "year": 2025,
  "fromDate": null,
  "toDate": null,
  "localName": null,
  "name": null,
  "countryCode": "KR",
  "fixed": null,
  "global": null,
  "launchYear": null,
  "counties": null,
  "types": null
}
```

### ☑️ 응답 예시
```
{
    "isSuccess": true,
    "result": {
        "page": 1,
        "content": [
            {
                "year": 2025,
                "date": "2025-01-01",
                "localName": "새해",
                "name": "New Year's Day",
                "countryCode": "KR",
                "fixed": false,
                "global": true,
                "launchYear": null,
                "counties": [],
                "types": [
                    "Public"
                ]
            },
            {
                "year": 2025,
                "date": "2025-01-28",
                "localName": "설날",
                "name": "Lunar New Year",
                "countryCode": "KR",
                "fixed": false,
                "global": true,
                "launchYear": null,
                "counties": [],
                "types": [
                    "Public"
                ]
            },
            {
                "year": 2025,
                "date": "2025-01-29",
                "localName": "설날",
                "name": "Lunar New Year",
                "countryCode": "KR",
                "fixed": false,
                "global": true,
                "launchYear": null,
                "counties": [],
                "types": [
                    "Public"
                ]
            },
           
            ````
          
        ],
        "size": 10,
        "totalElements": 15,
        "totalPages": 2,
        "first": true,
        "last": false
    }
}
```
---
## 3. 공휴일 재동기화 (Upsert)

| 항목 | 설명 |
|------|------|
| 메서드 | `PUT` |
| URL | `/api/holidays/upsert` |
| 설명 | 특정 연도와 국가 코드에 해당하는 공휴일 정보를 외부 공공 API로부터 다시 조회하여 덮어씁니다. <br>기존 데이터가 있으면 갱신되고, 없으면 새로 저장됩니다. |

### 🔸 요청 바디 (RequestBody - HolidayKeyRequest)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| year | Integer | O | 대상 연도 (예: 2024) |
| countryCode | String | O | ISO 국가 코드 (예: "KR", "US") |

### ☑️ 요청 예시

```json
{
  "year": 2024,
  "countryCode": "KR"
}
```

### ☑️ 응답 예시

```json
{
    "isSuccess": true,
    "result": {
        "message": "2025, KR 데이터 덮어쓰기 완료"
    }
}
```

---

## 4. 공휴일 삭제

| 항목 | 설명 |
|------|------|
| 메서드 | `DELETE` |
| URL | `/api/holidays/{year}/{countryCode}` |
| 설명 | 특정 연도와 국가 코드에 해당하는 공휴일 정보를 데이터베이스에서 삭제합니다. <br>해당 정보가 없더라도 예외 없이 처리됩니다. |

### 🔸 경로 파라미터 (PathVariable)

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| year | Integer | O | 삭제 대상 연도 (예: 2024) |
| countryCode | String | O | ISO 국가 코드 (예: "KR", "US") |

### ☑️ 예시 호출

```
DELETE /api/holidays/2024/KR
```

### ☑️ 응답 예시

```json
{
    "isSuccess": true,
    "result": {
        "message": "2025, KR 데이터 레코드 삭제 완료"
    }
}
```

## 5. 배치 자동화
### 기능 설명

- 매년 1월 2일 오전 1시(KST)에 전년도, 금년도 데이터를 자동 동기화
- 스프링 스케줄러 기반 자동실행  (Spring @Scheduled)

# 🧠 설계 포인트

## ✅ 비동기 + 배치 구조 도입 이유

- 대량의 공휴일 데이터를 빠르게 적재하기 위해, ThreadPool 기반 병렬 처리를 적용했습니다. (109개 나라 * 5년치)
- 단순 순차 실행 시, 연도 × 국가 조합이 많아질수록 처리 시간이 기하급수적으로 증가하므로, 이를 해결하기 위해 `CompletableFuture` + `ThreadPoolTaskExecutor`로 비동기 병렬 처리를 수행합니다.
- `HolidayBatchExecutor`는 국가코드 리스트를 BATCH_SIZE(국가20개) 단위로 나누고, 각 연도별 작업을 동시에 실행함으로써 병렬 처리량을 최적화하였습니다.
- 비동기 구조 덕분에 실제 동기 구조로 진행했을때 평균 13분에서 -> 10초로 단축하였습니다. 

## ✅ 공통 배치 컴포넌트(HolidayBatchExecutor)로 재사용성 확보
- HolidayBatchExecutor는 연도 리스트와 국가코드 리스트를 입력으로 받아, 작업(BiConsumer 형태)을 실행하는 배치 실행기입니다.
- 데이터 적재, 배치 자동화 기능에서 공통된 처리 흐름(국가 리스트 → 연도별 비동기 저장 → 배치 완료 대기)로직을 추상화 하여 중복을 제거하고 유지보수성을 높였습니다. 
- 향후 국가별 통계 처리 등으로 확장 시, 다른 도메인 배치 작업에서도 재사용 가능한 구조로 설계하였습니다.


## ✅ 분산락(Redis 기반 Redisson) 도입 이유

- 같은 연도-국가 코드로 동시에 여러 요청이 들어올 경우, 중복 저장 및 Race Condition 문제가 발생할 수 있습니다.
- 이를 방지하기 위해 Redisson을 활용한 분산락을 적용하여, 특정 연도/국가 조합에 대해 하나의 스레드만 작업을 수행하도록 보장하였습니다.
- 기존의 DB 기반 Pessimistic Lock은 다음과 같은 문제가 있어 채택하지 않았습니다:
  - DB 커넥션을 오래 점유하여 전체 병목의 원인이 될 수 있음
  - 트랜잭션 종료 전까지 락 유지, 다중 요청 시 병렬 처리 제한
  - 서버 간 확장 불가능, 단일 DB에 의존
- 반면 Redis Lock은 락 유지 시간 설정 및 자동 해제 기능, 빠른 실패 및 재시도 전략, 멀티 서버 환경에서도 확장성을 확보할수 있습니다.

## ✅ Redis 캐시 미도입 결정 배경
> 일반적으로 Redis는 빠른 조회를 위해 많이 사용되지만, 본 프로젝트에서는 오히려 캐싱이 효율을 떨어뜨릴 수 있다는 판단 하에 사용하지 않았습니다.
- Holiday(year, countryCode) 조합은 요청마다 다양하게 분포될 가능성이 높음
- 특정 연도, 특정 국가에 대한 요청이 반복되는 패턴이 거의 없기 때문에 → 캐시 적중률이 매우 낮을 것으로 판단
- Redis는 메모리 기반이기 때문에 낮은 적중률에도 비용이 지속 발생하며, 오히려 불필요한 자원 낭비가 될 수 있음
- 결론 : 향후 트래픽 증가나 반복 요청 패턴이 확인된다면 그때 캐시 도입을 고려하는 방향으로 설계
---

#  ./gradlew clean test 성공 스크린샷 (JUNIT5)
![image](https://github.com/user-attachments/assets/e3c52a79-5a23-4abd-bd94-9b854d753d68)
![image](https://github.com/user-attachments/assets/ac60e750-eff3-40b6-9912-91d37bb00c82)


