import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// 쿠키 목록 로드
const sessionCookies = new SharedArray('sessionCookies', function () {
  return JSON.parse(open('./cookies.json'));
});

export const options = {
  scenarios: {
    queue_enter_test: {
      executor: 'constant-arrival-rate',
      rate: 50,          // 초당 요청 수 50
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 100,
      maxVUs: 400,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<5000'], // 5초로 상향 조정 (임시)
  },
};

export default function () {
  const vuId = (__VU - 1) % sessionCookies.length;
  const cookie = sessionCookies[vuId];
  const headers = {
    'Content-Type': 'application/json',
    'Cookie': cookie,
  };

  // 빈 JSON 바디 넣기
  const res = http.post('http://localhost:8081/v1/api/queues/enter', '{}', { headers });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'contains 대기열 메시지': (r) => r.body.includes('대기열 등록 완료'),
  });

  sleep(1);
}
