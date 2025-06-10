import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '1m', target: 50 },   // 1분 동안 50명까지 증가
    { duration: '2m', target: 50 },   // 2분 동안 50명 유지
    { duration: '1m', target: 0 },    // 1분 동안 0으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% 요청이 500ms보다 짧아야 함
    http_req_failed: ['rate<0.01'],   // 실패율이 1% 미만
  },
};

export default function () {
  const res = http.get('http://localhost:8080/tickets');
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(1);
}
