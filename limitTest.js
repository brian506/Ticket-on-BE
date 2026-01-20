import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 주소 고정 (로컬 테스트용)
const BASE_URL = 'http://localhost:8081';
// SQL에서 대량 생성한 Member ID 범위에 맞춰주세요 (예: 10,000명)
const MAX_MEMBER_ID = 10000;

export const options = {
  scenarios: {
    breakpoint: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        // 1단계: 1분 동안 100명까지 서서히 증가 (Warm up)
        { duration: '1m', target: 100 },
        // 2단계: 2분 동안 1,000명까지 증가 (부하 구간)
        { duration: '2m', target: 1000 },
        // 3단계: 1분 동안 2,000명까지 극한으로 증가 (Breaking Point 탐색)
        { duration: '1m', target: 2000 },
      ],
    },
  },
  // 에러가 너무 많이 나도 멈추지 않고 끝까지 기록해서 한계를 봄
  thresholds: {
    http_req_failed: ['rate<0.99'],
  },
};

export default function () {
  const memberId = Math.floor(Math.random() * MAX_MEMBER_ID) + 1;
  const headers = { 'Content-Type': 'application/json' };

  // === 1. 티켓 요청 (가볍게 1단계만 호출하여 서버의 순수 처리량 측정) ===
  // *전체 로직을 다 돌려도 되지만, 보통 DB 병목은 첫 진입점에서 판가름 납니다.
  // *정확한 통합 테스트를 원하면 confirm까지 포함하세요.

  const payload = JSON.stringify({
    ticketTypeId: 1,
    quantity: 1,
    memberId: memberId,
    amount: 150000
  });

  const res = http.post(`${BASE_URL}/ticket/ticket-request`, payload, { headers });

  check(res, {
    'status is 200 or 409': (r) => r.status === 200 || r.status === 409,
  });

  // 실제 유저처럼 약간의 텀을 둠 (너무 빠르면 네트워크 병목이 먼저 옴)
  sleep(0.1);
}