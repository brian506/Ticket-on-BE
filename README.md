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


### 대기열 로직

```
1. 사용자 대기열 등록
모든 사용자는 중앙에 있는 Redis 대기 큐에 memberId를 넣는다.

2.예약 서버 여유 상태 확인
예약 서버는 현재 접속 중인 사용자 수와 최대 처리 가능 인원을 관리한다.

3.대기열에서 사용자 순차적으로 처리
  예약 서버에 여유가 있을 때마다
→ Redis 대기 큐에서 한 명씩 pop 해서 꺼낸다.
→ 꺼낸 사용자를 예약 서버로 이동시켜 처리한다.

4.실시간 대기 인원 알림
  한 명씩 빼낼 때마다
→ 웹소켓을 통해 모든 접속된 사용자(또는 필요한 사용자)에게
→ 현재 대기 인원 수를 실시간으로 전달한다.

5. 예약 서버에 접속 중인 인원이 최대치에 도달하면
→ 대기 큐에서 더 이상 사용자 pop하지 않고 대기 상태를 유지시킨다.
```

Redis Eviction 정책

대기 큐에 들어간 사용자 정보는 절대 지워지면 안됩니다.(사용자 순서를 보장해야됨ㅇㅇ)  
절대 지워지면 안됨 == 무조건 보존해야 한다 -> noevivtion 정책 사용
TTL 설정은 어떻게 할지 ?


1. 예약 서버에 인원을 더 수용할 수 있는지를 대기 서버에서는 어떻게 확인할지 ?
2. 자신의 차례가 된 사용자가 예약 서버에 입장할때 인증/인가를 어떻게 처리할지 ?
3. 