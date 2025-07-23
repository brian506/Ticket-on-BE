import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  scenarios: {
    spike_load: {
      executor: 'constant-arrival-rate',
      rate: 50,
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 100,
      maxVUs: 100,
    },
  },
};

export default function () {
  const uniqueId = uuidv4();
  const email = `user-${uniqueId}@example.com`;

  const headers = {
    'Content-Type': 'application/json',
  };

  const body = JSON.stringify({ email });

  const res = http.post('http://localhost:8082/v1/api/queues/enter', body, { headers });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'contains 요청 수신 완료': (r) => r.body.includes('요청 수신 완료'),
  });
}
