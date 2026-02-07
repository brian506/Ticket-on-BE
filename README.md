## Ticketon 티켓팅 서비스

----

## 프로젝트 목표

```
대규모 트래픽에 대응하기 위해 설계된 티켓팅 서비스입니다. 백엔드 개발자 3명이 함께 논블로킹·비동기 아키텍처 학습을 목적으로 시작한 프로젝트로, 
Kafka 기반의 이벤트 처리 방식을 도입해 단일 대기열 서버에서 최대 60,000 TPS를 안정적으로 처리할 수 있도록 단계적으로 발전시켰습니다.
```

## 👨‍💻 팀원 소개

| 이름    | 역할                            | 이메일                 | 사진                                                  |
|---------|----------------------------------|-------------------------|-----------------------------------------------------|
| 최영민   | 금융 데이터 정합성 유지 및 락 테스트   | brian506@naver.com       | <img src=" " width="100"/>                          |
| 신민석   | 대기열 입장 기능, 부하 테스트         | sin1768@naver.com        | <img src="https://github.com/user-attachments/assets/593ea7ec-5e57-4f15-b302-57b5b85934b4" width="80"/> |
| 김영솔   | 비관적락, 낙관적락, 분산락 구현        | onagu7167@gmail.com      | <img src="https://github.com/user-attachments/assets/43dcb73b-d3f6-4cd8-b7e2-b9468357196d" width="80"/>    |

### 기술 스택  

**Version** : `JDK21`  
**Backend** : `Spring Boot`, `Spring WebFlux`, `Kafka`, `WebSocket`, `JPA`,  
**Database** : `MySQL`, `Redis`  
**Devops** : `Nginx`, `Docker`, `AWS`



### 시스템 아키텍처

<img src="https://github.com/user-attachments/assets/2fb2fd47-8296-4c94-a6af-643a5728a61b" width="600"/>

### 대기열 입장 아키텍처

<img src="https://github.com/user-attachments/assets/e423d91b-6e8e-4b8b-8ea0-aefe6a8d93c1" width="600"/>

### 모니터링 아키텍처

<img src="https://github.com/user-attachments/assets/5edc7e60-501b-40ac-87df-99cbb5e6f3d4" width="600"/>

### 예약-결제 아키텍처

<img src="https://github.com/user-attachments/assets/521fa4c7-06f8-4a95-9820-6ab552895b55" width="600"/>

### 실행 방법

1. 루트 디렉토리에 .env 파일에 아래 환경변수들 필수 사용  

**RDS 관련 설정들은 개별 문의 : sin1768@naver.com**

```
DB_NAME=ticket_on
DB_DOCKER_URL=jdbc:mysql://mysql:3306/ticket_on?serverTimezone=Asia/Seoul
DB_LOCAL_URL=jdbc:mysql://localhost:3306/ticket_on?serverTimezone=Asia/Seoul
DB_USER_NAME=root
DB_PASSWORD=[DB비번]
LOKI_URL=http://loki:3100/loki/api/v1/push
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
TOSS_SECRET_KEY=test_sk_ex6BJGQOVDKOYJ0OPdpn3W4w2zNb
TOSS_CLIENT_KEY=test_ck_pP2YxJ4K871KbDe5qoQWVRGZwXLO
WEBSOCKET_BASE_URL=http://localhost:8081
QUEUE_BASE_URL=http://localhost:8082
RDS_DB_URL={RDS URL}
RDS_USER_NAME={RDS 사용자}
RDS_USER_PASSWORD={RDS 비번}
```

3. start.sh 권한 부여  
`chmod +x start.sh`

4. start.sh 실행 (최신 jar 빌드 -> Docker 이미지 생성 -> 생성된 Docker 이미지 및 모니터링 환경 이미지 일괄 생성)  
`./start.sh`


### REST API 네이밍 

1. 기본 접두사 형식 `/v1/api/`  
   `v1` 은 버전, `api` 는 REST API 식별 


2. 자원 기준의 URI 명명 (복수형 명사 사용) `/v1/api/tickets`  


3. RESTful 메서드별 네이밍 예시:

| 기능           | HTTP Method | URI 예시                          | 설명                        |
|----------------|-------------|-----------------------------------|-----------------------------|
| 티켓 전체 조회 | `GET`       | `/v1/api/tickets`                | 티켓 목록 조회              |
| 티켓 상세 조회 | `GET`       | `/v1/api/tickets/{ticketId}`     | 특정 티켓 상세 조회         |
| 티켓 생성      | `POST`      | `/v1/api/tickets`                | 새 티켓 생성                |
| 티켓 수정      | `PUT`       | `/v1/api/tickets/{ticketId}`     | 전체 정보 수정              |
| 티켓 부분 수정 | `PATCH`     | `/v1/api/tickets/{ticketId}`     | 특정 필드만 수정            |
| 티켓 삭제      | `DELETE`    | `/v1/api/tickets/{ticketId}`     | 티켓 삭제                   |


4. 중첩 자원 예시:  
   `GET /v1/api/tickets/{ticketId}/comments`  
   `POST /v1/api/tickets/{ticketId}/comments`

---

- 자원 이름은 **항상 복수형** 사용 (`tickets`, `users`, `comments`)
- URI 경로에는 **동사 대신 명사** 사용
- 클라이언트가 의미를 쉽게 이해할 수 있도록 **간결하고 직관적**이어야 함
- 상태 변경 등은 필요 시 `PATCH /{id}/status` 와 같이 명확히 표현



### Redis 구성

| 용도                           | 호스트 이름 (`host`)     | 포트 (`port`) | 설명                                              |
|--------------------------------|---------------------------|----------------|----------------------------------------------------|
| 대기열 순서 관리 Redis         | `wating-line-redis`       | `6379`         | 사용자의 대기 순서를 관리하는 Redis 인스턴스   |
| 예약 서버용 Redis (분산락 등) | `reservation-redis`       | `6380`         | 예약 처리 시 분산락 및 입장 인원 검증 등을 위한 Redis |
