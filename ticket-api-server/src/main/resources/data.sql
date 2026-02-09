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

INSERT INTO event_items (title, start_date, end_date, event_status) VALUES
('2026 Winter Rock Festival', '2026-02-01', '2026-02-03', 'OPEN'),
('Jazz Night in Seoul', '2026-03-10', '2026-03-10', 'OPEN');

INSERT INTO ticket_types (event_id, ticket_type_name, description, max_quantity, issued_quantity, price, status) VALUES
(1, 'Standing A', 'Stage side', 100000, 0, 150000, 'READY');

