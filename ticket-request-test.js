import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';
import execution from 'k6/execution'; // 🔥 실행 정보를 얻기 위해 필요

// ===== 설정 =====
const BASE_URL = 'http://localhost:8081';
const TICKET_TYPE_ID = 1;

// ===== 메트릭 =====
const create_success = new Counter('create_success');
const create_fail = new Counter('create_fail');

export const options = {
  scenarios: {
    create_load: {
      executor: 'shared-iterations', // 정해진 횟수만큼 정확히 실행
      vus: 50,                       // 50명이 동시에
      iterations: 10000,             // 총 10,000건 생성 (1번 ~ 10000번)
      maxDuration: '5m',             // 최대 5분
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<2000'], // 생성 지연시간 2초 이내
    'create_success': ['count>0'],
  },
};

export default function () {
  // 1. 고유하고 예측 가능한 Order ID 생성
  // scenario.iterationInTest는 0부터 시작해서 9999까지 1씩 증가함 (전체 VU 공유)
  const uniqueId = execution.scenario.iterationInTest + 1;
  const orderId = `order-${uniqueId}`; // 예: order-1, order-2 ...

  const headers = { 'Content-Type': 'application/json' };

  const payload = JSON.stringify({
    orderId: orderId, // 🔥 클라이언트가 지정한 ID 전송
    ticketTypeId: TICKET_TYPE_ID,
    memberId: 1,
    quantity: 1,
    amount: 150000
  });

  // [Ticket Request] 생성 요청
  const res = http.post(`${BASE_URL}/ticket/ticket-request`, payload, {
    headers,
    tags: { name: 'CreateAPI' } // Grafana 태그용
  });

  // 결과 집계
  if (res.status === 200) {
    create_success.add(1);
  } else {
    create_fail.add(1);
    console.error(`Create Failed: ${res.status} ${res.body}`);
  }
}