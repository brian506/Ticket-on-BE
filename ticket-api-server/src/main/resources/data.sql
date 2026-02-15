-- 1. 이벤트 생성 (최상위 부모)
INSERT INTO event_items (title, start_date, end_date, event_status) VALUES
('2026 Winter Rock Festival', '2026-02-01', '2026-02-03', 'OPEN'),
('Jazz Night in Seoul', '2026-03-10', '2026-03-10', 'OPEN');

-- 2. 티켓 타입 생성 (이벤트가 있어야 생성 가능)
-- 여기서 ticket_type_id = 1 인 데이터가 생성됩니다.
INSERT INTO ticket_types (event_id, ticket_type_name, description, max_quantity, issued_quantity, price, status) VALUES
(1, 'Standing A', 'Stage side', 1000000, 0, 150000, 'READY');

-- 3. 회원 생성 (독립적)
INSERT INTO members (email, password)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000
)
SELECT
    CONCAT('user', n, '@test.com'),
    'pass1234'
FROM seq;

-- 4. 티켓 생성 (회원과 티켓 타입이 모두 있어야 생성 가능)
-- 이제 ticket_type_id = 1 이 존재하므로 에러가 나지 않습니다.
INSERT INTO tickets (ticket_type_id, member_id, status, price, order_id, expired_at)
SELECT
    1,                              -- ticket_type_id (위에서 만든 1번 타입)
    m.member_id,                    -- member_id (위에서 만든 회원들)
    'PENDING',                      -- status
    150000,                         -- price
    CONCAT('ORD-', m.member_id),    -- order_id
    DATE_SUB(NOW(), INTERVAL 1 DAY) -- expired_at
FROM members m;