import http from 'k6/http';
import { check,sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';  // UUID 라이브러리


export let options = {
    vus : 1,
    duration : '15s',
};

export default function(){
   const url = 'http://host.docker.internal:8081/v1/api/payments/confirm'
   const uniqueId = uuidv4();

   const payload = JSON.stringify({
     paymentKey: "test-key",
     orderId: uniqueId,
     amount: 10000,
     ticketTypeId: 1,
     memberId: Math.floor(Math.random() * 100) + 1,
   });

   const headers = {'Content-Type': 'application/json'};

   const res = http.post(url,payload,{headers});

   check(res, {
    'is status 200': (r) => r.status === 200 || r.status === 201,
    '응답 메시지 확인': (r) => r.body.includes("잔여 수량") || r.body.includes("성공"),
   });

   sleep(1);
}