import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ===== 1. 설정 및 주소 고정 (환경변수 무시) =====
// 로컬 터미널에서 실행하므로 localhost:8081로 강제 고정합니다.
const BASE_URL = 'http://localhost:8081';
const TICKET_TYPE_ID = 1; // SQL에 넣은 Standing A의 ID
const EXPECTED_TICKETS = 500;

// ===== 2. 커스텀 메트릭 =====
const prepare_success = new Counter('prepare_success');
const confirm_success = new Counter('confirm_success');
const ticket_soldout = new Counter('ticket_soldout');
const network_error = new Counter('network_error');
const confirm_duration = new Trend('confirm_duration');

export const options = {
  scenarios: {
      // 💡 여기가 핵심 변경 포인트!
      rate_limit_test: {
        // "일정한 속도로 도착(Constant Arrival Rate)"하게 만듭니다.
        executor: 'constant-arrival-rate',

        // 1초에 100명씩만 들여보내겠다 (서버 한계의 80~90% 수준)
        rate: 200,
        timeUnit: '1s',

        // 총 30초 동안 테스트 (100명 * 30초 = 3000명 처리 예상)
        duration: '40s',

        // 가상 유저는 필요하면 알아서 늘리도록 넉넉히 줌
        preAllocatedVUs: 100,
        maxVUs: 500,
      },
    },
  thresholds: {
    // 10,000개가 성공하지 못하면 테스트 실패로 간주
    'confirm_success': [`count>=${EXPECTED_TICKETS}`],
    // 95%의 요청은 500ms 이내에 완료되어야 함
    'http_req_duration': ['p(95)<500'],
  },
};

export default function () {
  // SQL 데이터에 맞게 1~10번 멤버 랜덤 선택
  const memberId = Math.floor(Math.random() * 500) + 1;
  const headers = { 'Content-Type': 'application/json' };

  // --- [STEP 1] 티켓 예매 요청 (purchaseTicket) ---
  const preparePayload = JSON.stringify({
    ticketTypeId: TICKET_TYPE_ID,
    quantity: 1,
    memberId: memberId,
    amount: 150000
  });

  const prepareRes = http.post(`${BASE_URL}/ticket/ticket-request`, preparePayload, { headers });

  // 1단계 방어: 네트워크 에러나 응답 없음 체크
  if (!prepareRes || prepareRes.status === 0) {
    network_error.add(1);
    return;
  }

  // 2단계 방어: 재고 부족(409) 체크
  if (prepareRes.status === 409) {
    ticket_soldout.add(1);
    return;
  }

  const isPrepareOk = check(prepareRes, { '1단계 성공': (r) => r.status === 200 });
  if (!isPrepareOk) return;

  // JSON 데이터 안전하게 추출
  let prepareData;
  try {
    prepareData = prepareRes.json().data;
    if (!prepareData) return;
  } catch (e) {
    return;
  }

  const ticketId = prepareData.ticketId;
  const orderId = prepareData.orderId;
  prepare_success.add(1);

  sleep(0.1);

  // --- [STEP 2] 결제 승인 요청 (createPayment) ---
  const confirmPayload = JSON.stringify({
    ticketId: ticketId,
    memberId: memberId,
    orderId: orderId,
    paymentKey: `pk-${orderId}-${Date.now()}`,
    amount: 150000
  });

  // Controller 경로에 맞춰 /v1/api/payments/confirm 호출
  const confirmRes = http.post(`${BASE_URL}/v1/api/payments/confirm`, confirmPayload, {
    headers,
    tags: { endpoint: 'confirm' }
  });

  // 💡 TypeError 완전 방어 코드
  check(confirmRes, {
    // r.body가 존재할 때만 .includes()를 호출하도록 체크
    '최종 승인 확인': (r) => r.status === 200 && r.body && r.body.includes('성공'),
    '상태값 확인': (r) => r.status === 200 || r.status === 409
  });

  if (confirmRes.status === 200) {
    confirm_success.add(1);
    confirm_duration.add(confirmRes.timings.duration);
  }

  sleep(0.5);
}