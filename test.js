import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Trend } from 'k6/metrics';


const BASE_URL = 'http://3.26.171.218:8081';

const flow_success = new Counter('flow_success');
const req_duration = new Trend('req_duration');
const pay_duration = new Trend('pay_duration');

export const options = {
  scenarios: {
    breaking_point_test: {
      executor: 'ramping-arrival-rate',
      startRate: 100, // 시작부터 100 TPS로 강하게 시작
      timeUnit: '1s',

      // ⭐ VU 부족해서 테스트 멈추지 않게 넉넉히 설정
      preAllocatedVUs: 1000,
      maxVUs: 4000,

      stages: [
        { target: 200, duration: '1m' }, // 1분 동안 200까지 도달 (워밍업)
        { target: 300, duration: '1m' }, // 300 TPS 구간
        { target: 500, duration: '2m' }, // ⭐ 마의 400 TPS 구간 (여기서 터질 확률 높음)
        { target: 600, duration: '1m' }, // 혹시 버티면 500까지 (확인사살)
        { target: 0, duration: '30s' },  // 쿨다운
      ],
    },
  },
  thresholds: {
    'flow_success': ['count>0'],
    // 2초 넘어가면 사실상 실패로 간주
    'pay_duration': ['p(95)<2000'],
  },
};
export default function () {
  const headers = { 'Content-Type': 'application/json' };

  const uniqueId = (__VU * 10000) + __ITER;
  const memberId = (uniqueId % 10000) + 1;

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


  if (res1.status !== 200) return;


  let orderId;
  try {
    orderId = res1.json().data.orderId;
  } catch(e) { return; }

  sleep(1);

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


  if (res2.status === 200) {
    flow_success.add(1);
  }
}