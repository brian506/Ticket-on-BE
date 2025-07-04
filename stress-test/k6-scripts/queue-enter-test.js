import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  scenarios: {
    spike_load: {
      executor: 'constant-arrival-rate',
      rate: 50,
      timeUnit: '1s',
      duration: '10s', //
      preAllocatedVUs: 100,
      maxVUs: 100,    // ← 더 많은 사용자 할당
    },
  },
};
export default function () {
  const uniqueId = uuidv4();
  const email = `user-${uniqueId}@example.com`;

  const params = {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  };

  const body = `email=${encodeURIComponent(email)}`;

  const res = http.post('http://localhost:8080/v1/api/queues/enter', body, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'contains 대기열 메시지': (r) => r.body.includes('대기열 등록 완료'),
  });
}
