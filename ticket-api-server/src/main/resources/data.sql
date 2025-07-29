
SET FOREIGN_KEY_CHECKS = 0; -- 외래 키 제약 조건 비활성화
TRUNCATE TABLE payment;
TRUNCATE TABLE tickets;
TRUNCATE TABLE ticket_types;
TRUNCATE TABLE members;
TRUNCATE TABLE event_items;
SET FOREIGN_KEY_CHECKS = 1; -- 외래 키 제약 조건 활성화

ALTER TABLE event_items AUTO_INCREMENT = 1;
ALTER TABLE members AUTO_INCREMENT = 1;
ALTER TABLE ticket_types AUTO_INCREMENT = 1;
ALTER TABLE tickets AUTO_INCREMENT = 1;
ALTER TABLE payment AUTO_INCREMENT = 1;

INSERT INTO event_items (title, start_date, end_date, event_status)
VALUES ('싸이의 흠뻑쇼', CURDATE(), CURDATE(), 'OPEN');

INSERT INTO ticket_types (event_id, ticket_type_name, description, price, max_quantity, issued_quantity, status)
VALUES (1, '테스트 티켓','재밌어요', 1000, 100, 0, 'READY');

INSERT INTO members (email, password) VALUES
('user1@test.com', 'password'), ('user2@test.com', 'password'), ('user3@test.com', 'password'), ('user4@test.com', 'password'), ('user5@test.com', 'password'),
('user6@test.com', 'password'), ('user7@test.com', 'password'), ('user8@test.com', 'password'), ('user9@test.com', 'password'), ('user10@test.com', 'password'),
('user11@test.com', 'password'), ('user12@test.com', 'password'), ('user13@test.com', 'password'), ('user14@test.com', 'password'), ('user15@test.com', 'password'),
('user16@test.com', 'password'), ('user17@test.com', 'password'), ('user18@test.com', 'password'), ('user19@test.com', 'password'), ('user20@test.com', 'password'),
('user21@test.com', 'password'), ('user22@test.com', 'password'), ('user23@test.com', 'password'), ('user24@test.com', 'password'), ('user25@test.com', 'password'),
('user26@test.com', 'password'), ('user27@test.com', 'password'), ('user28@test.com', 'password'), ('user29@test.com', 'password'), ('user30@test.com', 'password'),
('user31@test.com', 'password'), ('user32@test.com', 'password'), ('user33@test.com', 'password'), ('user34@test.com', 'password'), ('user35@test.com', 'password'),
('user36@test.com', 'password'), ('user37@test.com', 'password'), ('user38@test.com', 'password'), ('user39@test.com', 'password'), ('user40@test.com', 'password'),
('user41@test.com', 'password'), ('user42@test.com', 'password'), ('user43@test.com', 'password'), ('user44@test.com', 'password'), ('user45@test.com', 'password'),
('user46@test.com', 'password'), ('user47@test.com', 'password'), ('user48@test.com', 'password'), ('user49@test.com', 'password'), ('user50@test.com', 'password'),
('user51@test.com', 'password'), ('user52@test.com', 'password'), ('user53@test.com', 'password'), ('user54@test.com', 'password'), ('user55@test.com', 'password'),
('user56@test.com', 'password'), ('user57@test.com', 'password'), ('user58@test.com', 'password'), ('user59@test.com', 'password'), ('user60@test.com', 'password'),
('user61@test.com', 'password'), ('user62@test.com', 'password'), ('user63@test.com', 'password'), ('user64@test.com', 'password'), ('user65@test.com', 'password'),
('user66@test.com', 'password'), ('user67@test.com', 'password'), ('user68@test.com', 'password'), ('user69@test.com', 'password'), ('user70@test.com', 'password'),
('user71@test.com', 'password'), ('user72@test.com', 'password'), ('user73@test.com', 'password'), ('user74@test.com', 'password'), ('user75@test.com', 'password'),
('user76@test.com', 'password'), ('user77@test.com', 'password'), ('user78@test.com', 'password'), ('user79@test.com', 'password'), ('user80@test.com', 'password'),
('user81@test.com', 'password'), ('user82@test.com', 'password'), ('user83@test.com', 'password'), ('user84@test.com', 'password'), ('user85@test.com', 'password'),
('user86@test.com', 'password'), ('user87@test.com', 'password'), ('user88@test.com', 'password'), ('user89@test.com', 'password'), ('user90@test.com', 'password'),
('user91@test.com', 'password'), ('user92@test.com', 'password'), ('user93@test.com', 'password'), ('user94@test.com', 'password'), ('user95@test.com', 'password'),
('user96@test.com', 'password'), ('user97@test.com', 'password'), ('user98@test.com', 'password'), ('user99@test.com', 'password'), ('user100@test.com', 'password');
