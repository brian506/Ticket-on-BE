import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ===== 설정 =====
const BASE_URL = 'http://localhost:8081';

// ===== 메트릭 =====
const flow_success = new Counter('flow_success'); // 전체 플로우 성공 횟수
const req_duration = new Trend('req_duration');   // 예매 요청 시간
const pay_duration = new Trend('pay_duration');   // 결제 승인 시간

export const options = {
  scenarios: {
    full_flow_test: {
      executor: 'constant-arrival-rate', // 🔥 목표 TPS 강제 유지 모드
      rate: 400,             // 초당 300명의 사용자가 유입됨 (목표 TPS)
      timeUnit: '1s',
      duration: '4m',       // 30초간 지속

      // VU를 미리 넉넉하게 할당 (sleep 시간 고려해서 계산해야 함)
      // TPS 300 * 5초 대기 = 최소 1500명 필요. 여유 있게 3000 잡음.
      preAllocatedVUs: 2000,
      maxVUs: 5000,          // 부족하면 K6가 알아서 더 늘림
    },
  },
  thresholds: {
    // 결제 승인까지 끝난 건수가 있어야 함
    'flow_success': ['count>0'],
    // 결제 API 응답 속도 관리
    'pay_duration': ['p(95)<1000'],
  },
};

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  // 고유한 유저/주문 ID 생성
  // (충돌 안 나게 VU ID와 시간 조합)
  const uniqueId = (__VU * 10000) + __ITER;
  const memberId = (uniqueId % 10000) + 1;

  // ============================================================
  // [STEP 1] 티켓 예매 요청 (사용자 진입)
  // ============================================================
  const preparePayload = JSON.stringify({
    ticketTypeId: 1,
    memberId: memberId,
    quantity: 1,
    amount: 150000
  });

  const res1 = http.post(`${BASE_URL}/ticket/ticket-request`, preparePayload, {
    headers,
    tags: { type: 'REQ' }
  });

  req_duration.add(res1.timings.duration);

  // 실패하면(매진 등) 여기서 종료
  if (res1.status !== 200) return;

  // OrderId 파싱
  let orderId;
  try {
    orderId = res1.json().data.orderId; // 경로 확인 필요
  } catch(e) { return; }


  // ============================================================
  // [STEP 2] 사용자 대기 (User Think Time + System Lag)
  // ============================================================
  // 이 sleep은 두 가지 의미가 있습니다.
  // 1. 실제 사용자가 결제 비밀번호 입력하는 시간
  // 2. Kafka가 메시지를 컨슈밍해서 DB에 넣을 때까지의 물리적 시간
  sleep(0.1);


  // ============================================================
  // [STEP 3] 결제 승인 요청 (최종 완료)
  // ============================================================
  const confirmPayload = JSON.stringify({
    ticketId: 0,
    memberId: memberId,
    orderId: orderId,
    paymentKey: `pk-${orderId}`,
    amount: 150000
  });

  const res2 = http.post(`${BASE_URL}/v1/api/payments/confirm`, confirmPayload, {
    headers,
    tags: { type: 'PAY' }
  });

  pay_duration.add(res2.timings.duration);

  // 최종 성공 여부 판단
  if (res2.status === 200) {
    flow_success.add(1);
  }
}