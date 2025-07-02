-- 1. EventItem 삽입
INSERT INTO event_items (title, start_date, end_date)
VALUES ('2025 싸이의 흠뻑쇼', '2025-07-01', '2025-07-03');

-- 삽입된 ID를 저장 (이건 MySQL 클라이언트나 애플리케이션에서 처리)
-- SET @event_item_id = LAST_INSERT_ID(); 를 사용하거나, 수동으로 ID를 지정하세요.

-- 예를 들어, 위에서 생성된 event_item_id가 1이라고 가정
SET @event_item_id = LAST_INSERT_ID();

-- 2. TicketType 삽입
INSERT INTO ticket_types (event_id, ticket_type_name, description, max_quantity, issued_quantity, price, status)
VALUES
(@event_item_id, '스탠딩석', '무대 앞에서 즐길 수 있는 자리', 200, 0, 110000, 'ON_SALE'),
(@event_item_id, '그린석', '잔디밭에서 여유롭게 관람', 200, 0, 90000, 'ON_SALE'),
(@event_item_id, 'VIP석', '가장 좋은 자리에서 관람', 100, 0, 150000, 'ON_SALE');