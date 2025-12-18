-- ========================================
-- SEMO 프로젝트 초기 데이터
-- ========================================

-- 1. Role 초기 데이터
INSERT INTO role (name) VALUES ('ROLE_USER');     -- 일반 사용자 (임차인)
INSERT INTO role (name) VALUES ('ROLE_OWNER');    -- 창고 주인
INSERT INTO role (name) VALUES ('ROLE_ADMIN');    -- 관리자

-- 2. 테스트 회원 데이터
-- 비밀번호: 1234 (모두 동일, BCrypt 인코딩됨)
INSERT INTO member (email, password, name, phone_number, created_at, updated_at)
VALUES
    ('user@test.com', '$2a$10$X5wFuQoXe5LJr1zzPbD1ReJNaLvGMxeKOkMpZhVqKzkYbeOB4Bpdi', '일반사용자', '010-1111-1111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('owner@test.com', '$2a$10$X5wFuQoXe5LJr1zzPbD1ReJNaLvGMxeKOkMpZhVqKzkYbeOB4Bpdi', '창고주인', '010-2222-2222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('admin@test.com', '$2a$10$X5wFuQoXe5LJr1zzPbD1ReJNaLvGMxeKOkMpZhVqKzkYbeOB4Bpdi', '관리자', '010-3333-3333', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 회원-역할 매핑 (member_role)
-- user@test.com (id=1) -> ROLE_USER
INSERT INTO member_role (member_id, role_id) VALUES (1, 1);

-- owner@test.com (id=2) -> ROLE_OWNER, ROLE_USER
INSERT INTO member_role (member_id, role_id) VALUES (2, 1);
INSERT INTO member_role (member_id, role_id) VALUES (2, 2);

-- admin@test.com (id=3) -> ROLE_ADMIN, ROLE_USER, ROLE_OWNER
INSERT INTO member_role (member_id, role_id) VALUES (3, 1);
INSERT INTO member_role (member_id, role_id) VALUES (3, 2);
INSERT INTO member_role (member_id, role_id) VALUES (3, 3);

-- 4. 테스트 임차인 프로필 (tenant_profile)
INSERT INTO tenant_profile (member_id, address, detailed_address, purpose, required_size_code, created_at, updated_at)
VALUES
    (1, '서울특별시 강남구 테헤란로 123', '101호', 'STORAGE', 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. 테스트 창고 주인 프로필 (owner_profile)
INSERT INTO owner_profile (member_id, business_registration_number, company_name, ceo_name, office_phone_number, created_at, updated_at)
VALUES
    (2, '123-45-67890', '테스트창고(주)', '홍길동', '02-1234-5678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '987-65-43210', '관리자창고(주)', '김관리', '02-9876-5432', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. 테스트 창고 데이터 (warehouse)
INSERT INTO warehouse (owner_id, title, description, address, area_sqm, price_per_month, available_status, latitude, longitude, created_at, updated_at)
VALUES
    (2, '강남 프리미엄 창고', '강남역 인근 깨끗하고 안전한 창고입니다.', '서울특별시 강남구 강남대로 456', 50.00, 500000, TRUE, 37.4979, 127.0276, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '판교 대형 창고', '판교 테크노밸리 근처 대형 창고', '경기도 성남시 분당구 판교역로 234', 100.00, 800000, TRUE, 37.3952, 127.1109, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '서초 소형 창고', '서초역 근처 개인용 소형 창고', '서울특별시 서초구 서초대로 789', 20.00, 300000, FALSE, 37.4836, 127.0327, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. 테스트 계약 데이터 (contract)
INSERT INTO contract (user_id, warehouse_id, start_date, end_date, total_price, contract_status, created_at)
VALUES
    (1, 1, '2025-01-01', '2025-12-31', 6000000, 'ACTIVE', CURRENT_TIMESTAMP);

-- 8. 테스트 결제 데이터 (payment)
INSERT INTO payment (contract_id, amount, payment_method, payment_status, paid_at)
VALUES
    (1, 6000000, 'CREDIT_CARD', 'PAID', CURRENT_TIMESTAMP);

-- 9. 테스트 리뷰 데이터 (review)
INSERT INTO review (user_id, warehouse_id, rating, comment, created_at, updated_at)
VALUES
    (1, 1, 5, '깨끗하고 관리가 잘 되어 있습니다. 강남역에서 가까워 접근성도 좋아요!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, 4, '넓고 좋은데 가격이 조금 비싼 편입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
