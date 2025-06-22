## Ticketon 티켓팅 서비스

----

```
Ticketon은 대규모 트래픽 속에서도 안정적으로 티켓을 예매할 수 있는 고성능 분산 티켓팅 시스템입니다.
실시간 경쟁 상황에서도 공정성과 성능을 최우선으로 설계하였습니다.

목표: 수십만 건의 요청을 짧은 시간 내 처리하며 오버부킹을 방지하고, 정확한 예약을 보장하는 시스템 구축.
```


### 기술 스택  

**Version** : `JDK21`  
**Backend** : `Spring Boot`, `JPA`, `QueryDSL`  
**Database** : `MySQL`, `Redis`  
**Devops** : `Nginx`, `Docker`,



### 시스템 아키텍처 (초기)

![티켓팅 시스템 아키텍처](./flow.png)



### 실행 방법

1. 루트 디렉토리에 .env 파일에 아래 환경변수들 필수 사용  

```
DB_NAME=ticket_on
DB_DOCKER_URL=jdbc:mysql://mysql:3306/ticket_on?serverTimezone=Asia/Seoul
DB_LOCAL_URL=jdbc:mysql://localhost:3306/ticket_on?serverTimezone=Asia/Seoul
DB_USER_NAME=root
DB_PASSWORD=[DB비번]
LOKI_URL=http://loki:3100/loki/api/v1/push
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
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
