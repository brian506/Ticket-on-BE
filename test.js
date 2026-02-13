import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Trend } from 'k6/metrics';


const BASE_URL = 'http://3.26.171.218:8081';

const flow_success = new Counter('flow_success');
const req_duration = new Trend('req_duration');
const pay_duration = new Trend('pay_duration');

export const options = {
  scenarios: {
    full_flow_test: {
      executor: 'constant-arrival-rate',
      rate: 100,
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 1000,
      maxVUs: 5000,
    },
  },
  thresholds: {
    'flow_success': ['count>0'],
    'pay_duration': ['p(95)<1000'],
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