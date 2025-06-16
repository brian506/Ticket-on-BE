## Ticketon 티켓팅 서비스

----

```
Ticketon은 대규모 트래픽 속에서도 안정적으로 티켓을 예매할 수 있는 고성능 분산 티켓팅 시스템입니다.
실시간 경쟁 상황에서도 공정성과 성능을 최우선으로 설계하였습니다.

목표: 수십만 건의 요청을 짧은 시간 내 처리하며 오버부킹을 방지하고, 정확한 예약을 보장하는 시스템 구축.
``` 


### 커밋 컨벤션

- feat : 새로운 기능 구현
- docs : 문서(README) 수정
- fix : 버그 수정
- refactor : 리팩토링
- test : 테스트 코드 추가 또는 수정
- chore : 기타 변경사항 (빌드 설정, 패키지 관리 등)




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

