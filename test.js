import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Trend } from 'k6/metrics';


const BASE_URL = 'http://3.26.171.218:8081';


const req_duration = new Trend('req_duration');

const pay_success_trend = new Trend('pay_success_duration'); // 성공(재고 차감) 시간
const pay_success_count = new Counter('pay_success_count');  // 성공 횟수

const pay_soldout_trend = new Trend('pay_soldout_duration'); // 재고 없음 시간
const pay_soldout_count = new Counter('pay_soldout_count');  // 재고 없음 횟수

const pay_error_count = new Counter('pay_error_count');      // 그 외 에러 횟수

export const options = {
  scenarios: {
    breaking_point_test: {
      executor: 'ramping-arrival-rate',
      startRate: 100,
      timeUnit: '1s',

      preAllocatedVUs: 1000,
      maxVUs: 5000,

      stages: [
        { target: 300, duration: '1m' }, // 300 TPS
        { target: 500, duration: '1m' }, // 500 TPS (한계 돌파 시도)
        { target: 0, duration: '30s' },  // 쿨다운
      ],
    },
  },
  thresholds: {
    'pay_success_count': ['count>0'],
    'pay_success_duration': ['p(95)<2000'],
  },
};


export default function () {
  const headers = { 'Content-Type': 'application/json' };

  const memberId = Math.floor(Math.random() * 1000) + 1;

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

  if (!check(res1, { 'Ticket Request 200': (r) => r.status === 200 })) {
    return;
  }

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


  if (res2.status === 200) {
    pay_success_count.add(1);
    pay_success_trend.add(res2.timings.duration);
  }

  else if (res2.status === 409 || (res2.body && res2.body.includes("재고"))) {
    pay_soldout_count.add(1);
    pay_soldout_trend.add(res2.timings.duration);
  }

  else {
    pay_error_count.add(1);
    console.error(`Error Status: ${res2.status}, Body: ${res2.body}`);
  }
}