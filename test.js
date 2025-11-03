import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend } from 'k6/metrics';

// --- 사용자 설정 ---
// 테스트할 기본 URL
const BASE_URL = 'http://localhost:8080';
// 테스트할 티켓 ID (핫스팟 시뮬레이션)
const TICKET_TYPE_ID = 1;
// ------------------

// 커스텀 트렌드 메트릭 (응답 시간 측정용)
const reservationTrend = new Trend('t1_reservation_api');
const confirmationTrend = new Trend('t2_confirmation_api');

/**
 * k6 옵션
 * 1. vus: 50명의 동시 사용자가
 * 2. stages: 30초 동안 50명까지 증가하고, 1분간 유지한 뒤, 30초 동안 감소
 * 3. thresholds: 95%의 요청이 800ms 안에 처리되어야 함
 */
export const options = {
  scenarios: {
    ramping: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 50 }, // 30초간 50명까지 증가
        { duration: '1m', target: 50 },  // 1분간 50명 유지
        { duration: '30s', target: 0 },  // 30초간 0명으로 감소
      ],
      gracefulRampDown: '10s',
    },
  },
  thresholds: {
    'http_req_failed': ['rate<0.01'], // 1% 미만의 에러율
    't1_reservation_api': ['p(95)<800'], // 1단계 API의 95%가 800ms 이내
    't2_confirmation_api': ['p(95)<500'], // 2단계 API의 95%가 500ms 이내
  },
};

/**
 * HTML 본문에서 정규식으로 변수를 추출하는 헬퍼 함수
 * (paymentConfirm.html 스크립트의 변수를 파싱)
 */
function parseVariable(body, varName) {
  const regex = new RegExp(`const ${varName}\\s*=\\s*"?([^";]+)"?;?`);
  const match = body.match(regex);
  if (match && match[1]) {
    return match[1];
  }
  return null;
}

/**
 * k6 메인 테스트 함수 (VU 1명당 실행되는 로직)
 */
export default function () {
  let orderId = null;
  let amount = null;
  let reservationResponse = null;

  // 1단계: 티켓 예약 (재고 선점 + PENDING 티켓 INSERT)
  group('T1_Reservation', () => {
    reservationResponse = http.get(
      `${BASE_URL}/payment/prepare?ticketTypeId=${TICKET_TYPE_ID}&quantity=1`,
      { tags: { name: 'PaymentPrepare' } }
    );

    check(reservationResponse, {
      '[T1] Reservation success (status 200)': (r) => r.status === 200,
    });

    // 응답 시간 측정
    reservationTrend.add(reservationResponse.timings.duration);

    if (reservationResponse.status === 200) {
      // 응답 HTML에서 orderId와 amount 파싱
      orderId = parseVariable(reservationResponse.body, 'orderId');
      amount = parseInt(parseVariable(reservationResponse.body, 'amount'), 10);

      check(null, {
        '[T1] Parsed orderId': () => orderId !== null,
        '[T1] Parsed amount': () => amount > 0,
      });
    }
  });

  // 1단계가 실패했거나, 파싱에 실패하면 2단계를 실행하지 않음
  if (!orderId || !amount) {
    return; // 이 VU의 테스트 중단
  }

  // 1~3초간의 사용자 "결제" 시간 시뮬레이션
  sleep(Math.random() * 2 + 1);

  // 2단계: 결제 확정 (PAID로 UPDATE + Outbox에 INSERT)
  group('T2_Confirmation', () => {
    // PG사(Toss)가 반환할 가짜 paymentKey 생성
    const mockPaymentKey = `k6-key-${__VU}-${__ITER}`;

    const payload = JSON.stringify({
      paymentKey: mockPaymentKey,
      orderId: orderId,
      amount: amount
    });

    const params = {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'PaymentConfirm' },
    };

    // 'paymentSuccess.html'이 호출하는 API를 k6가 직접 호출
    const confirmResponse = http.post(
      `${BASE_URL}/v1/api/payments/confirm`,
      payload,
      params
    );

    check(confirmResponse, {
      '[T2] Confirmation success (status 200)': (r) => r.status === 200,
    });

    confirmationTrend.add(confirmResponse.timings.duration);
  });
}