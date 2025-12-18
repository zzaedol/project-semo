-- ========================================
-- SEMO 프로젝트 DB 스키마 (H2 호환)
-- ========================================

-- 1. member 테이블 (기본 회원 정보)
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID (PK)',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '로그인 ID, 이메일 주소 (중복 불가)',
    password VARCHAR(255) NOT NULL COMMENT 'Spring Security로 암호화된 비밀번호',
    name VARCHAR(100) NOT NULL COMMENT '사용자 이름',
    phone_number VARCHAR(20) COMMENT '전화번호',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '정보 수정일'
);

COMMENT ON TABLE member IS '서비스 사용자 및 창고 주인의 기본 정보';

-- 2. role 테이블 (권한 정보)
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '역할 ID (PK)',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '역할명 (예: ROLE_USER, ROLE_OWNER)'
);

COMMENT ON TABLE role IS '사용자 역할 정보 테이블. Spring Security의 권한으로 사용됩니다.';

-- 3. owner_profile 테이블 (창고 주인 상세 정보)
CREATE TABLE owner_profile (
    member_id BIGINT PRIMARY KEY COMMENT '회원 ID (member 테이블의 PK를 참조하는 FK)',
    business_registration_number VARCHAR(20) NOT NULL UNIQUE COMMENT '사업자번호 (중복 불가)',
    company_name VARCHAR(255) COMMENT '사업자(법인)명',
    ceo_name VARCHAR(100) COMMENT '대표자 이름',
    office_phone_number VARCHAR(20) COMMENT '사무실 전화번호',
    business_file_path VARCHAR(255) COMMENT '사업자 등록증 파일 경로',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '정보 수정일',
    CONSTRAINT fk_owner_profile_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

COMMENT ON TABLE owner_profile IS '창고 주인 상세 정보 (회원 정보와 분리)';

-- 4. tenant_profile 테이블 (임차인 상세 정보)
CREATE TABLE tenant_profile (
    member_id BIGINT PRIMARY KEY COMMENT '회원 ID (member 테이블의 PK를 참조하는 FK)',
    address VARCHAR(500) COMMENT '기본 주소',
    detailed_address VARCHAR(255) COMMENT '상세 주소',
    purpose VARCHAR(50) COMMENT '창고 이용 목적 코드',
    required_size_code VARCHAR(10) COMMENT '필요 공간 크기 코드',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '정보 수정일',
    CONSTRAINT fk_tenant_profile_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

COMMENT ON TABLE tenant_profile IS '임차인(일반 사용자)의 추가 프로필 정보';

-- 5. member_role 테이블 (회원-역할 매핑)
CREATE TABLE member_role (
    member_id BIGINT NOT NULL COMMENT '회원 ID (member 테이블의 PK를 참조하는 FK)',
    role_id BIGINT NOT NULL COMMENT '역할 ID (role 테이블의 PK를 참조하는 FK)',
    PRIMARY KEY (member_id, role_id),
    CONSTRAINT fk_member_role_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_member_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

COMMENT ON TABLE member_role IS '회원과 역할 간의 관계 테이블. 회원이 여러 역할을 가질 수 있습니다.';

-- 6. warehouse 테이블 (창고 정보)
CREATE TABLE warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '창고 ID (PK)',
    owner_id BIGINT NOT NULL COMMENT '창고를 등록한 사용자 (창고 주인) ID',
    title VARCHAR(255) COMMENT '창고명 또는 제목',
    description TEXT COMMENT '창고에 대한 상세 설명',
    address VARCHAR(500) NOT NULL COMMENT '창고 주소',
    area_sqm DECIMAL(10, 2) COMMENT '면적 (제곱미터)',
    price_per_month DECIMAL(10, 2) COMMENT '월별 가격',
    available_status BOOLEAN NOT NULL COMMENT '현재 임대 가능 여부 (true/false)',
    latitude DOUBLE COMMENT '위도',
    longitude DOUBLE COMMENT '경도',
    image_path VARCHAR(255) COMMENT '이미지 경로',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '창고 등록일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '창고 정보 수정일',
    CONSTRAINT fk_warehouse_owner FOREIGN KEY (owner_id) REFERENCES member(id) ON DELETE CASCADE
);

COMMENT ON TABLE warehouse IS '창고 등록 정보';

-- 7. contract 테이블 (계약 정보)
CREATE TABLE contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '계약 ID (PK)',
    user_id BIGINT NOT NULL COMMENT '계약을 체결한 사용자 ID',
    warehouse_id BIGINT NOT NULL COMMENT '계약된 창고 ID',
    start_date DATE NOT NULL COMMENT '계약 시작일',
    end_date DATE NOT NULL COMMENT '계약 종료일',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '총 결제 금액',
    contract_status VARCHAR(50) NOT NULL COMMENT '계약 상태 (예: PENDING, ACTIVE, COMPLETED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '계약 생성일',
    CONSTRAINT fk_contract_user FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_contract_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id) ON DELETE CASCADE
);

COMMENT ON TABLE contract IS '창고 임대 계약 정보';

-- 8. payment 테이블 (결제 정보)
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결제 ID (PK)',
    contract_id BIGINT NOT NULL UNIQUE COMMENT '결제가 연결된 계약 ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '실제 결제 금액',
    payment_method VARCHAR(50) COMMENT '결제 수단',
    payment_status VARCHAR(50) NOT NULL COMMENT '결제 상태 (예: PENDING, PAID, CANCELED)',
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '결제 완료 시간',
    CONSTRAINT fk_payment_contract FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE CASCADE
);

COMMENT ON TABLE payment IS '결제 내역 정보';

-- 9. review 테이블 (리뷰 정보)
CREATE TABLE review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '리뷰 ID (PK)',
    user_id BIGINT NOT NULL COMMENT '리뷰를 작성한 사용자 ID',
    warehouse_id BIGINT NOT NULL COMMENT '리뷰 대상이 된 창고 ID',
    rating INT NOT NULL COMMENT '사용자가 남긴 평점 (1~5)',
    comment TEXT COMMENT '사용자가 작성한 리뷰 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '리뷰 작성일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '리뷰 수정일',
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
);

COMMENT ON TABLE review IS '창고에 대한 리뷰 및 평점 정보';

-- 10. term 테이블 (약관 정보) - Entity 파일에서 확인됨
CREATE TABLE IF NOT EXISTS term (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    version VARCHAR(50),
    effective_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE term IS '서비스 약관 정보';
