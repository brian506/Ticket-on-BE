const axios = require('axios').default;
const fs = require('fs');

const BASE_URL = 'http://localhost:8081';
const TOTAL_USERS = 100;

function generateUser(index) {
    return {
        email: `user${index}@example.com`,
        password: '1234',
    };
}

(async () => {
    const cookiesList = [];

    for (let i = 1; i <= TOTAL_USERS; i++) {
        const user = generateUser(i);

        try {
            // 로그인 요청 (폼 로그인)
            const loginRes = await axios.post(
                `${BASE_URL}/login`,
                new URLSearchParams({
                    username: user.email,
                    password: user.password,
                }).toString(),
                {
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    maxRedirects: 0,
                    validateStatus: (status) => status === 302 || status === 200,
                }
            );

            // Set-Cookie 헤더에서 모든 쿠키 추출 후 name=value 형태만 뽑아서 배열로 만듦
            const setCookie = loginRes.headers['set-cookie'];

            if (setCookie && setCookie.length > 0) {
                const cookies = setCookie.map(cookieStr => cookieStr.split(';')[0]); // ["JSESSIONID=xxx", "grafana_session=yyy", ...]
                const cookieHeader = cookies.join('; '); // "JSESSIONID=xxx; grafana_session=yyy; ..."
                cookiesList.push(cookieHeader);
            } else {
                console.warn(`⚠️ user${i} - Set-Cookie 없음`);
            }

            if (i % 50 === 0) console.log(`✅ 로그인 성공: user${i}`);

        } catch (error) {
            console.error(`❌ 로그인 실패: user${i}`, error.response?.status, error.response?.data || error.message);
        }
    }

    // 결과 저장
    fs.writeFileSync('cookies.json', JSON.stringify(cookiesList, null, 2));
    console.log('🍪 모든 쿠키 저장 완료 → cookies.json');
})();
