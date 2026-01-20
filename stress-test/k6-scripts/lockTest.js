import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export let options = {
    // 1. 부하 프로필: 10초 동안 100명까지 급격히 증가 후 20초간 유지
    stages: [
        { duration: '10s', target: 100 },
        { duration: '20s', target: 100 },
        { duration: '5s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청은 500ms 이내에 완료되어야 함
    },
};

export default function () {
    // 도커 내부 포트 8081로 전송 (docker-compose 매핑 기준)
    const url = 'http://host.docker.internal:8081/api/v1/payments/confirm';
    const uniqueId = uuidv4();

    const payload = JSON.stringify({
        paymentKey: `test-key-${uniqueId}`,
        orderId: `ORD-${uniqueId}`,
        amount: 150000,
        ticketTypeId: 1, // 더미 데이터의 'Standing A' (max: 100개)
        memberId: Math.floor(Math.random() * 10) + 1, // 더미 데이터의 1~10번 회원 활용
    });

    const headers = { 'Content-Type': 'application/json' };

    const res = http.post(url, payload, { headers });

    // 2. 체크 로직 강화
    check(res, {
        '응답 성공(200/201)': (r) => r.status === 200 || r.status === 201,
        '매진 또는 성공 메시지 확인': (r) =>
            r.body.includes("성공") ||
            r.body.includes("매진") ||
            r.body.includes("Exceeded"),
    });

    // 티켓팅 상황처럼 매우 빠르게 요청을 보내기 위해 sleep을 짧게 설정
    sleep(0.1);
}