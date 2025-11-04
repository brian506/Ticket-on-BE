import http from 'k6/http';
import { check, sleep } from 'k6';

// -------- 옵션 설정 --------
export const options = {
  vus: 50,          // 동시에 50명
  duration: '15s',  // 15초간 부하 테스트
  thresholds: {
    http_req_failed: ['rate<0.05'],  // 실패율 5% 미만
    http_req_duration: ['p(95)<500'], // 95% 요청이 500ms 미만
  },
};

// -------- 테스트 환경 --------
const BASE_URL = 'http://localhost:8081'; // Spring 서버 주소
const MEMBER_ID = 1;                      // 테스트용 memberId
const TICKET_TYPE_ID = 1;                 // 테스트용 티켓 타입
const HEADERS = { 'Content-Type': 'application/json' };

// -------- 테스트 시나리오 --------
export default function () {
  // 1️⃣ 티켓 결제 요청 (티켓 예약)
  const ticketRequest = JSON.stringify({
    ticketTypeId: TICKET_TYPE_ID,
    memberId: MEMBER_ID,
    quantity: 1,
  });

  const ticketRes = http.post(`${BASE_URL}/ticket/ticket-request`, ticketRequest, { headers: HEADERS });

  check(ticketRes, {
    '티켓 요청 성공': (r) => r.status === 200,
  });

  // 응답에서 orderId, ticketId 등 추출
  const readyData = ticketRes.json();
  const ticketId = readyData?.data?.ticketId || null;
  const orderId = readyData?.data?.orderId || `order-${__VU}-${Date.now()}`; // VU별 고유값

  // 2️⃣ 결제 완료(confirm) 요청
  const confirmRequest = JSON.stringify({
    ticketId: ticketId,
    orderId: orderId,
    memberId: MEMBER_ID,
    paymentKey: `pk_test_${__VU}_${Date.now()}`,
    amount: 10000,
  });

  const confirmRes = http.post(`${BASE_URL}/v1/api/payments/confirm`, confirmRequest, { headers: HEADERS });

  check(confirmRes, {
    '결제 승인 성공': (r) => r.status === 200,
  });

  sleep(0.5);
}
