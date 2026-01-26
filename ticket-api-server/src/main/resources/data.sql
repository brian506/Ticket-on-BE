
INSERT INTO members (email, password)
SELECT
    CONCAT('tester', n, '@example.com'),
    'password123'
FROM (
    -- a(1), b(10), c(100), d(1000) 자릿수를 조합
    SELECT (a.N + b.N * 10 + c.N * 100 + d.N * 1000) AS n
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS a
    CROSS JOIN (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS b
) AS numbers
WHERE n BETWEEN 1 AND 100;

-- 2. EventItem (event_status 컬럼 사용, CANCELED 철자 확인)
INSERT INTO event_items (title, start_date, end_date, event_status) VALUES
('2026 Winter Rock Festival', '2026-02-01', '2026-02-03', 'OPEN'),
('Jazz Night in Seoul', '2026-03-10', '2026-03-10', 'OPEN');

-- 3. TicketType (ticket_type_name 컬럼 사용)
INSERT INTO ticket_types (event_id, ticket_type_name, description, max_quantity, issued_quantity, price, status) VALUES
(1, 'Standing A', 'Stage side', 100, 0, 150000, 'READY');

