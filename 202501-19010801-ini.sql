SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema camping
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `camping`;
CREATE SCHEMA IF NOT EXISTS `camping` DEFAULT CHARACTER SET utf8 ;
USE `camping` ;

DROP TABLE IF EXISTS `camping`.`company` ;
DROP TABLE IF EXISTS `camping`.`camping_car` ;
DROP TABLE IF EXISTS `camping`.`part_inventory` ;
DROP TABLE IF EXISTS `camping`.`staff` ;
DROP TABLE IF EXISTS `camping`.`internal_maintenance` ;
DROP TABLE IF EXISTS `camping`.`customer` ;
DROP TABLE IF EXISTS `camping`.`rental` ;
DROP TABLE IF EXISTS `camping`.`external_center` ;
DROP TABLE IF EXISTS `camping`.`external_maintenance` ;

-- -----------------------------------------------------
-- Table `camping`.`company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`company` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `manager_name` VARCHAR(50) NOT NULL,
  `manager_email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`camping_car`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`camping_car` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `company_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `plate_number` VARCHAR(20) NOT NULL,
  `capacity` INT NOT NULL COMMENT 'Check (capacity > 0)',
  `image` VARCHAR(200) NULL,
  `detail_info` TEXT NULL,
  `rental_price` DECIMAL(10,0) NOT NULL COMMENT 'Check (rent_fee > 0)',
  `registration_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `number_UNIQUE` (`plate_number` ASC) VISIBLE,
  INDEX `fk_company_idx` (`company_id` ASC) VISIBLE,
  CONSTRAINT `fk_compingcar_company_id`
    FOREIGN KEY (`company_id`)
    REFERENCES `camping`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`part_inventory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`part_inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL COMMENT 'Check (part_price >= 0)',
  `quantity` INT NOT NULL DEFAULT 0 COMMENT 'Check (part_quantity >= 0)',
  `received_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `supplier_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`staff`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`staff` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `salary` DECIMAL(10,0) NOT NULL COMMENT 'check (monthly_salary >= 0)',
  `family_num` INT NOT NULL DEFAULT 0,
  `department` VARCHAR(50) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`internal_maintenance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`internal_maintenance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `car_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  `staff_id` BIGINT NOT NULL,
  `repair_date` DATETIME NOT NULL,
  `duration` INT NOT NULL COMMENT 'check (repair_time >= 0)',
  `description` TEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Repair_Records_TB_Camping_Car_TB1_idx` (`car_id` ASC) VISIBLE,
  INDEX `fk_Repair_Records_TB_Part_Inventory_TB1_idx` (`part_id` ASC) VISIBLE,
  INDEX `fk_Repair_Records_TB_Employee_TB1_idx` (`staff_id` ASC) VISIBLE,
  CONSTRAINT `fk_intmaint_car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `camping`.`camping_car` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_intmaint_part_id`
    FOREIGN KEY (`part_id`)
    REFERENCES `camping`.`part_inventory` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_intmaint_staff_id`
    FOREIGN KEY (`staff_id`)
    REFERENCES `camping`.`staff` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `license_number` VARCHAR(20) UNIQUE NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `phone` VARCHAR(15) UNIQUE NOT NULL,
  `email` VARCHAR(100) UNIQUE NOT NULL,
  `prev_return_date` DATETIME NULL,
  `prev_car_type` VARCHAR(45) NULL,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `camping`.`rental`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`rental` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `car_id` BIGINT NOT NULL,
  `customer_id` BIGINT NOT NULL,	
  `license_number` VARCHAR(20) NOT NULL,
  `company_id` BIGINT NOT NULL,
  `start_date` DATETIME NOT NULL,
  `return_date` DATETIME NOT NULL,
  `rental_days` INT NOT NULL COMMENT 'check (rent_period > 0)',
  `rental_fee` DECIMAL(10,2) NOT NULL COMMENT 'check (rent_fee >= 0)',
  `fee_due_date` DATETIME NOT NULL,
  `extra_details` TEXT NULL,
  `extra_fee` DECIMAL(10,2) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Rent_TB_Camping_Car_TB1_idx` (`car_id` ASC) VISIBLE,
  INDEX `fk_company_idx` (`company_id` ASC) VISIBLE,
  INDEX `fk_rental_license_no_idx` (`license_number` ASC) VISIBLE,
  CONSTRAINT `fk_rental_car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `camping`.`camping_car` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rental_company_id`
    FOREIGN KEY (`company_id`)
    REFERENCES `camping`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rental_customer_id`
    FOREIGN KEY (`customer_id`)
    REFERENCES `camping`.`customer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`external_center`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`external_center` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `manager_name` VARCHAR(50) NOT NULL,
  `manager_email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `camping`.`external_maintenance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `camping`.`external_maintenance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `car_id` BIGINT NOT NULL,
  `center_id` BIGINT NOT NULL,
  `customer_id` BIGINT NOT NULL,
  `license_number` VARCHAR(20) NOT NULL,
  `company_id` BIGINT NOT NULL,
  `repair_details` TEXT NOT NULL,
  `repair_date` DATETIME NOT NULL,
  `repair_fee` DECIMAL(10,2) NOT NULL COMMENT 'check (repair_fee >= 0)',
  `fee_due_date` DATE NOT NULL,
  `extra_details` TEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Repair_Info_TB_Camping_Car_TB1_idx` (`car_id` ASC) VISIBLE,
  INDEX `fk_Repair_Info_TB_Client_TB1_idx` (`license_number` ASC) VISIBLE,
  INDEX `fk_company_idx` (`company_id` ASC) VISIBLE,
  CONSTRAINT `fk_extmaint_company_id`
    FOREIGN KEY (`company_id`)
    REFERENCES `camping`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_extmaint_car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `camping`.`camping_car` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_extmaint_customer_id`
    FOREIGN KEY (`customer_id`)
    REFERENCES `camping`.`customer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_extmaint_center_id`
    FOREIGN KEY (`center_id`)
    REFERENCES `camping`.`external_center` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- INSERT sample data
-- -----------------------------------------------------
INSERT INTO customer (username, password, license_number, name, address, phone, email, prev_return_date, prev_car_type) VALUES 
('root', '1234', 'AD2025-1001', '관리자', '서울특별시 광진구 능동로 209', '010-0000-0000', 'admin@example.com', NULL, NULL),
('user1', 'pass1!', 'DL2025-1001', '김민수', '서울특별시 강남구 테헤란로 123', '010-0000-1001', 'user1@example.com', NULL, NULL),
('user2', 'pass2!', 'DL2025-1002', '이하은', '부산광역시 해운대구 해운대로 123', '010-0000-1002', 'user2@example.com', '2025-04-21 00:00:00', 'Type2'),
('user3', 'pass3!', 'DL2025-1003', '박지훈', '대구광역시 수성구 동대구로 123', '010-0000-1003', 'user3@example.com', NULL, NULL),
('user4', 'pass4!', 'DL2025-1004', '정예린', '인천광역시 연수구 송도과학로 123', '010-0000-1004', 'user4@example.com', '2025-04-11 00:00:00', 'Type4'),
('user5', 'pass5!', 'DL2025-1005', '최현우', '광주광역시 서구 상무대로 123', '010-0000-1005', 'user5@example.com', NULL, NULL),
('user6', 'pass6!', 'DL2025-1006', '한서연', '대전광역시 유성구 대학로 123', '010-0000-1006', 'user6@example.com', '2025-04-01 00:00:00', 'Type6'),
('user7', 'pass7!', 'DL2025-1007', '윤도현', '울산광역시 남구 돋질로 123', '010-0000-1007', 'user7@example.com', NULL, NULL),
('user8', 'pass8!', 'DL2025-1008', '배수지', '세종특별자치시 도움1로 123', '010-0000-1008', 'user8@example.com', '2025-03-22 00:00:00', 'Type8'),
('user9', 'pass9!', 'DL2025-1009', '장태산', '경기도 성남시 분당구 정자동 123', '010-0000-1009', 'user9@example.com', NULL, NULL),
('user10', 'pass10!', 'DL2025-1010', '오지은', '경상남도 창원시 의창구 중앙대로 123', '010-0000-1010', 'user10@example.com', '2025-03-12 00:00:00', 'Type10'),
('user11', 'pass11!', 'DL2025-1011', '조윤호', '강원도 춘천시 중앙로 123', '010-0000-1011', 'user11@example.com', NULL, NULL),
('user12', 'pass12!', 'DL2025-1012', '문지아', '전라북도 전주시 완산구 홍산로 123', '010-0000-1012', 'user12@example.com', '2025-03-02 00:00:00', 'Type12');

INSERT INTO external_center (name, address, phone, manager_name, manager_email) VALUES
('서울정비센터', '서울특별시 강서구 공항대로 45길 10', '02-1234-1001', '김재현', 'jaehyun.kim@repaircenter.co.kr'),
('부산오토정비', '부산광역시 사하구 다대로 112', '051-5678-2002', '이수빈', 'subin.lee@repaircenter.co.kr'),
('대구카닥터', '대구광역시 중구 달구벌대로 1234', '053-2345-3003', '박지호', 'jiho.park@repaircenter.co.kr'),
('인천수리공방', '인천광역시 부평구 경원대로 890', '032-3456-4004', '최예린', 'yerin.choi@repaircenter.co.kr'),
('광주정비소', '광주광역시 북구 하서로 210', '062-4567-5005', '한지후', 'jih00.han@repaircenter.co.kr'),
('대전정비마스터', '대전광역시 동구 대전로 56', '042-7890-6006', '정하늘', 'haneul.jung@repaircenter.co.kr'),
('울산카센터', '울산광역시 북구 진장유통로 89', '052-1234-7007', '윤지민', 'jimin.yoon@repaircenter.co.kr'),
('세종오토센터', '세종특별자치시 도움5로 42', '044-2345-8008', '배도윤', 'doyoon.bae@repaircenter.co.kr'),
('경기하이카정비', '경기도 수원시 권선구 권중로 188', '031-3456-9009', '오유진', 'yujin.oh@repaircenter.co.kr'),
('강원모터케어', '강원도 원주시 봉화로 22', '033-4567-1010', '장태현', 'taehyun.jang@repaircenter.co.kr'),
('전주정비월드', '전라북도 전주시 덕진구 백제대로 540', '063-5678-1111', '문채린', 'chaerin.moon@repaircenter.co.kr'),
('창원오토리페어', '경상남도 창원시 마산합포구 해운로 78', '055-6789-1212', '조하윤', 'hayoon.jo@repaircenter.co.kr');

INSERT INTO company (name, address, phone, manager_name, manager_email) VALUES
('서울캠핑렌트', '서울특별시 마포구 독막로 123', '02-123-4567', '김현수', 'hs.kim@seoulcamping.co.kr'),
('부산카라반', '부산광역시 해운대구 해운대로 456', '051-234-5678', '이정민', 'jm.lee@busancaravan.kr'),
('대전오토캠핑', '대전광역시 유성구 계룡로 789', '042-345-6789', '박소영', 'sy.park@djautocamp.co.kr'),
('광주캠핑차대여', '광주광역시 북구 무등로 101', '062-456-7890', '최지훈', 'jh.choi@gjcampingcar.kr'),
('인천캠핑렌트카', '인천광역시 미추홀구 인하로 202', '032-567-8901', '정수진', 'sj.jung@incampingrent.kr'),
('울산캠핑서비스', '울산광역시 남구 번영로 303', '052-678-9012', '한지원', 'jw.han@ulsancamping.kr'),
('경기캠핑차', '경기도 수원시 영통구 광교로 404', '031-789-0123', '류승우', 'sw.ryu@ggcampcar.co.kr'),
('강원렌탈캠핑', '강원도 원주시 중앙로 505', '033-890-1234', '양하나', 'hn.yang@kwrentalcamp.kr'),
('충북카라반렌트', '충청북도 청주시 상당구 상당로 606', '043-901-2345', '백지훈', 'jh.baek@cbcaravan.kr'),
('전남오토캠핑', '전라남도 목포시 하당로 707', '061-012-3456', '서민정', 'mj.seo@jnautocamp.kr'),
('경남캠핑차서비스', '경상남도 진주시 진양호로 808', '055-123-4567', '이태환', 'th.lee@gncamping.kr'),
('제주캠핑렌트카', '제주특별자치도 제주시 연북로 909', '064-234-5678', '고은지', 'ej.ko@jejucampingcar.kr');

INSERT INTO part_inventory (name, price, quantity, received_date, supplier_name) VALUES
('브레이크 패드', 45000.00, 20, '2025-04-01 09:00:00', '오토월드 부품'),
('타이어', 95000.00, 40, '2025-04-02 10:30:00', '대한타이어'),
('배터리', 120000.00, 15, '2025-04-03 11:15:00', '그린에너지 배터리'),
('엔진오일', 25000.00, 60, '2025-04-04 08:45:00', '코리아 윤활유'),
('에어필터', 18000.00, 35, '2025-04-05 14:10:00', '에어클린 코리아'),
('와이퍼 블레이드', 12000.00, 50, '2025-04-06 13:20:00', '비전오토'),
('냉각수', 30000.00, 25, '2025-04-07 16:00:00', '한빛 부품'),
('전조등', 35000.00, 30, '2025-04-08 10:10:00', '라이트존'),
('퓨즈 박스', 28000.00, 10, '2025-04-09 09:30:00', '퓨즈월드'),
('점화 플러그', 15000.00, 45, '2025-04-10 15:00:00', '한국점화'),
('서스펜션', 110000.00, 5, '2025-04-11 11:45:00', '차량부품센터'),
('트랜스미션 오일', 40000.00, 18, '2025-04-12 12:30:00', '오일텍코리아');

INSERT INTO staff (name, phone, address, salary, family_num, department, role) VALUES
('김지훈', '010-1111-0001', '서울특별시 강남구', 3200000, 3, '정비부', '정비'),
('박하늘', '010-1111-0002', '부산광역시 해운대구', 3100000, 2, '관리부', '관리'),
('이서준', '010-1111-0003', '대구광역시 달서구', 3500000, 4, '정비부', '정비'),
('최유진', '010-1111-0004', '인천광역시 남동구', 3300000, 1, '운영팀', '사무'),
('정태영', '010-1111-0005', '광주광역시 북구', 2900000, 2, '정비부', '정비'),
('한예슬', '010-1111-0006', '대전광역시 서구', 3000000, 0, '인사부', '사무'),
('오민석', '010-1111-0007', '울산광역시 중구', 2800000, 1, '정비부', '정비'),
('강지호', '010-1111-0008', '세종특별자치시', 3600000, 3, '기획부', '관리'),
('윤하늘', '010-1111-0009', '경기도 수원시 영통구', 3100000, 2, '운영팀', '사무'),
('문지훈', '010-1111-0010', '강원도 원주시', 2700000, 1, '정비부', '정비'),
('서수빈', '010-1111-0011', '충청북도 청주시', 3200000, 2, '관리부', '관리'),
('배상민', '010-1111-0012', '전라남도 목포시', 3400000, 4, '정비부', '정비');

INSERT INTO camping_car (company_id, name, plate_number, capacity, image, detail_info, rental_price) VALUES
(1, '현대 스타렉스', '11가1234', 4, NULL, '넓은 실내와 싱크대, 침대 포함', 120000),
(2, '기아 카니발', '22나2345', 5, NULL, '침대와 전기설비 완비', 130000),
(3, '현대 포터', '33다3456', 2, NULL, '소형 캠핑 전용 개조차량', 100000),
(4, '기아 봉고', '44라4567', 2, NULL, '접이식 침대와 외부 샤워기 포함', 95000),
(5, '쌍용 렉스턴 스포츠', '55마5678', 3, NULL, '견인력 우수한 트럭형 캠핑카', 140000),
(6, '벤츠 스프린터', '66바6789', 5, NULL, '럭셔리 내부 인테리어, 전기·수도 완비', 200000),
(7, '현대 스타렉스', '77사7890', 4, NULL, '냉장고, 인덕션, 침대 완비', 125000),
(8, '기아 카니발', '88아8901', 4, NULL, '4인용 하이리무진 구성', 135000),
(9, '쉐보레 익스프레스', '99자9012', 6, NULL, '풀사이즈 밴 기반, 미국식 감성', 180000),
(10, '현대 포터', '10차0123', 2, NULL, '최소형 캠핑 장비 포함', 105000),
(11, '기아 레이', '11카1234', 2, NULL, '초소형 경차 캠핑 세팅', 90000),
(12, '르노 마스터', '12타2345', 4, NULL, '중형 밴 기반 넓은 공간 구성', 160000);

INSERT INTO internal_maintenance (car_id, part_id, staff_id, repair_date, duration, description) VALUES
(1, 1, 1, '2025-04-01 10:00:00', 30, '오일 필터 교체'),
(2, 2, 2, '2025-04-03 14:00:00', 90, '배터리 점검 및 교체'),
(3, 3, 3, '2025-04-05 09:30:00', 15, '타이어 공기압 조정'),
(4, 4, 4, '2025-04-07 16:00:00', 120, '브레이크 패드 교체'),
(5, 5, 5, '2025-04-09 11:15:00', 45, '에어컨 필터 청소'),
(6, 6, 6, '2025-04-11 13:00:00', 20, '냉각수 보충'),
(7, 7, 7, '2025-04-13 15:30:00', 40, '내부 조명 점검'),
(8, 8, 8, '2025-04-15 10:45:00', 100, '차량 전기 시스템 점검'),
(9, 9, 9, '2025-04-17 12:00:00', 60, '운전석 시트 고정 수리'),
(10, 10, 10, '2025-04-19 09:00:00', 180, '차량 전체 정기 점검'),
(11, 11, 11, '2025-04-21 14:20:00', 75, '하부 녹 방지 코팅'),
(12, 12, 12, '2025-04-23 11:10:00', 25, '경고등 센서 리셋');

INSERT INTO external_maintenance (car_id, center_id, license_number, customer_id, company_id, repair_details, repair_date, repair_fee, fee_due_date, extra_details) VALUES
(1, 1, 'DL2025-1001', 1, 1, '외부 도색 작업', '2025-03-01 10:00:00', 120000.00, '2025-03-10', '하부 녹 제거 포함'),
(2, 2, 'DL2025-1002', 2, 2, '와이퍼 모터 교체', '2025-03-03 11:30:00', 80000.00, '2025-03-12', NULL),
(3, 3, 'DL2025-1003', 3, 3, '엔진 오일 누유 수리', '2025-03-05 14:15:00', 200000.00, '2025-03-15', '오일필터 교체 포함'),
(4, 4, 'DL2025-1004', 4, 4, '브레이크 라인 수리', '2025-03-07 16:45:00', 150000.00, '2025-03-17', NULL),
(5, 5, 'DL2025-1005', 5, 5, '배터리 교체 및 테스트', '2025-03-09 09:00:00', 110000.00, '2025-03-19', '고속 충전 테스트 포함'),
(6, 6, 'DL2025-1006', 6, 6, '스프링 서스펜션 교체', '2025-03-11 13:20:00', 175000.00, '2025-03-21', NULL),
(7, 7, 'DL2025-1007', 7, 7, '차량 하부 부식 보수', '2025-03-13 15:00:00', 220000.00, '2025-03-23', '언더코팅 포함'),
(8, 8, 'DL2025-1008', 8, 8, '냉각수 라인 교체', '2025-03-15 10:30:00', 95000.00, '2025-03-25', NULL),
(9, 9, 'DL2025-1009', 9, 9, '차량 휠 얼라인먼트 조정', '2025-03-17 11:50:00', 60000.00, '2025-03-27', NULL),
(10, 10, 'DL2025-1010', 10, 10, '연료 필터 교체', '2025-03-19 14:10:00', 70000.00, '2025-03-29', NULL),
(11, 11, 'DL2025-1011', 11, 11, '차량 외부 세차 및 광택', '2025-03-21 16:40:00', 50000.00, '2025-03-31', '내부 클리닝 포함'),
(12, 12, 'DL2025-1012', 12, 12, '도어 잠금장치 고장 수리', '2025-03-23 09:40:00', 85000.00, '2025-04-02', NULL);

INSERT INTO rental (car_id, license_number, customer_id, company_id, start_date, return_date, rental_days, rental_fee, fee_due_date, extra_details, extra_fee) VALUES
(1, 'DL2025-1001', 1, 1, '2025-03-01 10:00:00', '2025-03-05 10:00:00', 4, 400000.00, '2025-03-05 12:00:00', NULL, NULL),
(2, 'DL2025-1002', 2, 2, '2025-03-03 09:00:00', '2025-03-06 09:00:00', 3, 300000.00, '2025-03-06 12:00:00', '추가 보험료', 20000.00),
(3, 'DL2025-1003', 3, 3, '2025-03-05 15:00:00', '2025-03-09 15:00:00', 4, 360000.00, '2025-03-09 18:00:00', NULL, NULL),
(4, 'DL2025-1004', 4, 4, '2025-03-07 12:00:00', '2025-03-09 12:00:00', 2, 220000.00, '2025-03-09 15:00:00', NULL, NULL),
(5, 'DL2025-1005', 5, 5, '2025-03-09 11:00:00', '2025-03-14 11:00:00', 5, 500000.00, '2025-03-14 12:00:00', '청소비 포함', 10000.00),
(6, 'DL2025-1006', 6, 6, '2025-03-11 08:00:00', '2025-03-13 08:00:00', 2, 200000.00, '2025-03-13 10:00:00', NULL, NULL),
(7, 'DL2025-1007', 7, 7, '2025-03-13 10:00:00', '2025-03-16 10:00:00', 3, 330000.00, '2025-03-16 11:00:00', NULL, NULL),
(8, 'DL2025-1008', 8, 8, '2025-03-15 13:00:00', '2025-03-18 13:00:00', 3, 360000.00, '2025-03-18 14:00:00', NULL, NULL),
(9, 'DL2025-1009', 9, 9, '2025-03-17 09:30:00', '2025-03-20 09:30:00', 3, 300000.00, '2025-03-20 11:00:00', NULL, NULL),
(10, 'DL2025-1010', 10, 10, '2025-03-19 17:00:00', '2025-03-22 17:00:00', 3, 270000.00, '2025-03-22 20:00:00', NULL, NULL),
(11, 'DL2025-1011', 11, 11, '2025-03-21 14:00:00', '2025-03-24 14:00:00', 3, 330000.00, '2025-03-24 16:00:00', '아이용 카시트 포함', 15000.00),
(12, 'DL2025-1012', 12, 12, '2025-03-23 10:00:00', '2025-03-25 10:00:00', 2, 220000.00, '2025-03-25 12:00:00', NULL, NULL);
