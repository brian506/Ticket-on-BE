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


INSERT INTO tickets (ticket_type_id, member_id, status, price, order_id, expired_at)
SELECT
    1,                              -- ticket_type_id (고정)
    m.member_id,                    -- member_id (실제 생성된 회원 ID 사용)
    'PENDING',                      -- status
    150000,                         -- price
    CONCAT('ORD-', m.member_id),    -- order_id (고유하게 생성)
    DATE_SUB(NOW(), INTERVAL 1 DAY) -- expired_at
FROM members m;

INSERT INTO event_items (title, start_date, end_date, event_status) VALUES
('2026 Winter Rock Festival', '2026-02-01', '2026-02-03', 'OPEN'),
('Jazz Night in Seoul', '2026-03-10', '2026-03-10', 'OPEN');

INSERT INTO ticket_types (event_id, ticket_type_name, description, max_quantity, issued_quantity, price, status) VALUES
(1, 'Standing A', 'Stage side', 1000000, 0, 150000, 'READY');

