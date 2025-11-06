import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// ===== 환경변수 =====
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const TICKET_TYPE_ID = Number(__ENV.TICKET_TYPE_ID || 1);
const EXPECTED_TICKETS = Number(__ENV.EXPECTED_TICKETS || 100);

// ===== 부하 설정 (⚠️ 함수 아님) =====
export const options = {
  vus: 300,
  duration: '10s',
  thresholds: {
    http_req_duration: ['p(95)<300'],
    confirm_success: [`count==${EXPECTED_TICKETS}`],
  },
};

// ===== 커스텀 메트릭 =====
const prepare_ok = new Counter('prepare_ok');
const confirm_success = new Counter('confirm_success');
const ticket_soldout = new Counter('ticket_soldout');
const ticket_error = new Counter('ticket_error');

// ===== 테스트 시나리오 =====
export default function () {
  const memberId = __VU;

  // 1️⃣ 결제 요청
  const preparePayload = JSON.stringify({
    ticketTypeId: TICKET_TYPE_ID,
    memberId: memberId,
    quantity: 1,
    amount: 10000,
  });
  const headers = { 'Content-Type': 'application/json' };

  const prepareRes = http.post(`${BASE_URL}/ticket/ticket-request`, preparePayload, { headers });

  if (prepareRes.status === 409) {
    ticket_soldout.add(1);
    return;
  }

  const ok = check(prepareRes, { '티켓 요청 200': (r) => r.status === 200 });
  if (!ok) {
    ticket_error.add(1);
    return;
  }
  prepare_ok.add(1);

  // 응답 파싱
  let data;
  try {
    data = prepareRes.json()?.data || {};
  } catch (e) {
    ticket_error.add(1);
    return;
  }

  const ticketId = data.ticketId;
  const orderId = data.orderId;
  const price = data.price;
  const mid = data.memberId ?? memberId;

  const hasRequired = check(data, {
    'ticketId 존재': () => !!ticketId,
    'orderId 존재': () => !!orderId,
    'price 존재': () => Number.isFinite(price),
  });
  if (!hasRequired) {
    ticket_error.add(1);
    return;
  }

  // 2️⃣ 결제 승인
  const uniqueKey = `pk-${memberId}-${__ITER}-${Date.now()}`;
  const confirmPayload = JSON.stringify({
    ticketTypeId: TICKET_TYPE_ID,
    ticketId: ticketId,
    paymentKey: uniqueKey,
    memberId: mid,
    orderId: orderId,
    amount: price,
  });

  const confirmRes = http.post(`${BASE_URL}/v1/api/payments/confirm`, confirmPayload, { headers });

  if (confirmRes.status === 200) {
    confirm_success.add(1);
  } else if (confirmRes.status === 409 || confirmRes.status === 422) {
    ticket_soldout.add(1);
  } else {
    ticket_error.add(1);
  }

  sleep(0.2);
}
