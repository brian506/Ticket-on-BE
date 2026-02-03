import http from 'k6/http';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import execution from 'k6/execution';

// ===== 설정 =====
const BASE_URL = 'http://localhost:8081';

// ===== 메트릭 =====
const pay_success = new Counter('pay_success');
const pay_fail = new Counter('pay_fail');
const pay_duration = new Trend('pay_duration'); // 결제 API 전용 지연시간

export const options = {
  scenarios: {
    payment_load: {
      executor: 'constant-arrival-rate', // 목표 TPS 유지 모드
      rate: 300,             // 🔥 목표 TPS: 초당 300건 결제
      timeUnit: '1s',
      duration: '30s',       // 30초 동안 공격
      preAllocatedVUs: 100,
      maxVUs: 1000,
    },
  },
  thresholds: {
    'pay_success': ['count>0'],
    'pay_duration': ['p(95)<500'], // 결제는 더 빨라야 함 (500ms 이내)
  },
};

export default function () {
  // 1. 생성 때와 똑같은 로직으로 ID 생성 (순차적 or 랜덤)
  // 여기서는 1번부터 10000번 사이의 ID를 사용해야 함

  // __ITER는 각 VU별 반복 횟수이므로, 전체 범위에서 고유하게 뽑으려면
  // execution.scenario.iterationInTest 사용이 가장 정확함 (순차 처리 시)
  // 또는 랜덤으로 뽑아서 동시성 테스트를 할 수도 있음

  const totalTickets = 10000; // Step 1에서 만든 개수
  // 순차적으로 결제 시도 (이미 생성된 범위 내에서)
  const uniqueId = (execution.scenario.iterationInTest % totalTickets) + 1;
  const orderId = `order-${uniqueId}`;

  const headers = { 'Content-Type': 'application/json' };

  const payload = JSON.stringify({
    ticketId: 0, // 서버에서 안 쓰면 0
    memberId: 1,
    orderId: orderId, // 🔥 아까 만든 그 ID
    paymentKey: `pk-${orderId}`,
    amount: 150000
  });

  // [Payment Confirm] 결제 승인 요청 (Update + Outbox)
  const res = http.post(`${BASE_URL}/v1/api/payments/confirm`, payload, {
    headers,
    tags: { name: 'PaymentAPI' }
  });

  // 결과 집계
  if (res.status === 200) {
    pay_success.add(1);
    pay_duration.add(res.timings.duration);
  } else if (res.status === 409) {
    // 이미 결제된 경우 (테스트 반복 시 발생 가능) -> 성공으로 간주할지 선택
    // pay_success.add(1);
  } else {
    pay_fail.add(1);
    // console.error(`Pay Failed: ${res.status} ${res.body}`);
  }
}