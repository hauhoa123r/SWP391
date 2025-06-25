DROP DATABASE IF EXISTS swp391;
CREATE DATABASE swp391;
USE swp391;

CREATE TABLE `users`
(
    `user_id`            BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_role`          ENUM ('ADMIN', 'PATIENT', 'STAFF') DEFAULT 'PATIENT',
    `phone_number`       VARCHAR(255),
    `email`              VARCHAR(255),
    `password_hash`      VARCHAR(255),
    `user_status`        ENUM ('ACTIVE', 'INACTIVE')        DEFAULT 'ACTIVE',
    `verification_token` VARCHAR(255),
    `is_verified`        BOOLEAN,
    `two_factor_enabled` BOOLEAN
);

CREATE TABLE `patients`
(
    `patient_id`   BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_id`      BIGINT,
    `phone_number` VARCHAR(255),
    `email`        VARCHAR(255),
    `full_name`    VARCHAR(255),
    `avatar_url`   VARCHAR(255),
    `relationship` ENUM ('SELF', 'FATHER', 'MOTHER', 'BROTHER', 'SISTER', 'DAUGHTER', 'SON', 'GRAND_FATHER', 'GRAND_MOTHER', 'UNCLE', 'AUNT', 'CAUSIN', 'OTHER') DEFAULT 'SELF',
    `address`      VARCHAR(255),
    `gender`       ENUM ('MALE', 'FEMALE', 'OTHER')                                                                                                              DEFAULT 'OTHER',
    `birthdate`    DATE,
    `blood_type`   ENUM ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `hospitals`
(
    `hospital_id`  BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `name`         VARCHAR(255),
    `address`      VARCHAR(255),
    `phone_number` VARCHAR(255),
    `email`        VARCHAR(255),
    `avatar_url`   VARCHAR(255)
);

CREATE TABLE `departments`
(
    `department_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `name`          VARCHAR(255),
    `description`   TEXT,
    `video_url`     VARCHAR(255),
    `banner_url`    VARCHAR(255),
    `slogan`        VARCHAR(255)
);

CREATE TABLE `staffs`
(
    `staff_id`      BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_id`       BIGINT UNIQUE,
    `staff_role`    ENUM ('DOCTOR', 'TECHNICIAN', 'SCHEDULING_COORDINATOR', 'PHARMACIST', 'INVENTORY_MANAGER') DEFAULT 'DOCTOR',
    `manager_id`    BIGINT,
    `department_id` BIGINT,
    `full_name`     VARCHAR(255),
    `avatar_url`    VARCHAR(255),
    `hire_date`     DATE,
    `rank_level`    INT                                                                                        DEFAULT 1,
    `staff_type`    ENUM ('PART_TIME_CONTRACT', 'INTERN', 'CONSULTANT', 'FULL_TIME'),
    `hospital_id`   BIGINT,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`manager_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`hospital_id`) REFERENCES `hospitals` (`hospital_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_qualifications`
(
    `staff_qualification_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`               BIGINT,
    `qualification_type`     ENUM ('DEGREE', 'CERTIFICATE', 'LICENSE', 'AWARD'),
    `title`                  VARCHAR(255),
    `description`            TEXT,
    `issue_date`             DATE,
    `expiration_date`        DATE,
    `url`                    VARCHAR(255),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_skills`
(
    `staff_skill_id`         BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`               BIGINT,
    `name`                   VARCHAR(255),
    `proficiency_percentage` DECIMAL(5, 2),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_specialities`
(
    `staff_speciality_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`            BIGINT,
    `title`               VARCHAR(255),
    `description`         TEXT,
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_educations`
(
    `staff_education_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`           BIGINT,
    `year`               INT,
    `degree`             VARCHAR(255),
    `institute`          VARCHAR(255),
    `result`             VARCHAR(255),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_shifts`
(
    `staff_shift_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`       BIGINT,
    `date`           DATE,
    `shift_type`     ENUM ('MORNING', 'AFTERNOON', 'NIGHT'),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_schedules`
(
    `staff_schedule_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`          BIGINT,
    `available_date`    DATE,
    `start_time`        TIMESTAMP,
    `end_time`          TIMESTAMP,
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_experiences`
(
    `staff_experience_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `staff_id`            BIGINT,
    `year`                INT,
    `department`          VARCHAR(255),
    `position`            VARCHAR(255),
    `hospital`            VARCHAR(255),
    `result`              VARCHAR(255),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `doctors`
(
    `doctor_id`   BIGINT UNIQUE PRIMARY KEY,
    `doctor_rank` ENUM (
        'INTERN',
        'RESIDENT',
        'ATTENDING',
        'SPECIALIST',
        'SENIOR_SPECIALIST',
        'CONSULTANT',
        'CHIEF_PHYSICIAN'
        ) DEFAULT 'INTERN',
    FOREIGN KEY (`doctor_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `technicians`
(
    `technician_id`   BIGINT UNIQUE PRIMARY KEY,
    `technician_rank` ENUM (
        'JUNIOR_TECHNICIAN',
        'TECHNICIAN',
        'SENIOR_TECHNICIAN',
        'LEAD_TECHNICIAN',
        'TECHNICAL_SUPERVISOR'
        ) DEFAULT 'JUNIOR_TECHNICIAN',
    FOREIGN KEY (`technician_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `pharmacists`
(
    `pharmacist_id`   BIGINT UNIQUE PRIMARY KEY,
    `pharmacist_rank` ENUM (
        'PHARMACY_INTERN',
        'STAFF_PHARMACIST',
        'CLINICAL_PHARMACIST',
        'SENIOR_PHARMACIST',
        'PHARMACY_MANAGER',
        'CHIEF_PHARMACIST'
        ) DEFAULT 'PHARMACY_INTERN',
    FOREIGN KEY (`pharmacist_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `inventory_managers`
(
    `inventory_manager_id`   BIGINT UNIQUE PRIMARY KEY,
    `inventory_manager_rank` ENUM (
        'INVENTORY_ASSISTANT',
        'INVENTORY_SPECIALIST',
        'SENIOR_INVENTORY_SPECIALIST',
        'INVENTORY_MANAGER',
        'HEAD_OF_INVENTORY'
        ) DEFAULT 'INVENTORY_ASSISTANT',
    FOREIGN KEY (`inventory_manager_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `scheduling_coordinators`
(
    `scheduling_coordinator_id`   BIGINT UNIQUE PRIMARY KEY,
    `scheduling_coordinator_rank` ENUM (
        'ASSISTANT_COORDINATOR',
        'COORDINATOR',
        'SENIOR_COORDINATOR',
        'SCHEDULING_MANAGER',
        'HEAD_OF_SCHEDULING'
        ) DEFAULT 'ASSISTANT_COORDINATOR',
    FOREIGN KEY (`scheduling_coordinator_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `products`
(
    `product_id`       BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `product_type`     ENUM ('PRICING_PLAN', 'SERVICE', 'TEST', 'MEDICINE', 'MEDICAL_PRODUCT') DEFAULT 'MEDICAL_PRODUCT',
    `name`             VARCHAR(255),
    `description`      TEXT,
    `price`            DECIMAL(10, 2),
    `unit`             VARCHAR(255),
    `product_status`   ENUM ('ACTIVE', 'INACTIVE')                                             DEFAULT 'ACTIVE',
    `stock_quantities` INT,
    `image_url`        VARCHAR(255),
    `label`            ENUM ('NEW', 'SALE', 'STANDARD')                                        DEFAULT 'STANDARD'
);

CREATE TABLE `services`
(
    `service_id`    BIGINT UNIQUE PRIMARY KEY,
    `department_id` BIGINT,
    FOREIGN KEY (`service_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `service_features`
(
    `service_feature_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `service_id`         BIGINT,
    `name`               VARCHAR(255),
    `description`        TEXT,
    FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `appointments`
(
    `appointment_id`            BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `doctor_id`                 BIGINT,
    `patient_id`                BIGINT,
    `service_id`                BIGINT,
    `start_time`                TIMESTAMP,
    `duration_minutes`          INT,
    `appointment_status`        ENUM ('PENDING', 'CONFIRMED', 'COMPLETED', 'IN_PROGRESS','WAITING_RESULT', 'CANCELLED'),
    `scheduling_coordinator_id` BIGINT,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`scheduling_coordinator_id`) REFERENCES `scheduling_coordinators` (`scheduling_coordinator_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medical_records`
(
    `medical_record_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `patient_id`        BIGINT,
    `appointment_id`    BIGINT,
    `admission_date`    DATE,
    `discharge_date`    DATE,
    `main_complaint`    VARCHAR(255),
    `allergies`         JSON,
    `chronic_diseases`  JSON,
    `diagnosis`         TEXT,
    `treatment_plan`    TEXT,
    `outcome`           VARCHAR(255),
    FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medical_profiles`
(
    `medical_profile_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `patient_id`         BIGINT UNIQUE,
    `allergies`          JSON,
    `chronic_diseases`   JSON
);

CREATE TABLE `test_requests`
(
    `test_request_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `appointment_id`  BIGINT,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `tests` (
                         `test_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                         `product_id` BIGINT,
                         `gender` ENUM('MALE', 'FEMALE', 'OTHER'),
                         `age_min` INT,
                         `age_max` INT,
                         `normal_min` DECIMAL(10,2),
                         `normal_max` DECIMAL(10,2),
                         `test_unit` VARCHAR(50),
                         `NOTE` VARCHAR(255),
                         FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
);

CREATE TABLE `test_request_items`
(
    `test_id`         BIGINT,
    `test_request_id` BIGINT,
    `technician_id`   BIGINT,
    `reason`          TEXT,
    `result`          JSON,
    PRIMARY KEY (`test_id`, `test_request_id`),
    FOREIGN KEY (`test_id`) REFERENCES `tests` (`test_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`test_request_id`) REFERENCES `test_requests` (`test_request_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`technician_id`) REFERENCES `technicians` (`technician_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `ingredient_requests`
(
    `ingredient_request_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `appointment_id`        BIGINT,
    `pharmacist_id`         BIGINT,
    `reason`                TEXT,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`pharmacist_id`) REFERENCES `pharmacists` (`pharmacist_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medicines`
(
    `medicine_id` BIGINT UNIQUE PRIMARY KEY,
    FOREIGN KEY (`medicine_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medicine_ingredients`
(
    `medicine_ingredient_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `medicine_id`            BIGINT,
    `chemical_name`          VARCHAR(255),
    `generic_name`           VARCHAR(255),
    `description`            TEXT,
    `therapeutic_class`      VARCHAR(255),
    `atc_code`               VARCHAR(255),
    `strength`               VARCHAR(255),
    FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`medicine_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `ingredient_interactions`
(
    `medicine_ingredient_id_1` BIGINT,
    `medicine_ingredient_id_2` BIGINT,
    `severity`                 ENUM ('MAJOR', 'MODERATE', 'MINOR'),
    `description`              TEXT,
    PRIMARY KEY (`medicine_ingredient_id_1`, `medicine_ingredient_id_2`),
    FOREIGN KEY (`medicine_ingredient_id_1`) REFERENCES `medicine_ingredients` (`medicine_ingredient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`medicine_ingredient_id_2`) REFERENCES `medicine_ingredients` (`medicine_ingredient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `ingredient_request_items`
(
    `ingredient_request_id`  BIGINT,
    `medicine_ingredient_id` BIGINT,
    PRIMARY KEY (`ingredient_request_id`, `medicine_ingredient_id`),
    FOREIGN KEY (`ingredient_request_id`) REFERENCES `ingredient_requests` (`ingredient_request_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`medicine_ingredient_id`) REFERENCES `medicine_ingredients` (`medicine_ingredient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `prescriptions`
(
    `prescription_id`       BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `ingredient_request_id` BIGINT,
    FOREIGN KEY (`ingredient_request_id`) REFERENCES `ingredient_requests` (`ingredient_request_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `prescription_items`
(
    `prescription_id` BIGINT,
    `medicine_id`     BIGINT,
    `dosage`          TEXT,
    `quantity`        INT,
    `duration_days`   INT,
    PRIMARY KEY (`prescription_id`, `medicine_id`),
    FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`prescription_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`medicine_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `pricing_plans`
(
    `pricing_plan_id` BIGINT UNIQUE PRIMARY KEY,
    `plan_type`       ENUM ('MONTHLY', 'YEARLY', 'ONETIME', 'CUSTOM'),
    FOREIGN KEY (`pricing_plan_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `pricing_plan_features`
(
    `pricing_plan_feature_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `pricing_plan_id`         BIGINT,
    `name`                    VARCHAR(255),
    `description`             TEXT,
    FOREIGN KEY (`pricing_plan_id`) REFERENCES `pricing_plans` (`pricing_plan_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `pricing_plan_subscriptions`
(
    `patient_id`      BIGINT,
    `pricing_plan_id` BIGINT,
    PRIMARY KEY (`patient_id`, `pricing_plan_id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`pricing_plan_id`) REFERENCES `pricing_plans` (`pricing_plan_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medical_products`
(
    `medical_product_id` BIGINT UNIQUE PRIMARY KEY,
    FOREIGN KEY (`medical_product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `reviews`
(
    `review_id`   BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `review_type` ENUM ('PRODUCT', 'STAFF') DEFAULT 'STAFF',
    `patient_id`  BIGINT,
    `content`     TEXT,
    `rating`      INT,
    FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `product_reviews`
(
    `product_review_id` BIGINT UNIQUE PRIMARY KEY,
    `product_id`        BIGINT,
    FOREIGN KEY (`product_review_id`) REFERENCES `reviews` (`review_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `staff_reviews`
(
    `staff_review_id` BIGINT UNIQUE PRIMARY KEY,
    `staff_id`        BIGINT,
    FOREIGN KEY (`staff_id`) REFERENCES `staffs` (`staff_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`staff_review_id`) REFERENCES `reviews` (`review_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `suppliers`
(
    `supplier_id`  BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `name`         VARCHAR(255),
    `email`        VARCHAR(255),
    `phone_number` VARCHAR(255)
);

CREATE TABLE `supplier_transactions`
(
    `supplier_transaction_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `inventory_manager_id`    BIGINT,
    `supplier_id`             BIGINT,
    `total_amount`            DECIMAL(10, 2),
    `transaction_date`        TIMESTAMP,
    FOREIGN KEY (`inventory_manager_id`) REFERENCES `inventory_managers` (`inventory_manager_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `supplier_transaction_items`
(
    `supplier_transaction_id` BIGINT,
    `product_id`              BIGINT,
    `quantity`                INT,
    `unit_price`              DECIMAL(10, 2),
    `currency_unit`           VARCHAR(255) DEFAULT 'VND',
    `manufacture_date`        DATE,
    `expiration_date`         DATE,
    PRIMARY KEY (`supplier_transaction_id`, `product_id`),
    FOREIGN KEY (`supplier_transaction_id`) REFERENCES `supplier_transactions` (`supplier_transaction_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `categories`
(
    `category_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(255),
    `description` TEXT
);

CREATE TABLE `product_categories`
(
    `product_id`  BIGINT,
    `category_id` BIGINT,
    PRIMARY KEY (`product_id`, `category_id`),
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `product_tags`
(
    `product_id` BIGINT,
    `name`       VARCHAR(255),
    PRIMARY KEY (`product_id`, `name`),
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `product_additional_infos`
(
    `product_additional_info_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `product_id`                 BIGINT,
    `name`                       VARCHAR(255),
    `value`                      VARCHAR(255),
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `shipping_addresses`
(
    `shipping_address_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_id`             BIGINT,
    `address_type`        ENUM ('HOME', 'COMPANY'),
    `province`            VARCHAR(255),
    `district`            VARCHAR(255),
    `commune`             VARCHAR(255),
    `detail_address`      TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `coupons`
(
    `coupon_id`            BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `code`                 VARCHAR(255),
    `description`          TEXT,
    `discount_type`        ENUM ('PERCENTAGE', 'FIXED'),
    `value`                DECIMAL(10, 2),
    `minimum_order_amount` DECIMAL(10, 2) DEFAULT 0,
    `expiration_date`      DATE
);

CREATE TABLE `orders`
(
    `order_id`            BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `order_type`          ENUM ('APPOINTMENT', 'DIRECT') DEFAULT 'DIRECT',
    `appointment_id`      BIGINT,
    `shipping_address_id` BIGINT,
    `shipping_fee`        DECIMAL(10, 2)                 DEFAULT 0,
    `total_amount`        DECIMAL(10, 2),
    `order_status`        ENUM ('PENDING', 'FULLFILED', 'CANCELLED'),
    `real_amount`         DECIMAL(10, 2),
    `coupon_id`           BIGINT,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`shipping_address_id`) REFERENCES `shipping_addresses` (`shipping_address_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `order_items`
(
    `order_id`   BIGINT,
    `product_id` BIGINT,
    `quantity`   INT,
    PRIMARY KEY (`order_id`, `product_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `payments`
(
    `payment_id`     BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `order_id`       BIGINT,
    `amount`         DECIMAL(10, 2),
    `payment_method` ENUM ('CASH', 'CARD', 'MOMO'),
    `payment_status` ENUM ('SUCCESSED', 'FAILED', 'PENDING'),
    `payment_time`   TIMESTAMP,
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `wallets`
(
    `wallet_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_id`   BIGINT,
    `balance`   DECIMAL(10, 2),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `wallet_transactions`
(
    `wallet_transaction_id`   BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `wallet_id`               BIGINT,
    `payment_id`              BIGINT,
    `amount`                  DECIMAL(10, 2),
    `wallet_transaction_type` ENUM ('DEPOSIT', 'WITHDRAW'),
    `description`             VARCHAR(255),
    FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`wallet_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `user_coupons`
(
    `coupon_id` BIGINT,
    `user_id`   BIGINT,
    `use_at`    TIMESTAMP,
    PRIMARY KEY (`coupon_id`, `user_id`),
    FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `cart_items`
(
    `cart_item_id` BIGINT,
    `user_id`      BIGINT,
    `product_id`   BIGINT,
    `quantity`     INT,
    PRIMARY KEY (`cart_item_id`, `user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `wishlist_products`
(
    `user_id`    BIGINT,
    `product_id` BIGINT,
    PRIMARY KEY (`user_id`, `product_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `notifications`
(
    `notification_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT,
    `title`           VARCHAR(255),
    `content`         TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `data_assist_users` (
                                     `data_assist_user_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     `user_id`      BIGINT,
                                     `data` TEXT,
                                     FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- Tạm thời bỏ ràng buộc khóa ngoại vì có tham chiếu vòng
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- =============================================
-- CHÈN DỮ LIỆU MẪU CHO HỆ THỐNG QUẢN LÝ Y TẾ
-- =============================================
SET @@cte_max_recursion_depth = 2147483647;
-- Giới hạn đệ quy CTE, mặc định là 1000
-- hoặc một giá trị phù hợp lớn hơn
-- Tao cac bien so luong ban ghi
SET @user_count = 5000; -- Tổng số người dùng
SET @appointment_count = 10000;
SET @product_count = 2000;
SET @test_result_count = 5;
SET @review_count = 10000;
SET @supplier_transaction_count = 200;
SET @order_count = 1000;
SET @notification_count = 500;
SET @so_luong_gio_hang = 200; -- Số lượng sản phẩm trong giỏ hàng
SET @so_luong_yeu_thich = 300; -- Số lượng sản phẩm yêu thích
SET @nguoi_dung_co_gio = 3; -- 1/3 người dùng có giỏ hàng
SET @nguoi_dung_co_yeu_thich = 4;
-- 1/4 người dùng có sản phẩm yêu thích

-- Generate sample data for users table
INSERT INTO users (user_role,
                   phone_number,
                   email,
                   password_hash,
                   user_status,
                   verification_token,
                   is_verified,
                   two_factor_enabled)
WITH RECURSIVE user_generator AS (SELECT 1 AS id
                                  UNION ALL
                                  SELECT id + 1
                                  FROM user_generator
                                  WHERE id < @user_count)
SELECT
    -- user_role: Distribute as 5% ADMIN, 15% STAFF, 80% PATIENT
    ELT(CASE
            WHEN RAND() < 0.05 THEN 1 -- 5% ADMIN
            WHEN RAND() < 0.20 THEN 2 -- 15% STAFF
            ELSE 3 -- 80% PATIENT
            END, 'ADMIN', 'STAFF', 'PATIENT'),

    -- phone_number: Random 10-digit number starting with '0'
    CONCAT('0',
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10),
           FLOOR(RAND() * 10)
    ),

    -- email: Random email in format user_X@example.com
    CONCAT('user_', id, '@example.com'),

    -- password_hash: Simulate a 64-character hash string (similar to SHA-256)
    CONCAT(
            MD5(CONCAT('password', id)),
            MD5(CONCAT('salt', id))
    ),

    -- user_status: 90% ACTIVE, 10% INACTIVE
    IF(RAND() < 0.9, 'ACTIVE', 'INACTIVE'),

    -- verification_token: Random 32-character token
    MD5(CONCAT(RAND(), NOW(), id)),

    -- is_verified: 80% TRUE, 20% FALSE
    IF(RAND() < 0.8, TRUE, FALSE),

    -- two_factor_enabled: 30% TRUE, 70% FALSE
    IF(RAND() < 0.3, TRUE, FALSE)

FROM user_generator;

-- Generate sample data for patients table
INSERT INTO patients (user_id,
                      phone_number,
                      email,
                      full_name,
                      avatar_url,
                      relationship,
                      address,
                      gender,
                      birthdate,
                      blood_type)
-- Get user_ids from users table (only those with PATIENT role)
WITH user_list AS (SELECT user_id
                   FROM users
                   WHERE user_role = 'PATIENT')
SELECT
    -- user_id: From existing users table
    u.user_id,

    -- phone_number: Random 10-digit number starting with '0'
    CONCAT('0',
           LPAD(FLOOR(RAND() * 100000000), 9, '0')
    ),

    -- email: Random email
    CONCAT('patient_', u.user_id, '@gmail.com'),

    -- full_name: Random name from common English names
    CONCAT(
            ELT(FLOOR(1 + RAND() * 10), 'Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Miller', 'Davis', 'Garcia',
                'Wilson', 'Anderson'), ' ',
            ELT(FLOOR(1 + RAND() * 10), 'John', 'Robert', 'Michael', 'David', 'James', 'Mary', 'Jennifer', 'Sarah',
                'Elizabeth', 'Emma')
    ),

    -- avatar_url: Sample avatar URL
    CONCAT('https://api.dicebear.com/6.x/avataaars/svg?seed=', u.user_id, '.jpg'),

    -- relationship: Random relationship (more SELF, fewer others)
    ELT(
            CASE
                WHEN RAND() < 0.6 THEN 1 -- 60% SELF
                ELSE FLOOR(2 + RAND() * 12) -- 40% distributed among other relationships
                END,
            'SELF', 'FATHER', 'MOTHER', 'BROTHER', 'SISTER', 'DAUGHTER', 'SON',
            'GRAND_FATHER', 'GRAND_MOTHER', 'UNCLE', 'AUNT', 'CAUSIN', 'OTHER'
    ),

    -- address: Random English address
    CONCAT(
            FLOOR(1 + RAND() * 200), ' ',
            ELT(FLOOR(1 + RAND() * 5), 'Main Street', 'Park Avenue', 'Oak Road', 'Maple Drive', 'Pine Lane'), ', ',
            ELT(FLOOR(1 + RAND() * 5), 'Westwood', 'Springfield', 'Riverside', 'Newport', 'Burlington'), ', ',
            ELT(FLOOR(1 + RAND() * 5), 'New York', 'California', 'Texas', 'Florida', 'Washington')
    ),

    -- gender: Random gender (40% MALE, 40% FEMALE, 20% Other)
    ELT(
            CASE
                WHEN RAND() < 0.4 THEN 1 -- 40% MALE
                WHEN RAND() < 0.8 THEN 2 -- 40% FEMALE
                ELSE 3 -- 20% OTHER
                END,
            'MALE', 'FEMALE', 'OTHER'
    ),

    -- birthdate: Random date within past 80 years
    DATE_SUB(CURDATE(),
             INTERVAL FLOOR(RAND() * 80 * 365) DAY
    ),

    -- blood_type: Random blood type
    ELT(FLOOR(1 + RAND() * 8), 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')

FROM user_list u;

-- Tạo dữ liệu ngẫu nhiên cho bảng hospitals bằng tiếng Anh

-- Tạo bảng tạm để lưu dữ liệu
DROP TEMPORARY TABLE IF EXISTS temp_hospitals;
CREATE TEMPORARY TABLE temp_hospitals
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255),
    city          VARCHAR(100),
    hospital_type VARCHAR(50)
);

-- Thêm dữ liệu vào bảng tạm
INSERT INTO temp_hospitals (name, city, hospital_type)
VALUES ('Central', 'New York', 'General Hospital'),
       ('Memorial', 'Los Angeles', 'Medical Center'),
       ('St. Mary\'s', 'Chicago', 'Hospital'),
       ('Grace', 'Houston', 'Medical Center'),
       ('Mercy', 'Phoenix', 'Hospital'),
       ('University', 'Philadelphia', 'Medical Center'),
       ('City', 'San Antonio', 'General Hospital'),
       ('Good Hope', 'San Diego', 'Hospital'),
       ('Community', 'Dallas', 'Regional Hospital'),
       ('General', 'San Jose', 'Hospital'),
       ('Veterans', 'Austin', 'Medical Center'),
       ('Children\'s', 'Jacksonville', 'Hospital'),
       ('West', 'Fort Worth', 'Medical Center'),
       ('Sacred Heart', 'Columbus', 'Hospital'),
       ('Metropolitan', 'San Francisco', 'General Hospital'),
       ('Hope', 'Charlotte', 'Medical Center'),
       ('Unity', 'Indianapolis', 'Hospital'),
       ('Northwest', 'Seattle', 'Medical Center'),
       ('East End', 'Denver', 'Hospital'),
       ('Riverside', 'Boston', 'Medical Center');

-- Chèn dữ liệu vào bảng hospitals
INSERT INTO hospitals (name, address, phone_number, email, avatar_url)
SELECT
    -- Tên bệnh viện: kết hợp city và hospital_type
    CONCAT(city, ' ', name, ' ', hospital_type),

    -- Địa chỉ: tạo địa chỉ ngẫu nhiên
    CONCAT(
            FLOOR(100 + RAND() * 9900), ' ',
            ELT(FLOOR(1 + RAND() * 10), 'Main St', 'Oak Avenue', 'Park Road', 'Maple Drive', 'Pine Street',
                'Cedar Lane', 'Elm Boulevard', 'Washington Ave', 'Liberty Road', 'Broadway'),
            ', ', city, ', USA'
    ),

    -- Số điện thoại: định dạng Mỹ (xxx) xxx-xxxx
    CONCAT(
            '(', FLOOR(100 + RAND() * 900), ') ',
            FLOOR(100 + RAND() * 900), '-',
            FLOOR(1000 + RAND() * 9000)
    ),

    -- Email: dựa trên tên bệnh viện
    CONCAT(
            'info@',
            LOWER(REPLACE(REPLACE(name, ' ', ''), '.', '')), -- Bỏ REPLACE dư thừa
            LOWER(REPLACE(hospital_type, ' ', '')),
            '.org'
    ),
    -- Avatar URL: sử dụng DiceBear để tạo hình ảnh đại diện
    CONCAT('https://api.dicebear.com/6.x/identicon/svg?seed=', id)
FROM temp_hospitals
ORDER BY RAND()
LIMIT 10;

-- Thêm dữ liệu vào bảng departments
INSERT INTO departments (name, description, video_url, banner_url, slogan)
WITH department_data AS (
    -- Danh sách 15 phòng ban mẫu
    SELECT 1 AS id,
           'Internal Medicine' AS name,
           'Specializes in diagnosing and treating internal diseases such as cardiovascular, respiratory, and digestive disorders.' AS description
    UNION
    SELECT 2,
           'Surgery',
           'Focuses on surgical procedures and treatments for various conditions requiring operative intervention.'
    UNION
    SELECT 3,
           'Obstetrics & Gynecology',
           'Provides care for women during pregnancy, childbirth, and gynecological issues.'
    UNION
    SELECT 4, 'Pediatrics', 'Offers healthcare services for infants, children, and adolescents up to 15 years old.'
    UNION
    SELECT 5,
           'Emergency Medicine',
           'Handles emergency cases and provides 24/7 immediate care for urgent medical conditions.'
    UNION
    SELECT 6, 'Laboratory', 'Conducts various tests on blood, urine, and other body fluids to aid in diagnosis.'
    UNION
    SELECT 7, 'Diagnostic Imaging', 'Performs diagnostic imaging techniques including X-ray, CT scan, and MRI.'
    UNION
    SELECT 8, 'Ophthalmology', 'Specializes in eye care and treatment of eye diseases and disorders.'
    UNION
    SELECT 9, 'Otolaryngology (ENT)', 'Focuses on diagnosing and treating ear, nose, and throat conditions.'
    UNION
    SELECT 10, 'Dentistry', 'Provides oral healthcare and maxillofacial surgery services.'
    UNION
    SELECT 11, 'Dermatology', 'Specializes in skin disorders and their treatments.'
    UNION
    SELECT 12, 'Cardiology', 'Dedicated to heart disorders, cardiovascular interventions, and treatments.'
    UNION
    SELECT 13, 'Neurology', 'Focuses on disorders of the brain and nervous system.'
    UNION
    SELECT 14,
           'Intensive Care Unit',
           'Provides specialized care for critically ill patients requiring continuous monitoring.'
    UNION
    SELECT 15, 'Nutrition', 'Offers dietary advice and nutritional therapy for various health conditions.')

SELECT
    d.name,
    CONCAT(d.description, ' The department is equipped with modern facilities and staffed by experienced,
    dedicated medical professionals. We are committed to providing high-quality healthcare services and ensuring patient satisfaction.'),
    -- video_url (70% có video)
    CASE
        WHEN RAND() < 0.7
            THEN ELT(
                FLOOR(1 + RAND() * 5),
                'https://www.youtube.com/watch?v=EngW7tLk6R8',
                'https://www.youtube.com/watch?v=V5mByceEU_o',
                'https://www.youtube.com/watch?v=Ia-UEYYR44s',
                'https://www.youtube.com/watch?v=JnlULOjUhSQ',
                'https://www.youtube.com/watch?v=RznmAuX1Mnc'
                 )
        ELSE NULL
        END,
    CONCAT('https://api.dicebear.com/9.x/initials/svg?seed=', d.name),
    ELT(FLOOR(1 + RAND() * 5),
        'Dedicated care - Professional service',
        'Your health is our priority',
        'Effective treatment - Reliable safety',
        'Quality is honor - Compassion is responsibility',
        'Trust - Professionalism - Innovation')
FROM department_data d;

-- Tạo bảng tạm chứa tất cả các cặp (hospital, department)
CREATE TEMPORARY TABLE IF NOT EXISTS temp_hospital_departments AS
SELECT h.hospital_id, d.department_id,
       ROW_NUMBER() OVER () AS pair_row_num -- Đánh số thứ tự cho mỗi cặp
FROM hospitals h
         CROSS JOIN departments d;
select * from temp_hospital_departments;

-- Tạo bảng tạm chứa tất cả user STAFF chưa được phân công
CREATE TEMPORARY TABLE IF NOT EXISTS temp_staff_users AS
SELECT user_id,
       ROW_NUMBER() OVER () AS user_row_num -- Đánh số thứ tự user
FROM users
WHERE user_role = 'STAFF'
  AND user_id NOT IN (SELECT user_id FROM staffs);

-- Ghép cặp (hospital, department) với user duy nhất
CREATE TEMPORARY TABLE IF NOT EXISTS temp_doctor_assignments AS
SELECT hd.hospital_id, hd.department_id, u.user_id
FROM temp_hospital_departments hd
         LEFT JOIN temp_staff_users u ON hd.pair_row_num = u.user_row_num -- Ghép 1-1
WHERE u.user_id IS NOT NULL;
select * from temp_doctor_assignments;

-- Chèn bác sĩ vào staffs (dùng đúng hospital_id từ bảng temp)
INSERT INTO staffs (user_id, staff_role, department_id, hospital_id,
                    full_name, avatar_url, hire_date, staff_type, rank_level)
SELECT t.user_id,
       'DOCTOR',
       t.department_id,
       t.hospital_id, -- Dùng hospital_id từ bảng temp_doctor_assignments
       CONCAT('Dr. ', ELT(FLOOR(1 + RAND() * 10), 'James', 'Robert', 'John', 'Michael', 'William',
                          'Emma', 'Olivia', 'Sarah', 'Jennifer', 'Elizabeth'), ' ',
              ELT(FLOOR(1 + RAND() * 10), 'Smith', 'Johnson', 'Williams', 'Brown', 'Jones',
                  'Garcia', 'Miller', 'Davis', 'Wilson', 'Taylor')),
       CONCAT('https://api.dicebear.com/6.x/avataaars/svg?seed=', FLOOR(1 + RAND() * 70)),
       DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 3650) DAY),
       'FULL_TIME',
       5 + (RAND() * 3) -- Cấp cao (5-7) cho bác sĩ trưởng phòng
FROM temp_doctor_assignments t;

-- Xóa các bảng tạm
DROP TEMPORARY TABLE IF EXISTS temp_doctor_assignments;
DROP TEMPORARY TABLE IF EXISTS temp_staff_users;
DROP TEMPORARY TABLE IF EXISTS temp_departments;

-- PART 1B: Thêm các nhân viên còn lại
INSERT INTO staffs (user_id, staff_role, department_id, hospital_id,
                    full_name, avatar_url, hire_date, staff_type, rank_level)
WITH remaining_users AS (SELECT user_id, ROW_NUMBER() OVER () AS row_num
                         FROM users
                         WHERE user_role = 'STAFF'
                           AND user_id NOT IN (SELECT user_id FROM staffs)),
     all_departments AS (SELECT department_id,
                                ROW_NUMBER() OVER (ORDER BY RAND()) as random_order
                         FROM departments)
SELECT u.user_id,
       @staff_role := CASE
                          WHEN RAND() < 0.4 THEN 'DOCTOR'
                          WHEN RAND() < 0.55 THEN 'TECHNICIAN'
                          WHEN RAND() < 0.7 THEN 'PHARMACIST'
                          WHEN RAND() < 0.85 THEN 'INVENTORY_MANAGER'
                          ELSE 'SCHEDULING_COORDINATOR'
           END,
       d.department_id,
       (SELECT hospital_id FROM hospitals ORDER BY RAND() LIMIT 1), -- Chọn hospital_id ngẫu nhiên
       CONCAT(
               ELT(FLOOR(1 + RAND() * 10), 'Dr. ', 'Dr. ', 'Dr. ', 'Dr. ', 'Dr. ', 'Mr. ', 'Mrs. ', 'Ms. ', 'Prof. ',
                   ''),
               ELT(FLOOR(1 + RAND() * 10), 'James', 'Robert', 'John', 'Michael', 'William', 'Emma', 'Olivia', 'Sarah',
                   'Jennifer', 'Elizabeth'), ' ',
               ELT(FLOOR(1 + RAND() * 10), 'Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller',
                   'Davis',
                   'Wilson', 'Taylor')
       ),
       CONCAT('https://api.dicebear.com/6.x/avataaars/svg?seed=', FLOOR(1 + RAND() * 70)),
       DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 3650) DAY),
       ELT(FLOOR(1 + RAND() * 4), 'FULL_TIME', 'PART_TIME_CONTRACT', 'INTERN', 'CONSULTANT'),
       CASE @staff_role
           WHEN 'DOCTOR' THEN
               FLOOR(1 + RAND() * 4) -- Cấp 1-4 cho bác sĩ thường
           WHEN 'PHARMACIST' THEN
               FLOOR(1 + RAND() * 6) -- Cấp 1-6 cho dược sĩ
           ELSE
               FLOOR(1 + RAND() * 5) -- Cấp 1-5 cho các vai trò khác
           END
FROM remaining_users u
         JOIN
     all_departments d
     ON MOD(u.row_num, (SELECT COUNT(*) FROM departments)) + 1 = d.random_order;

-- Bước 1: Đặt tất cả manager_id về NULL
UPDATE staffs SET manager_id = NULL;

-- Bước 2: Tạo bảng tạm chứa thông tin manager cho mỗi cặp (phòng ban, bệnh viện)
CREATE TEMPORARY TABLE temp_managers AS
SELECT
    department_id,
    hospital_id,
    MIN(staff_id) as manager_staff_id
FROM staffs
WHERE staff_role = 'DOCTOR' AND rank_level >= 5
GROUP BY department_id, hospital_id;

-- Bước 3: Cập nhật manager_id từ bảng tạm
UPDATE staffs s
    JOIN temp_managers m ON s.department_id = m.department_id AND s.hospital_id = m.hospital_id
SET s.manager_id = IF(s.staff_id = m.manager_staff_id, NULL, m.manager_staff_id);

-- Bước 4: Xóa bảng tạm
DROP TEMPORARY TABLE temp_managers;

-- Generate sample data for staff_qualifications table
-- Tạo dữ liệu bằng cấp/chứng chỉ cho nhân viên
INSERT INTO staff_qualifications (staff_id,
                                  qualification_type,
                                  title,
                                  description,
                                  issue_date,
                                  expiration_date,
                                  url)
WITH staff_list AS (
    -- Lấy danh sách staff_id
    SELECT staff_id
    FROM staffs),
     qualification_count AS (
         -- Mỗi nhân viên sẽ có 1-3 bằng cấp/chứng chỉ
         SELECT s.staff_id,
                FLOOR(1 + RAND() * 3) AS num_qualifications
         FROM staff_list s),
     qualification_rows AS (
         -- Tạo các hàng dữ liệu dựa trên số lượng bằng cấp
         SELECT qc.staff_id,
                n.num
         FROM qualification_count qc
                  JOIN (SELECT 1 AS num
                        UNION
                        SELECT 2
                        UNION
                        SELECT 3) n ON n.num <= qc.num_qualifications)
SELECT
    -- staff_id
    qr.staff_id,

    -- qualification_type: Phân bổ các loại chứng chỉ
    CASE
        WHEN qr.num = 1 THEN 'DEGREE' -- Bằng cấp thường là chứng chỉ đầu tiên
        WHEN RAND() < 0.4 THEN 'CERTIFICATE'
        WHEN RAND() < 0.7 THEN 'LICENSE'
        ELSE 'AWARD'
        END,

    -- title: Tên chứng chỉ/bằng cấp phù hợp với loại
    CASE
        WHEN qr.num = 1 THEN
            ELT(FLOOR(1 + RAND() * 5),
                'Doctor of Medicine (MD)',
                'Bachelor of Medicine and Surgery (MBBS)',
                'Doctor of Nursing Practice (DNP)',
                'Bachelor of Science in Nursing (BSN)',
                'Master of Science in Medical Technology')
        WHEN qr.num = 2 THEN
            ELT(FLOOR(1 + RAND() * 5),
                'Board Certification in Internal Medicine',
                'Advanced Cardiac Life Support (ACLS)',
                'Medical License',
                'Certified Nursing Assistant (CNA)',
                'Certified Medical Assistant (CMA)')
        ELSE
            ELT(FLOOR(1 + RAND() * 5),
                'Excellence in Patient Care Award',
                'Medical Research Achievement',
                'Leadership in Healthcare',
                'Specialized Procedure Certification',
                'Healthcare Innovation Recognition')
        END,

    -- description
    CONCAT('This qualification demonstrates proficiency in ',
           ELT(FLOOR(1 + RAND() * 5),
               'medical procedures and patient care',
               'diagnostic techniques and analysis',
               'specialized treatment methodologies',
               'healthcare management and administration',
               'medical research and development'),
           ' obtained from ',
           ELT(FLOOR(1 + RAND() * 5),
               'Harvard Medical School',
               'Johns Hopkins University',
               'Mayo Clinic College of Medicine',
               'Stanford University School of Medicine',
               'National Medical Association')),

    -- issue_date: Trong vòng 20 năm qua
    DATE_SUB(CURDATE(),
             INTERVAL FLOOR(30 + RAND() * (365 * 20)) DAY),

    -- expiration_date: 50% có ngày hết hạn trong tương lai
    CASE
        WHEN RAND() < 0.5 THEN
            DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 1825) DAY) -- 0-5 năm trong tương lai
        ELSE NULL
        END,

    -- url: 70% có URL
    CASE
        WHEN RAND() < 0.7 THEN
            CONCAT('https://credentials.example.org/verify/',
                   LOWER(REPLACE(UUID(), '-', '')))
        ELSE NULL
        END

FROM qualification_rows qr;

-- Generate sample data for staff_skills table
-- Tạo dữ liệu kỹ năng cho nhân viên
INSERT INTO staff_skills (staff_id,
                          name,
                          proficiency_percentage)
WITH staff_list AS (
    -- Lấy danh sách staff_id
    SELECT staff_id, staff_role
    FROM staffs),
     skill_count AS (
         -- Mỗi nhân viên sẽ có 2-5 kỹ năng
         SELECT s.staff_id,
                s.staff_role,
                FLOOR(2 + RAND() * 4) AS num_skills
         FROM staff_list s),
     skill_rows AS (
         -- Tạo các hàng dữ liệu dựa trên số lượng kỹ năng
         SELECT sc.staff_id,
                sc.staff_role,
                n.num
         FROM skill_count sc
                  JOIN (SELECT 1 AS num
                        UNION
                        SELECT 2
                        UNION
                        SELECT 3
                        UNION
                        SELECT 4
                        UNION
                        SELECT 5) n ON n.num <= sc.num_skills)
SELECT
    -- staff_id
    sr.staff_id,

    -- name: Tên kỹ năng phù hợp với vai trò
    CASE
        WHEN sr.staff_role = 'DOCTOR' THEN
            ELT(FLOOR(1 + RAND() * 10),
                'Patient Diagnosis',
                'Surgical Procedures',
                'Patient Communication',
                'Medical Research',
                'Emergency Response',
                'Pain Management',
                'Chronic Disease Management',
                'Pediatric Care',
                'Geriatric Medicine',
                'Critical Care')
        ELSE -- TECHNICIAN
            ELT(FLOOR(1 + RAND() * 10),
                'Laboratory Testing',
                'Equipment Operation',
                'Sample Collection',
                'Data Analysis',
                'Quality Control',
                'Inventory Management',
                'Sterilization Procedures',
                'Patient Preparation',
                'Result Documentation',
                'Safety Protocols')
        END,

    -- proficiency_percentage: Giá trị từ 50-100
    ROUND(50 + (RAND() * 50), 2)

FROM skill_rows sr;

-- Generate sample data for staff_specialities table
-- Tạo dữ liệu chuyên môn cho nhân viên
INSERT INTO staff_specialities (staff_id,
                                title,
                                description)
WITH staff_docs AS (
    -- Chỉ lấy nhân viên là bác sĩ
    SELECT staff_id
    FROM staffs
    WHERE staff_role = 'DOCTOR'),
     speciality_count AS (
         -- Mỗi bác sĩ có 1-3 chuyên môn
         SELECT s.staff_id,
                FLOOR(1 + RAND() * 3) AS num_specialities
         FROM staff_docs s),
     speciality_rows AS (
         -- Tạo các hàng dữ liệu dựa trên số lượng chuyên môn
         SELECT sc.staff_id,
                n.num
         FROM speciality_count sc
                  JOIN (SELECT 1 AS num
                        UNION
                        SELECT 2
                        UNION
                        SELECT 3) n ON n.num <= sc.num_specialities)
SELECT
    -- staff_id
    sr.staff_id,

    -- title: Tên chuyên môn
    ELT(FLOOR(1 + RAND() * 15),
        'Cardiology',
        'Neurology',
        'Pediatrics',
        'Orthopedics',
        'Dermatology',
        'Oncology',
        'Gynecology',
        'Psychiatry',
        'Ophthalmology',
        'Urology',
        'Gastroenterology',
        'Endocrinology',
        'Pulmonology',
        'Nephrology',
        'Hematology'),

    -- description: Mô tả chi tiết về chuyên môn
    CONCAT('Specializes in the diagnosis and treatment of ',
           ELT(FLOOR(1 + RAND() * 5),
               'complex and challenging cases involving ',
               'both acute and chronic conditions related to ',
               'advanced procedures and innovative therapies for ',
               'preventive care and management of ',
               'surgical and non-surgical approaches to '),
           LOWER(ELT(FLOOR(1 + RAND() * 5),
                     'the heart and cardiovascular system',
                     'the brain and nervous system',
                     'children and adolescents',
                     'the musculoskeletal system',
                     'various skin conditions')),
           '. With expertise in ',
           ELT(FLOOR(1 + RAND() * 3),
               'minimally invasive techniques',
               'the latest diagnostic technologies',
               'comprehensive patient-centered approaches'),
           '.')

FROM speciality_rows sr;
-- Generate sample data for staff_shifts table
-- Tạo dữ liệu ca làm việc cho nhân viên (trong 30 ngày tới)
INSERT INTO staff_shifts (staff_id,
                          date,
                          shift_type)
WITH staff_list AS (
    -- Lấy danh sách staff_id
    SELECT staff_id
    FROM staffs),
     date_range AS (
         -- Tạo phạm vi 30 ngày tới
         SELECT ADDDATE(CURRENT_DATE(), t.day) AS shift_date
         FROM (SELECT 0 AS day
               UNION
               SELECT 1
               UNION
               SELECT 2
               UNION
               SELECT 3
               UNION
               SELECT 4
               UNION
               SELECT 5
               UNION
               SELECT 6
               UNION
               SELECT 7
               UNION
               SELECT 8
               UNION
               SELECT 9
               UNION
               SELECT 10
               UNION
               SELECT 11
               UNION
               SELECT 12
               UNION
               SELECT 13
               UNION
               SELECT 14
               UNION
               SELECT 15
               UNION
               SELECT 16
               UNION
               SELECT 17
               UNION
               SELECT 18
               UNION
               SELECT 19
               UNION
               SELECT 20
               UNION
               SELECT 21
               UNION
               SELECT 22
               UNION
               SELECT 23
               UNION
               SELECT 24
               UNION
               SELECT 25
               UNION
               SELECT 26
               UNION
               SELECT 27
               UNION
               SELECT 28
               UNION
               SELECT 29) AS t),
     shift_assignments AS (
         -- Phân bổ ca làm việc cho nhân viên
         SELECT s.staff_id,
                d.shift_date,
                -- Mỗi ngày, mỗi nhân viên có 30% xác suất được phân công ca làm việc
                CASE WHEN RAND() < 0.3 THEN TRUE ELSE FALSE END AS has_shift
         FROM staff_list s
                  CROSS JOIN date_range d)
SELECT
    -- staff_id
    sa.staff_id,

    -- date: Ngày ca làm việc
    sa.shift_date,

    -- shift_type: Loại ca (sáng, chiều, tối)
    ELT(FLOOR(1 + RAND() * 3), 'MORNING', 'AFTERNOON', 'NIGHT')

FROM shift_assignments sa
WHERE sa.has_shift = TRUE;

-- Generate sample data for staff_schedules table
-- Tạo dữ liệu lịch trình cho nhân viên (trong 14 ngày tới)
INSERT INTO staff_schedules (staff_id,
                             available_date,
                             start_time,
                             end_time)
WITH staff_list AS (
    -- Lấy danh sách staff_id
    SELECT staff_id
    FROM staffs),
     date_range AS (
         -- Tạo phạm vi 14 ngày tới
         SELECT ADDDATE(CURRENT_DATE(), t.day) AS available_date
         FROM (SELECT 0 AS day
               UNION
               SELECT 1
               UNION
               SELECT 2
               UNION
               SELECT 3
               UNION
               SELECT 4
               UNION
               SELECT 5
               UNION
               SELECT 6
               UNION
               SELECT 7
               UNION
               SELECT 8
               UNION
               SELECT 9
               UNION
               SELECT 10
               UNION
               SELECT 11
               UNION
               SELECT 12
               UNION
               SELECT 13) AS t),
     schedule_base AS (
         -- Cơ sở dữ liệu lịch trình
         SELECT s.staff_id,
                d.available_date,
                -- Mỗi ngày, mỗi nhân viên có 40% xác suất có lịch làm việc
                CASE WHEN RAND() < 0.4 THEN TRUE ELSE FALSE END AS has_schedule
         FROM staff_list s
                  CROSS JOIN date_range d)
SELECT
    -- staff_id
    sb.staff_id,

    -- available_date: Ngày có lịch
    sb.available_date,

    -- start_time: Thời gian bắt đầu (8:00, 9:00, hoặc 10:00)
    TIMESTAMP(sb.available_date,
              CONCAT(8 + FLOOR(RAND() * 3), ':00:00')),

    -- end_time: Thời gian kết thúc (4-8 giờ sau khi bắt đầu)
    TIMESTAMP(sb.available_date,
              CONCAT(12 + FLOOR(RAND() * 5), ':00:00'))

FROM schedule_base sb
WHERE sb.has_schedule = TRUE;

-- Generate sample data for staff_experiences table
-- Tạo dữ liệu kinh nghiệm làm việc cho nhân viên
INSERT INTO staff_experiences (staff_id,
                               year,
                               department,
                               position,
                               hospital,
                               result)
WITH staff_list AS (
    -- Lấy danh sách staff_id
    SELECT staff_id
    FROM staffs),
     experience_count AS (
         -- Mỗi nhân viên có 1-3 kinh nghiệm làm việc trước đây
         SELECT s.staff_id,
                FLOOR(1 + RAND() * 3) AS num_experiences
         FROM staff_list s),
     experience_rows AS (
         -- Tạo các hàng dữ liệu dựa trên số lượng kinh nghiệm
         SELECT ec.staff_id,
                n.num
         FROM experience_count ec
                  JOIN (SELECT 1 AS num
                        UNION
                        SELECT 2
                        UNION
                        SELECT 3) n ON n.num <= ec.num_experiences)
SELECT
    -- staff_id
    er.staff_id,

    -- year: Năm làm việc (quá khứ, sắp xếp theo thứ tự)
    YEAR(CURDATE()) - FLOOR(2 + RAND() * 10) - (3 - er.num) * 3,

    -- department: Khoa phòng
    ELT(FLOOR(1 + RAND() * 10),
        'Emergency Department',
        'Internal Medicine',
        'Surgery',
        'Pediatrics',
        'Obstetrics & Gynecology',
        'Cardiology',
        'Neurology',
        'Oncology',
        'Radiology',
        'Intensive Care Unit'),

    -- position: Vị trí công việc
    ELT(FLOOR(1 + RAND() * 10),
        'Resident Physician',
        'Attending Physician',
        'Medical Officer',
        'Department Head',
        'Clinical Specialist',
        'Senior Consultant',
        'Junior Doctor',
        'Medical Researcher',
        'Chief Resident',
        'Medical Technician'),

    -- hospital: Nơi làm việc
    ELT(FLOOR(1 + RAND() * 10),
        'Mayo Clinic',
        'Cleveland Clinic',
        'Johns Hopkins Hospital',
        'Massachusetts General Hospital',
        'New York-Presbyterian Hospital',
        'UCSF Medical Center',
        'Stanford Health Care',
        'Northwestern Memorial Hospital',
        'UCLA Medical Center',
        'Cedars-Sinai Medical Center'),

    -- result: Thành tựu/kết quả
    ELT(FLOOR(1 + RAND() * 5),
        'Successfully treated over 1000 patients',
        'Reduced department error rate by 15%',
        'Led team of medical professionals',
        'Published research in peer-reviewed journals',
        'Implemented new treatment protocols')

FROM experience_rows er;

-- Generate sample data for staff_educations table
INSERT INTO staff_educations (staff_id, year, degree, institute, result)
WITH staff_list AS (SELECT staff_id
                    FROM staffs
                    ORDER BY staff_id),
-- Generate 1-3 education records per staff
     education_count AS (SELECT sl.staff_id,
                                -- Doctors typically have more education records
                                CASE
                                    WHEN MOD(sl.staff_id, 3) = 0 THEN 3 -- 33% have 3 records (PhD, Masters, Bachelors)
                                    WHEN MOD(sl.staff_id, 3) = 1 THEN 2 -- 33% have 2 records (Masters, Bachelors)
                                    ELSE 1 -- 33% have 1 record (Bachelors only)
                                    END as edu_count
                         FROM staff_list sl),
     education_generator AS (SELECT ec.staff_id,
                                    ec.edu_count,
                                    num.n as edu_level
                             FROM education_count ec
                                      CROSS JOIN (SELECT 1 as n
                                                  UNION ALL
                                                  SELECT 2
                                                  UNION ALL
                                                  SELECT 3) num
                             WHERE num.n <= ec.edu_count)
SELECT eg.staff_id,
       -- Year based on education level (PhD more recent than Masters more recent than Bachelors)
       -- Base graduation year on staff ID (higher ID = more recent graduate)
       CASE eg.edu_level
           WHEN 1 THEN 2023 - FLOOR((200 - MOD(eg.staff_id, 150)) / 10) -- Highest degree (most recent)
           WHEN 2 THEN 2020 - FLOOR((200 - MOD(eg.staff_id, 150)) / 10) -- Middle degree
           ELSE 2016 - FLOOR((200 - MOD(eg.staff_id, 150)) / 10) -- First degree (oldest)
           END as year,
       -- Degree types based on level
       CASE eg.edu_level
           WHEN 1 THEN ELT(1 + FLOOR(RAND() * 3), 'Ph.D. in Pharmacy', 'Doctor of Medicine', 'Ph.D. in Biochemistry')
           WHEN 2 THEN ELT(1 + FLOOR(RAND() * 4), 'Master of Pharmacy', 'Master of Public Health',
                           'Master of Science in Nursing', 'Master of Clinical Research')
           ELSE ELT(1 + FLOOR(RAND() * 5), 'Bachelor of Pharmacy', 'Bachelor of Science in Nursing',
                    'Bachelor of Medicine', 'Bachelor of Health Sciences', 'Bachelor of Biomedical Science')
           END as degree,
       -- Various prestigious medical and pharmacy schools
       CASE FLOOR(RAND() * 10)
           WHEN 0 THEN 'Harvard Medical School'
           WHEN 1 THEN 'Johns Hopkins University'
           WHEN 2 THEN 'University of California'
           WHEN 3 THEN 'Stanford University'
           WHEN 4 THEN 'University of Michigan'
           WHEN 5 THEN 'University of Pennsylvania'
           WHEN 6 THEN 'Columbia University'
           WHEN 7 THEN 'Mayo Clinic School of Medicine'
           WHEN 8 THEN 'Duke University'
           ELSE 'Yale University'
           END as institute,
       -- Academic results
       CASE FLOOR(RAND() * 10)
           WHEN 0 THEN 'Summa Cum Laude'
           WHEN 1 THEN 'Magna Cum Laude'
           WHEN 2 THEN 'Cum Laude'
           WHEN 3 THEN 'Distinction'
           WHEN 4 THEN 'High Distinction'
           WHEN 5 THEN 'Honors'
           WHEN 6 THEN 'First Class Honors'
           WHEN 7 THEN '3.9 GPA'
           WHEN 8 THEN '3.8 GPA'
           ELSE 'Excellence Award'
           END as result
FROM education_generator eg
ORDER BY eg.staff_id, eg.edu_level DESC;
-- Order by staff ID and education level

-- Generate sample data for specific roles
-- 1. Chèn dữ liệu vào bảng doctors dựa theo rank_level
INSERT INTO doctors (doctor_id, doctor_rank)
SELECT staff_id,
       CASE
           WHEN rank_level = 1 THEN 'INTERN'
           WHEN rank_level = 2 THEN 'RESIDENT'
           WHEN rank_level = 3 THEN 'ATTENDING'
           WHEN rank_level = 4 THEN 'SPECIALIST'
           WHEN rank_level = 5 THEN 'SENIOR_SPECIALIST'
           WHEN rank_level = 6 THEN 'CONSULTANT'
           WHEN rank_level = 7 THEN 'CHIEF_PHYSICIAN'
           ELSE 'INTERN' -- Mặc định nếu rank_level không hợp lệ
           END as doctor_rank
FROM staffs
WHERE staff_role = 'DOCTOR';

-- 2. Chèn dữ liệu vào bảng technicians dựa theo rank_level
INSERT INTO technicians (technician_id, technician_rank)
SELECT staff_id,
       CASE
           WHEN rank_level = 1 THEN 'JUNIOR_TECHNICIAN'
           WHEN rank_level = 2 THEN 'TECHNICIAN'
           WHEN rank_level = 3 THEN 'SENIOR_TECHNICIAN'
           WHEN rank_level = 4 THEN 'LEAD_TECHNICIAN'
           WHEN rank_level = 5 THEN 'TECHNICAL_SUPERVISOR'
           ELSE 'JUNIOR_TECHNICIAN' -- Mặc định
           END as technician_rank
FROM staffs
WHERE staff_role = 'TECHNICIAN';

-- 3. Chèn dữ liệu vào bảng pharmacists dựa theo rank_level
INSERT INTO pharmacists (pharmacist_id, pharmacist_rank)
SELECT staff_id,
       CASE
           WHEN rank_level = 1 THEN 'PHARMACY_INTERN'
           WHEN rank_level = 2 THEN 'STAFF_PHARMACIST'
           WHEN rank_level = 3 THEN 'CLINICAL_PHARMACIST'
           WHEN rank_level = 4 THEN 'SENIOR_PHARMACIST'
           WHEN rank_level = 5 THEN 'PHARMACY_MANAGER'
           WHEN rank_level = 6 THEN 'CHIEF_PHARMACIST'
           ELSE 'PHARMACY_INTERN' -- Mặc định
           END as pharmacist_rank
FROM staffs
WHERE staff_role = 'PHARMACIST';

-- 4. Chèn dữ liệu vào bảng inventory_managers dựa theo rank_level
INSERT INTO inventory_managers (inventory_manager_id, inventory_manager_rank)
SELECT staff_id,
       CASE
           WHEN rank_level = 1 THEN 'INVENTORY_ASSISTANT'
           WHEN rank_level = 2 THEN 'INVENTORY_SPECIALIST'
           WHEN rank_level = 3 THEN 'SENIOR_INVENTORY_SPECIALIST'
           WHEN rank_level = 4 THEN 'INVENTORY_MANAGER'
           WHEN rank_level = 5 THEN 'HEAD_OF_INVENTORY'
           ELSE 'INVENTORY_ASSISTANT' -- Mặc định
           END as inventory_manager_rank
FROM staffs
WHERE staff_role = 'INVENTORY_MANAGER';

-- 5. Chèn dữ liệu vào bảng scheduling_coordinators dựa theo rank_level
INSERT INTO scheduling_coordinators (scheduling_coordinator_id, scheduling_coordinator_rank)
SELECT staff_id,
       CASE
           WHEN rank_level = 1 THEN 'ASSISTANT_COORDINATOR'
           WHEN rank_level = 2 THEN 'COORDINATOR'
           WHEN rank_level = 3 THEN 'SENIOR_COORDINATOR'
           WHEN rank_level = 4 THEN 'SCHEDULING_MANAGER'
           WHEN rank_level = 5 THEN 'HEAD_OF_SCHEDULING'
           ELSE 'ASSISTANT_COORDINATOR' -- Mặc định
           END as scheduling_coordinator_rank
FROM staffs
WHERE staff_role = 'SCHEDULING_COORDINATOR';
INSERT INTO products ( product_type, name, description, price, unit, product_status, stock_quantities, image_url, label)
VALUES
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 277091.17, 'lần', 'ACTIVE', 58, 'https://api.dicebear.com/9.x/icons/svg?seed=1', 'SALE'),
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 149039.31, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=2', 'SALE'),
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 189030.95, 'lần', 'ACTIVE', 95, 'https://api.dicebear.com/9.x/icons/svg?seed=3', 'NEW'),
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 172173.37, 'lần', 'ACTIVE', 44, 'https://api.dicebear.com/9.x/icons/svg?seed=4', 'NEW'),
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 256631.13, 'lần', 'ACTIVE', 61, 'https://api.dicebear.com/9.x/icons/svg?seed=5', 'STANDARD'),
    ( 'TEST', 'Glucose máu', 'Xét nghiệm Glucose máu định kỳ', 216235.36, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=6', 'SALE'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 197400.19, 'lần', 'ACTIVE', 30, 'https://api.dicebear.com/9.x/icons/svg?seed=7', 'STANDARD'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 210562.7, 'lần', 'ACTIVE', 35, 'https://api.dicebear.com/9.x/icons/svg?seed=8', 'SALE'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 158469.81, 'lần', 'ACTIVE', 25, 'https://api.dicebear.com/9.x/icons/svg?seed=9', 'NEW'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 265648.51, 'lần', 'ACTIVE', 83, 'https://api.dicebear.com/9.x/icons/svg?seed=10', 'NEW'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 182463.14, 'lần', 'ACTIVE', 43, 'https://api.dicebear.com/9.x/icons/svg?seed=11', 'STANDARD'),
    ( 'TEST', 'Cholesterol toàn phần', 'Xét nghiệm Cholesterol toàn phần định kỳ', 216521.57, 'lần', 'ACTIVE', 88, 'https://api.dicebear.com/9.x/icons/svg?seed=12', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 149331.68, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=13', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 260109.42, 'lần', 'ACTIVE', 88, 'https://api.dicebear.com/9.x/icons/svg?seed=14', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 204428.31, 'lần', 'ACTIVE', 82, 'https://api.dicebear.com/9.x/icons/svg?seed=15', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 273856.36, 'lần', 'ACTIVE', 69, 'https://api.dicebear.com/9.x/icons/svg?seed=16', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 137611.37, 'lần', 'ACTIVE', 25, 'https://api.dicebear.com/9.x/icons/svg?seed=17', 'STANDARD'),
    ( 'TEST', 'Triglyceride', 'Xét nghiệm Triglyceride định kỳ', 96500.98, 'lần', 'ACTIVE', 69, 'https://api.dicebear.com/9.x/icons/svg?seed=18', 'STANDARD'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 199295.04, 'lần', 'ACTIVE', 90, 'https://api.dicebear.com/9.x/icons/svg?seed=19', 'SALE'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 186806.65, 'lần', 'ACTIVE', 51, 'https://api.dicebear.com/9.x/icons/svg?seed=20', 'STANDARD'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 131527.1, 'lần', 'ACTIVE', 22, 'https://api.dicebear.com/9.x/icons/svg?seed=21', 'NEW'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 116853.95, 'lần', 'ACTIVE', 58, 'https://api.dicebear.com/9.x/icons/svg?seed=22', 'STANDARD'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 236034.41, 'lần', 'ACTIVE', 23, 'https://api.dicebear.com/9.x/icons/svg?seed=23', 'NEW'),
    ( 'TEST', 'ALT (GPT)', 'Xét nghiệm ALT (GPT) định kỳ', 82801.28, 'lần', 'ACTIVE', 91, 'https://api.dicebear.com/9.x/icons/svg?seed=24', 'SALE'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 269151.71, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=25', 'NEW'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 137434.74, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=26', 'SALE'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 261164.31, 'lần', 'ACTIVE', 57, 'https://api.dicebear.com/9.x/icons/svg?seed=27', 'NEW'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 107328.07, 'lần', 'ACTIVE', 45, 'https://api.dicebear.com/9.x/icons/svg?seed=28', 'SALE'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 227119.0, 'lần', 'ACTIVE', 27, 'https://api.dicebear.com/9.x/icons/svg?seed=29', 'SALE'),
    ( 'TEST', 'AST (GOT)', 'Xét nghiệm AST (GOT) định kỳ', 110508.44, 'lần', 'ACTIVE', 22, 'https://api.dicebear.com/9.x/icons/svg?seed=30', 'NEW'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 245475.4, 'lần', 'ACTIVE', 18, 'https://api.dicebear.com/9.x/icons/svg?seed=31', 'STANDARD'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 216895.09, 'lần', 'ACTIVE', 65, 'https://api.dicebear.com/9.x/icons/svg?seed=32', 'SALE'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 237021.68, 'lần', 'ACTIVE', 80, 'https://api.dicebear.com/9.x/icons/svg?seed=33', 'SALE'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 161712.96, 'lần', 'ACTIVE', 34, 'https://api.dicebear.com/9.x/icons/svg?seed=34', 'SALE'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 129156.16, 'lần', 'ACTIVE', 18, 'https://api.dicebear.com/9.x/icons/svg?seed=35', 'SALE'),
    ( 'TEST', 'Creatinine', 'Xét nghiệm Creatinine định kỳ', 116321.4, 'lần', 'ACTIVE', 38, 'https://api.dicebear.com/9.x/icons/svg?seed=36', 'NEW'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 222872.79, 'lần', 'ACTIVE', 19, 'https://api.dicebear.com/9.x/icons/svg?seed=37', 'STANDARD'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 191651.24, 'lần', 'ACTIVE', 47, 'https://api.dicebear.com/9.x/icons/svg?seed=38', 'SALE'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 180643.33, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=39', 'NEW'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 94837.75, 'lần', 'ACTIVE', 20, 'https://api.dicebear.com/9.x/icons/svg?seed=40', 'NEW'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 194799.37, 'lần', 'ACTIVE', 66, 'https://api.dicebear.com/9.x/icons/svg?seed=41', 'SALE'),
    ( 'TEST', 'Ure', 'Xét nghiệm Ure định kỳ', 235916.58, 'lần', 'ACTIVE', 77, 'https://api.dicebear.com/9.x/icons/svg?seed=42', 'SALE'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 172646.94, 'lần', 'ACTIVE', 28, 'https://api.dicebear.com/9.x/icons/svg?seed=43', 'STANDARD'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 243522.64, 'lần', 'ACTIVE', 56, 'https://api.dicebear.com/9.x/icons/svg?seed=44', 'STANDARD'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 272462.64, 'lần', 'ACTIVE', 60, 'https://api.dicebear.com/9.x/icons/svg?seed=45', 'STANDARD'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 122189.73, 'lần', 'ACTIVE', 88, 'https://api.dicebear.com/9.x/icons/svg?seed=46', 'STANDARD'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 245599.69, 'lần', 'ACTIVE', 28, 'https://api.dicebear.com/9.x/icons/svg?seed=47', 'SALE'),
    ( 'TEST', 'Acid uric', 'Xét nghiệm Acid uric định kỳ', 291039.96, 'lần', 'ACTIVE', 17, 'https://api.dicebear.com/9.x/icons/svg?seed=48', 'NEW'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 85244.03, 'lần', 'ACTIVE', 12, 'https://api.dicebear.com/9.x/icons/svg?seed=49', 'NEW'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 214959.03, 'lần', 'ACTIVE', 16, 'https://api.dicebear.com/9.x/icons/svg?seed=50', 'SALE'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 184377.28, 'lần', 'ACTIVE', 80, 'https://api.dicebear.com/9.x/icons/svg?seed=51', 'NEW'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 270632.29, 'lần', 'ACTIVE', 25, 'https://api.dicebear.com/9.x/icons/svg?seed=52', 'SALE'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 280709.16, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=53', 'STANDARD'),
    ( 'TEST', 'Calci toàn phần', 'Xét nghiệm Calci toàn phần định kỳ', 127316.13, 'lần', 'ACTIVE', 62, 'https://api.dicebear.com/9.x/icons/svg?seed=54', 'STANDARD'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 278576.43, 'lần', 'ACTIVE', 88, 'https://api.dicebear.com/9.x/icons/svg?seed=55', 'STANDARD'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 97513.44, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=56', 'STANDARD'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 284760.72, 'lần', 'ACTIVE', 55, 'https://api.dicebear.com/9.x/icons/svg?seed=57', 'SALE'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 286309.14, 'lần', 'ACTIVE', 47, 'https://api.dicebear.com/9.x/icons/svg?seed=58', 'NEW'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 263621.96, 'lần', 'ACTIVE', 98, 'https://api.dicebear.com/9.x/icons/svg?seed=59', 'STANDARD'),
    ( 'TEST', 'Sắt huyết thanh', 'Xét nghiệm Sắt huyết thanh định kỳ', 168771.97, 'lần', 'ACTIVE', 58, 'https://api.dicebear.com/9.x/icons/svg?seed=60', 'NEW'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 101533.86, 'lần', 'ACTIVE', 84, 'https://api.dicebear.com/9.x/icons/svg?seed=61', 'STANDARD'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 240657.25, 'lần', 'ACTIVE', 57, 'https://api.dicebear.com/9.x/icons/svg?seed=62', 'STANDARD'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 191688.92, 'lần', 'ACTIVE', 61, 'https://api.dicebear.com/9.x/icons/svg?seed=63', 'SALE'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 161152.67, 'lần', 'ACTIVE', 76, 'https://api.dicebear.com/9.x/icons/svg?seed=64', 'NEW'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 158964.89, 'lần', 'ACTIVE', 21, 'https://api.dicebear.com/9.x/icons/svg?seed=65', 'STANDARD'),
    ( 'TEST', 'Ferritin', 'Xét nghiệm Ferritin định kỳ', 205864.79, 'lần', 'ACTIVE', 68, 'https://api.dicebear.com/9.x/icons/svg?seed=66', 'NEW'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 229997.01, 'lần', 'ACTIVE', 63, 'https://api.dicebear.com/9.x/icons/svg?seed=67', 'STANDARD'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 195238.01, 'lần', 'ACTIVE', 23, 'https://api.dicebear.com/9.x/icons/svg?seed=68', 'STANDARD'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 194734.61, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=69', 'STANDARD'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 99778.24, 'lần', 'ACTIVE', 21, 'https://api.dicebear.com/9.x/icons/svg?seed=70', 'STANDARD'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 299347.01, 'lần', 'ACTIVE', 72, 'https://api.dicebear.com/9.x/icons/svg?seed=71', 'SALE'),
    ( 'TEST', 'Protein toàn phần', 'Xét nghiệm Protein toàn phần định kỳ', 133490.74, 'lần', 'ACTIVE', 14, 'https://api.dicebear.com/9.x/icons/svg?seed=72', 'SALE'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 297021.59, 'lần', 'ACTIVE', 73, 'https://api.dicebear.com/9.x/icons/svg?seed=73', 'NEW'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 250383.06, 'lần', 'ACTIVE', 10, 'https://api.dicebear.com/9.x/icons/svg?seed=74', 'SALE'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 251759.98, 'lần', 'ACTIVE', 70, 'https://api.dicebear.com/9.x/icons/svg?seed=75', 'NEW'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 285744.23, 'lần', 'ACTIVE', 10, 'https://api.dicebear.com/9.x/icons/svg?seed=76', 'SALE'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 269275.84, 'lần', 'ACTIVE', 79, 'https://api.dicebear.com/9.x/icons/svg?seed=77', 'SALE'),
    ( 'TEST', 'Albumin', 'Xét nghiệm Albumin định kỳ', 256299.99, 'lần', 'ACTIVE', 68, 'https://api.dicebear.com/9.x/icons/svg?seed=78', 'STANDARD'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 220951.4, 'lần', 'ACTIVE', 61, 'https://api.dicebear.com/9.x/icons/svg?seed=79', 'NEW'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 221134.53, 'lần', 'ACTIVE', 52, 'https://api.dicebear.com/9.x/icons/svg?seed=80', 'NEW'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 204647.32, 'lần', 'ACTIVE', 65, 'https://api.dicebear.com/9.x/icons/svg?seed=81', 'SALE'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 216276.97, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=82', 'NEW'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 290835.4, 'lần', 'ACTIVE', 24, 'https://api.dicebear.com/9.x/icons/svg?seed=83', 'STANDARD'),
    ( 'TEST', 'Globulin', 'Xét nghiệm Globulin định kỳ', 265241.96, 'lần', 'ACTIVE', 56, 'https://api.dicebear.com/9.x/icons/svg?seed=84', 'STANDARD'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 203374.95, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=85', 'STANDARD'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 264172.37, 'lần', 'ACTIVE', 19, 'https://api.dicebear.com/9.x/icons/svg?seed=86', 'SALE'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 230011.53, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=87', 'SALE'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 115718.06, 'lần', 'ACTIVE', 54, 'https://api.dicebear.com/9.x/icons/svg?seed=88', 'SALE'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 178077.17, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=89', 'SALE'),
    ( 'TEST', 'Bilirubin toàn phần', 'Xét nghiệm Bilirubin toàn phần định kỳ', 127020.41, 'lần', 'ACTIVE', 54, 'https://api.dicebear.com/9.x/icons/svg?seed=90', 'NEW'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 141282.46, 'lần', 'ACTIVE', 18, 'https://api.dicebear.com/9.x/icons/svg?seed=91', 'NEW'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 116413.29, 'lần', 'ACTIVE', 50, 'https://api.dicebear.com/9.x/icons/svg?seed=92', 'STANDARD'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 234485.49, 'lần', 'ACTIVE', 95, 'https://api.dicebear.com/9.x/icons/svg?seed=93', 'NEW'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 247984.9, 'lần', 'ACTIVE', 43, 'https://api.dicebear.com/9.x/icons/svg?seed=94', 'STANDARD'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 151410.44, 'lần', 'ACTIVE', 94, 'https://api.dicebear.com/9.x/icons/svg?seed=95', 'NEW'),
    ( 'TEST', 'Bilirubin trực tiếp', 'Xét nghiệm Bilirubin trực tiếp định kỳ', 266863.07, 'lần', 'ACTIVE', 43, 'https://api.dicebear.com/9.x/icons/svg?seed=96', 'SALE'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 210084.06, 'lần', 'ACTIVE', 75, 'https://api.dicebear.com/9.x/icons/svg?seed=97', 'SALE'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 118033.17, 'lần', 'ACTIVE', 22, 'https://api.dicebear.com/9.x/icons/svg?seed=98', 'NEW'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 275402.38, 'lần', 'ACTIVE', 19, 'https://api.dicebear.com/9.x/icons/svg?seed=99', 'SALE'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 222398.52, 'lần', 'ACTIVE', 65, 'https://api.dicebear.com/9.x/icons/svg?seed=100', 'NEW'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 167147.75, 'lần', 'ACTIVE', 63, 'https://api.dicebear.com/9.x/icons/svg?seed=101', 'NEW'),
    ( 'TEST', 'Bilirubin gián tiếp', 'Xét nghiệm Bilirubin gián tiếp định kỳ', 125719.26, 'lần', 'ACTIVE', 47, 'https://api.dicebear.com/9.x/icons/svg?seed=102', 'SALE'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 133176.66, 'lần', 'ACTIVE', 46, 'https://api.dicebear.com/9.x/icons/svg?seed=103', 'NEW'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 122896.19, 'lần', 'ACTIVE', 31, 'https://api.dicebear.com/9.x/icons/svg?seed=104', 'STANDARD'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 219953.18, 'lần', 'ACTIVE', 42, 'https://api.dicebear.com/9.x/icons/svg?seed=105', 'STANDARD'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 296895.64, 'lần', 'ACTIVE', 72, 'https://api.dicebear.com/9.x/icons/svg?seed=106', 'STANDARD'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 294465.73, 'lần', 'ACTIVE', 35, 'https://api.dicebear.com/9.x/icons/svg?seed=107', 'STANDARD'),
    ( 'TEST', 'HbA1c', 'Xét nghiệm HbA1c định kỳ', 193021.63, 'lần', 'ACTIVE', 62, 'https://api.dicebear.com/9.x/icons/svg?seed=108', 'NEW'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 200680.9, 'lần', 'ACTIVE', 92, 'https://api.dicebear.com/9.x/icons/svg?seed=109', 'STANDARD'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 276418.38, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=110', 'STANDARD'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 162527.08, 'lần', 'ACTIVE', 21, 'https://api.dicebear.com/9.x/icons/svg?seed=111', 'STANDARD'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 137912.1, 'lần', 'ACTIVE', 25, 'https://api.dicebear.com/9.x/icons/svg?seed=112', 'NEW'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 288521.43, 'lần', 'ACTIVE', 70, 'https://api.dicebear.com/9.x/icons/svg?seed=113', 'SALE'),
    ( 'TEST', 'CRP', 'Xét nghiệm CRP định kỳ', 173288.41, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=114', 'SALE'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 164858.48, 'lần', 'ACTIVE', 26, 'https://api.dicebear.com/9.x/icons/svg?seed=115', 'STANDARD'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 92189.73, 'lần', 'ACTIVE', 52, 'https://api.dicebear.com/9.x/icons/svg?seed=116', 'NEW'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 242400.64, 'lần', 'ACTIVE', 52, 'https://api.dicebear.com/9.x/icons/svg?seed=117', 'SALE'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 111545.42, 'lần', 'ACTIVE', 30, 'https://api.dicebear.com/9.x/icons/svg?seed=118', 'STANDARD'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 207478.73, 'lần', 'ACTIVE', 32, 'https://api.dicebear.com/9.x/icons/svg?seed=119', 'SALE'),
    ( 'TEST', 'TSH', 'Xét nghiệm TSH định kỳ', 258103.56, 'lần', 'ACTIVE', 62, 'https://api.dicebear.com/9.x/icons/svg?seed=120', 'STANDARD'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 80425.43, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=121', 'SALE'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 279199.09, 'lần', 'ACTIVE', 98, 'https://api.dicebear.com/9.x/icons/svg?seed=122', 'NEW'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 238191.17, 'lần', 'ACTIVE', 38, 'https://api.dicebear.com/9.x/icons/svg?seed=123', 'SALE'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 147135.32, 'lần', 'ACTIVE', 64, 'https://api.dicebear.com/9.x/icons/svg?seed=124', 'STANDARD'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 216213.95, 'lần', 'ACTIVE', 41, 'https://api.dicebear.com/9.x/icons/svg?seed=125', 'NEW'),
    ( 'TEST', 'FT3', 'Xét nghiệm FT3 định kỳ', 262299.49, 'lần', 'ACTIVE', 45, 'https://api.dicebear.com/9.x/icons/svg?seed=126', 'NEW'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 281259.97, 'lần', 'ACTIVE', 91, 'https://api.dicebear.com/9.x/icons/svg?seed=127', 'STANDARD'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 191944.76, 'lần', 'ACTIVE', 64, 'https://api.dicebear.com/9.x/icons/svg?seed=128', 'SALE'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 104335.9, 'lần', 'ACTIVE', 21, 'https://api.dicebear.com/9.x/icons/svg?seed=129', 'SALE'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 113928.09, 'lần', 'ACTIVE', 90, 'https://api.dicebear.com/9.x/icons/svg?seed=130', 'NEW'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 205898.78, 'lần', 'ACTIVE', 47, 'https://api.dicebear.com/9.x/icons/svg?seed=131', 'NEW'),
    ( 'TEST', 'FT4', 'Xét nghiệm FT4 định kỳ', 120732.1, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=132', 'SALE'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 209696.66, 'lần', 'ACTIVE', 35, 'https://api.dicebear.com/9.x/icons/svg?seed=133', 'SALE'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 276246.6, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=134', 'NEW'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 220536.76, 'lần', 'ACTIVE', 96, 'https://api.dicebear.com/9.x/icons/svg?seed=135', 'SALE'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 217182.37, 'lần', 'ACTIVE', 17, 'https://api.dicebear.com/9.x/icons/svg?seed=136', 'NEW'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 144217.13, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=137', 'STANDARD'),
    ( 'TEST', 'Testosterone', 'Xét nghiệm Testosterone định kỳ', 298627.52, 'lần', 'ACTIVE', 17, 'https://api.dicebear.com/9.x/icons/svg?seed=138', 'STANDARD'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 166977.58, 'lần', 'ACTIVE', 56, 'https://api.dicebear.com/9.x/icons/svg?seed=139', 'SALE'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 130049.16, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=140', 'STANDARD'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 274651.44, 'lần', 'ACTIVE', 38, 'https://api.dicebear.com/9.x/icons/svg?seed=141', 'STANDARD'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 85439.85, 'lần', 'ACTIVE', 11, 'https://api.dicebear.com/9.x/icons/svg?seed=142', 'SALE'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 251234.46, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=143', 'NEW'),
    ( 'TEST', 'Estradiol', 'Xét nghiệm Estradiol định kỳ', 299924.78, 'lần', 'ACTIVE', 10, 'https://api.dicebear.com/9.x/icons/svg?seed=144', 'SALE'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 212270.71, 'lần', 'ACTIVE', 97, 'https://api.dicebear.com/9.x/icons/svg?seed=145', 'STANDARD'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 226483.9, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=146', 'NEW'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 176375.37, 'lần', 'ACTIVE', 87, 'https://api.dicebear.com/9.x/icons/svg?seed=147', 'NEW'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 193328.43, 'lần', 'ACTIVE', 99, 'https://api.dicebear.com/9.x/icons/svg?seed=148', 'SALE'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 115892.7, 'lần', 'ACTIVE', 86, 'https://api.dicebear.com/9.x/icons/svg?seed=149', 'NEW'),
    ( 'TEST', 'Prolactin', 'Xét nghiệm Prolactin định kỳ', 223026.39, 'lần', 'ACTIVE', 31, 'https://api.dicebear.com/9.x/icons/svg?seed=150', 'SALE'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 253031.94, 'lần', 'ACTIVE', 21, 'https://api.dicebear.com/9.x/icons/svg?seed=151', 'STANDARD'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 202826.6, 'lần', 'ACTIVE', 69, 'https://api.dicebear.com/9.x/icons/svg?seed=152', 'SALE'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 106654.18, 'lần', 'ACTIVE', 43, 'https://api.dicebear.com/9.x/icons/svg?seed=153', 'STANDARD'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 214354.05, 'lần', 'ACTIVE', 49, 'https://api.dicebear.com/9.x/icons/svg?seed=154', 'SALE'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 178235.42, 'lần', 'ACTIVE', 77, 'https://api.dicebear.com/9.x/icons/svg?seed=155', 'STANDARD'),
    ( 'TEST', 'HBeAg', 'Xét nghiệm HBeAg định kỳ', 271347.02, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=156', 'STANDARD'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 149452.94, 'lần', 'ACTIVE', 75, 'https://api.dicebear.com/9.x/icons/svg?seed=157', 'NEW'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 113892.78, 'lần', 'ACTIVE', 41, 'https://api.dicebear.com/9.x/icons/svg?seed=158', 'NEW'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 221799.1, 'lần', 'ACTIVE', 77, 'https://api.dicebear.com/9.x/icons/svg?seed=159', 'NEW'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 220482.94, 'lần', 'ACTIVE', 18, 'https://api.dicebear.com/9.x/icons/svg?seed=160', 'SALE'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 250881.68, 'lần', 'ACTIVE', 15, 'https://api.dicebear.com/9.x/icons/svg?seed=161', 'NEW'),
    ( 'TEST', 'HBsAg', 'Xét nghiệm HBsAg định kỳ', 286650.05, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=162', 'SALE'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 283513.69, 'lần', 'ACTIVE', 31, 'https://api.dicebear.com/9.x/icons/svg?seed=163', 'SALE'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 200790.21, 'lần', 'ACTIVE', 56, 'https://api.dicebear.com/9.x/icons/svg?seed=164', 'NEW'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 276576.82, 'lần', 'ACTIVE', 39, 'https://api.dicebear.com/9.x/icons/svg?seed=165', 'STANDARD'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 280109.06, 'lần', 'ACTIVE', 90, 'https://api.dicebear.com/9.x/icons/svg?seed=166', 'SALE'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 101777.88, 'lần', 'ACTIVE', 38, 'https://api.dicebear.com/9.x/icons/svg?seed=167', 'STANDARD'),
    ( 'TEST', 'Anti-HBs', 'Xét nghiệm Anti-HBs định kỳ', 145809.95, 'lần', 'ACTIVE', 35, 'https://api.dicebear.com/9.x/icons/svg?seed=168', 'NEW'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 143262.25, 'lần', 'ACTIVE', 29, 'https://api.dicebear.com/9.x/icons/svg?seed=169', 'NEW'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 236995.47, 'lần', 'ACTIVE', 23, 'https://api.dicebear.com/9.x/icons/svg?seed=170', 'SALE'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 162311.11, 'lần', 'ACTIVE', 76, 'https://api.dicebear.com/9.x/icons/svg?seed=171', 'SALE'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 207132.68, 'lần', 'ACTIVE', 17, 'https://api.dicebear.com/9.x/icons/svg?seed=172', 'STANDARD'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 113577.05, 'lần', 'ACTIVE', 22, 'https://api.dicebear.com/9.x/icons/svg?seed=173', 'NEW'),
    ( 'TEST', 'Anti-HCV', 'Xét nghiệm Anti-HCV định kỳ', 124831.61, 'lần', 'ACTIVE', 90, 'https://api.dicebear.com/9.x/icons/svg?seed=174', 'STANDARD'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 294550.06, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=175', 'SALE'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 212678.79, 'lần', 'ACTIVE', 78, 'https://api.dicebear.com/9.x/icons/svg?seed=176', 'STANDARD'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 185757.87, 'lần', 'ACTIVE', 100, 'https://api.dicebear.com/9.x/icons/svg?seed=177', 'SALE'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 101903.83, 'lần', 'ACTIVE', 36, 'https://api.dicebear.com/9.x/icons/svg?seed=178', 'NEW'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 104919.14, 'lần', 'ACTIVE', 34, 'https://api.dicebear.com/9.x/icons/svg?seed=179', 'STANDARD'),
    ( 'TEST', 'AFP', 'Xét nghiệm AFP định kỳ', 139053.02, 'lần', 'ACTIVE', 91, 'https://api.dicebear.com/9.x/icons/svg?seed=180', 'STANDARD'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 242137.64, 'lần', 'ACTIVE', 34, 'https://api.dicebear.com/9.x/icons/svg?seed=181', 'STANDARD'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 178493.6, 'lần', 'ACTIVE', 97, 'https://api.dicebear.com/9.x/icons/svg?seed=182', 'SALE'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 100529.4, 'lần', 'ACTIVE', 18, 'https://api.dicebear.com/9.x/icons/svg?seed=183', 'SALE'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 165618.48, 'lần', 'ACTIVE', 60, 'https://api.dicebear.com/9.x/icons/svg?seed=184', 'NEW'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 107261.29, 'lần', 'ACTIVE', 96, 'https://api.dicebear.com/9.x/icons/svg?seed=185', 'NEW'),
    ( 'TEST', 'CEA', 'Xét nghiệm CEA định kỳ', 289802.26, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=186', 'NEW'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 192366.79, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=187', 'NEW'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 153976.53, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=188', 'NEW'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 171500.2, 'lần', 'ACTIVE', 81, 'https://api.dicebear.com/9.x/icons/svg?seed=189', 'STANDARD'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 273272.47, 'lần', 'ACTIVE', 73, 'https://api.dicebear.com/9.x/icons/svg?seed=190', 'SALE'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 262669.75, 'lần', 'ACTIVE', 80, 'https://api.dicebear.com/9.x/icons/svg?seed=191', 'STANDARD'),
    ( 'TEST', 'CA 19-9', 'Xét nghiệm CA 19-9 định kỳ', 192498.73, 'lần', 'ACTIVE', 75, 'https://api.dicebear.com/9.x/icons/svg?seed=192', 'SALE'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 188808.4, 'lần', 'ACTIVE', 100, 'https://api.dicebear.com/9.x/icons/svg?seed=193', 'NEW'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 279129.79, 'lần', 'ACTIVE', 65, 'https://api.dicebear.com/9.x/icons/svg?seed=194', 'NEW'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 294341.71, 'lần', 'ACTIVE', 100, 'https://api.dicebear.com/9.x/icons/svg?seed=195', 'NEW'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 216902.56, 'lần', 'ACTIVE', 35, 'https://api.dicebear.com/9.x/icons/svg?seed=196', 'STANDARD'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 281031.54, 'lần', 'ACTIVE', 93, 'https://api.dicebear.com/9.x/icons/svg?seed=197', 'NEW'),
    ( 'TEST', 'CA 125', 'Xét nghiệm CA 125 định kỳ', 254706.65, 'lần', 'ACTIVE', 49, 'https://api.dicebear.com/9.x/icons/svg?seed=198', 'NEW'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 136282.26, 'lần', 'ACTIVE', 16, 'https://api.dicebear.com/9.x/icons/svg?seed=199', 'SALE'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 100311.7, 'lần', 'ACTIVE', 61, 'https://api.dicebear.com/9.x/icons/svg?seed=200', 'STANDARD'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 110923.19, 'lần', 'ACTIVE', 61, 'https://api.dicebear.com/9.x/icons/svg?seed=201', 'SALE'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 103551.89, 'lần', 'ACTIVE', 22, 'https://api.dicebear.com/9.x/icons/svg?seed=202', 'NEW'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 286326.21, 'lần', 'ACTIVE', 33, 'https://api.dicebear.com/9.x/icons/svg?seed=203', 'STANDARD'),
    ( 'TEST', 'PSA toàn phần', 'Xét nghiệm PSA toàn phần định kỳ', 177297.11, 'lần', 'ACTIVE', 75, 'https://api.dicebear.com/9.x/icons/svg?seed=204', 'STANDARD'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 136815.8, 'lần', 'ACTIVE', 40, 'https://api.dicebear.com/9.x/icons/svg?seed=205', 'NEW'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 82654.66, 'lần', 'ACTIVE', 71, 'https://api.dicebear.com/9.x/icons/svg?seed=206', 'SALE'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 222507.61, 'lần', 'ACTIVE', 65, 'https://api.dicebear.com/9.x/icons/svg?seed=207', 'SALE'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 200518.96, 'lần', 'ACTIVE', 13, 'https://api.dicebear.com/9.x/icons/svg?seed=208', 'STANDARD'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 284574.21, 'lần', 'ACTIVE', 71, 'https://api.dicebear.com/9.x/icons/svg?seed=209', 'NEW'),
    ( 'TEST', 'PSA tự do', 'Xét nghiệm PSA tự do định kỳ', 111875.98, 'lần', 'ACTIVE', 44, 'https://api.dicebear.com/9.x/icons/svg?seed=210', 'NEW');

INSERT INTO tests (product_id, gender, age_min, age_max, normal_min, normal_max, test_unit, note)
VALUES
    ( 1, 'MALE', 0, 1, 3.67, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 0, 1, 3.18, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 2, 5, 3.63, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 2, 5, 3.33, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 6, 12, 3.72, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 6, 12, 3.87, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 13, 18, 4.2, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 13, 18, 3.22, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 19, 35, 4.58, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 19, 35, 3.76, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 36, 50, 4.56, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 36, 50, 3.31, 4.34, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 51, 65, 4.05, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 51, 65, 3.04, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 66, 80, 4.21, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 66, 80, 3.26, 4.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'MALE', 81, 120, 3.2, 4.48, 'mg/dL', 'Giá trị bình thường'),
    ( 1, 'FEMALE', 81, 120, 4.81, 7.27, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 0, 1, 4.99, 6.74, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 0, 1, 3.77, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 2, 5, 4.75, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 2, 5, 3.95, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 6, 12, 4.72, 6.47, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 6, 12, 4.14, 6.369999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 13, 18, 3.9, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 13, 18, 4.07, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 19, 35, 4.89, 7.07, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 19, 35, 4.3, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 36, 50, 4.45, 6.74, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 36, 50, 3.79, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 51, 65, 3.33, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 51, 65, 4.92, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 66, 80, 4.73, 6.340000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 66, 80, 4.37, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'MALE', 81, 120, 3.12, 4.37, 'mg/dL', 'Giá trị bình thường'),
    ( 2, 'FEMALE', 81, 120, 3.09, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 0, 1, 4.97, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 0, 1, 3.91, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 2, 5, 3.07, 5.22, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 2, 5, 4.93, 7.24, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 6, 12, 3.63, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 6, 12, 3.94, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 13, 18, 4.68, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 13, 18, 4.04, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 19, 35, 4.1, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 19, 35, 4.95, 6.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 36, 50, 3.26, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 36, 50, 3.2, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 51, 65, 3.76, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 51, 65, 4.58, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 66, 80, 3.93, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 66, 80, 4.55, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'MALE', 81, 120, 4.65, 5.8500000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 3, 'FEMALE', 81, 120, 4.46, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 0, 1, 4.28, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 0, 1, 3.91, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 2, 5, 3.23, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 2, 5, 3.08, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 6, 12, 4.23, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 6, 12, 4.94, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 13, 18, 3.08, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 13, 18, 4.99, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 19, 35, 3.52, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 19, 35, 3.71, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 36, 50, 3.94, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 36, 50, 4.95, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 51, 65, 3.52, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 51, 65, 3.46, 4.78, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 66, 80, 4.42, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 66, 80, 4.84, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'MALE', 81, 120, 3.71, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 4, 'FEMALE', 81, 120, 4.76, 6.98, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 0, 1, 4.88, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 0, 1, 4.46, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 2, 5, 3.38, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 2, 5, 4.04, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 6, 12, 3.72, 5.44, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 6, 12, 4.44, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 13, 18, 4.9, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 13, 18, 4.61, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 19, 35, 3.97, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 19, 35, 4.55, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 36, 50, 3.57, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 36, 50, 4.21, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 51, 65, 3.29, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 51, 65, 3.52, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 66, 80, 4.05, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 66, 80, 3.16, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'MALE', 81, 120, 3.4, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 5, 'FEMALE', 81, 120, 3.98, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 0, 1, 3.14, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 0, 1, 4.02, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 2, 5, 4.55, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 2, 5, 4.13, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 6, 12, 4.26, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 6, 12, 3.76, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 13, 18, 3.59, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 13, 18, 3.17, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 19, 35, 4.11, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 19, 35, 4.77, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 36, 50, 3.97, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 36, 50, 4.1, 6.379999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 51, 65, 4.55, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 51, 65, 3.67, 4.81, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 66, 80, 3.19, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 66, 80, 4.41, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'MALE', 81, 120, 3.86, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 6, 'FEMALE', 81, 120, 3.17, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 0, 1, 4.6, 6.539999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 0, 1, 4.5, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 2, 5, 4.76, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 2, 5, 4.04, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 6, 12, 3.63, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 6, 12, 4.1, 6.069999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 13, 18, 3.64, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 13, 18, 3.67, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 19, 35, 5.0, 7.48, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 19, 35, 3.29, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 36, 50, 4.62, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 36, 50, 3.79, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 51, 65, 4.43, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 51, 65, 4.12, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 66, 80, 3.44, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 66, 80, 3.55, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'MALE', 81, 120, 4.78, 7.17, 'mg/dL', 'Giá trị bình thường'),
    ( 7, 'FEMALE', 81, 120, 4.65, 6.510000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 0, 1, 4.07, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 0, 1, 3.29, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 2, 5, 3.82, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 2, 5, 4.86, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 6, 12, 3.18, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 6, 12, 3.82, 6.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 13, 18, 3.38, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 13, 18, 3.34, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 19, 35, 3.57, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 19, 35, 4.51, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 36, 50, 3.24, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 36, 50, 4.08, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 51, 65, 4.8, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 51, 65, 4.52, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 66, 80, 4.45, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 66, 80, 4.92, 7.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'MALE', 81, 120, 3.18, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 8, 'FEMALE', 81, 120, 3.43, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 0, 1, 4.06, 5.239999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 0, 1, 3.77, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 2, 5, 3.84, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 2, 5, 4.18, 6.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 6, 12, 3.24, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 6, 12, 3.69, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 13, 18, 3.53, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 13, 18, 4.28, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 19, 35, 3.34, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 19, 35, 3.82, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 36, 50, 3.37, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 36, 50, 4.95, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 51, 65, 4.62, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 51, 65, 3.08, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 66, 80, 4.58, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 66, 80, 4.65, 6.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'MALE', 81, 120, 4.74, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 9, 'FEMALE', 81, 120, 3.94, 6.37, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 0, 1, 4.17, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 0, 1, 3.57, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 2, 5, 4.04, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 2, 5, 3.82, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 6, 12, 3.76, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 6, 12, 3.61, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 13, 18, 4.55, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 13, 18, 3.27, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 19, 35, 4.98, 7.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 19, 35, 4.66, 6.98, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 36, 50, 3.81, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 36, 50, 4.31, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 51, 65, 3.05, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 51, 65, 4.6, 6.789999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 66, 80, 3.91, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 66, 80, 3.61, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'MALE', 81, 120, 4.36, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 10, 'FEMALE', 81, 120, 4.41, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 0, 1, 4.36, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 0, 1, 3.82, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 2, 5, 4.83, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 2, 5, 4.7, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 6, 12, 4.0, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 6, 12, 3.12, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 13, 18, 3.62, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 13, 18, 3.94, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 19, 35, 4.1, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 19, 35, 3.95, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 36, 50, 3.94, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 36, 50, 3.18, 4.69, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 51, 65, 3.86, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 51, 65, 3.98, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 66, 80, 4.05, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 66, 80, 3.79, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'MALE', 81, 120, 4.39, 6.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 11, 'FEMALE', 81, 120, 4.35, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 0, 1, 4.88, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 0, 1, 4.57, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 2, 5, 4.34, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 2, 5, 4.38, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 6, 12, 4.18, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 6, 12, 3.56, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 13, 18, 3.08, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 13, 18, 3.16, 4.69, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 19, 35, 3.76, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 19, 35, 3.05, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 36, 50, 4.99, 6.86, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 36, 50, 4.53, 7.03, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 51, 65, 3.99, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 51, 65, 3.28, 4.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 66, 80, 4.14, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 66, 80, 3.31, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'MALE', 81, 120, 3.53, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 12, 'FEMALE', 81, 120, 3.25, 4.48, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 0, 1, 3.72, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 0, 1, 3.21, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 2, 5, 3.28, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 2, 5, 3.04, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 6, 12, 4.68, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 6, 12, 4.68, 6.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 13, 18, 3.79, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 13, 18, 4.76, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 19, 35, 3.56, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 19, 35, 4.45, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 36, 50, 3.32, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 36, 50, 3.15, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 51, 65, 4.03, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 51, 65, 3.44, 4.47, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 66, 80, 3.11, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 66, 80, 3.95, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'MALE', 81, 120, 4.62, 7.08, 'mg/dL', 'Giá trị bình thường'),
    ( 13, 'FEMALE', 81, 120, 4.97, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 0, 1, 3.93, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 0, 1, 4.48, 6.040000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 2, 5, 4.64, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 2, 5, 4.23, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 6, 12, 3.84, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 6, 12, 4.04, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 13, 18, 4.98, 7.36, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 13, 18, 3.27, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 19, 35, 4.77, 6.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 19, 35, 3.32, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 36, 50, 4.18, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 36, 50, 4.33, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 51, 65, 4.65, 6.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 51, 65, 4.55, 6.81, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 66, 80, 4.58, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 66, 80, 4.75, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'MALE', 81, 120, 4.16, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 14, 'FEMALE', 81, 120, 4.27, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 0, 1, 4.17, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 0, 1, 3.27, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 2, 5, 3.7, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 2, 5, 3.63, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 6, 12, 3.14, 4.23, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 6, 12, 4.0, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 13, 18, 4.92, 7.41, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 13, 18, 4.66, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 19, 35, 4.6, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 19, 35, 3.09, 4.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 36, 50, 4.34, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 36, 50, 3.09, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 51, 65, 3.57, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 51, 65, 4.82, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 66, 80, 4.66, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 66, 80, 4.29, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'MALE', 81, 120, 3.62, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 15, 'FEMALE', 81, 120, 3.5, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 0, 1, 3.88, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 0, 1, 4.66, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 2, 5, 3.87, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 2, 5, 4.23, 5.460000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 6, 12, 4.12, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 6, 12, 3.11, 4.25, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 13, 18, 3.66, 4.87, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 13, 18, 4.34, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 19, 35, 3.15, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 19, 35, 4.2, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 36, 50, 3.62, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 36, 50, 4.93, 7.38, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 51, 65, 4.91, 7.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 51, 65, 3.11, 4.18, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 66, 80, 4.97, 7.35, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 66, 80, 3.32, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'MALE', 81, 120, 4.11, 5.11, 'mg/dL', 'Giá trị bình thường'),
    ( 16, 'FEMALE', 81, 120, 4.63, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 0, 1, 3.75, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 0, 1, 3.13, 5.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 2, 5, 3.98, 6.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 2, 5, 4.97, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 6, 12, 3.43, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 6, 12, 3.78, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 13, 18, 3.34, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 13, 18, 4.05, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 19, 35, 4.33, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 19, 35, 4.64, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 36, 50, 4.0, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 36, 50, 4.63, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 51, 65, 3.51, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 51, 65, 4.29, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 66, 80, 4.41, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 66, 80, 4.12, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'MALE', 81, 120, 4.31, 6.379999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 17, 'FEMALE', 81, 120, 4.91, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 0, 1, 3.6, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 0, 1, 4.69, 7.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 2, 5, 4.7, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 2, 5, 4.9, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 6, 12, 3.41, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 6, 12, 3.39, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 13, 18, 3.93, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 13, 18, 3.32, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 19, 35, 4.89, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 19, 35, 4.68, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 36, 50, 4.57, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 36, 50, 3.69, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 51, 65, 3.65, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 51, 65, 3.81, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 66, 80, 3.85, 5.44, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 66, 80, 4.82, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'MALE', 81, 120, 3.88, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 18, 'FEMALE', 81, 120, 3.84, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 0, 1, 4.98, 7.41, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 0, 1, 3.19, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 2, 5, 3.52, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 2, 5, 4.29, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 6, 12, 3.42, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 6, 12, 4.06, 6.459999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 13, 18, 3.89, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 13, 18, 4.33, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 19, 35, 4.07, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 19, 35, 4.52, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 36, 50, 3.68, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 36, 50, 4.12, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 51, 65, 4.1, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 51, 65, 4.45, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 66, 80, 4.75, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 66, 80, 3.73, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'MALE', 81, 120, 3.8, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 19, 'FEMALE', 81, 120, 4.61, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 0, 1, 3.04, 4.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 0, 1, 3.59, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 2, 5, 3.04, 4.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 2, 5, 3.79, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 6, 12, 3.65, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 6, 12, 4.2, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 13, 18, 4.56, 6.539999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 13, 18, 4.87, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 19, 35, 3.59, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 19, 35, 4.74, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 36, 50, 4.97, 6.64, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 36, 50, 4.54, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 51, 65, 3.86, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 51, 65, 4.64, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 66, 80, 4.2, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 66, 80, 3.71, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'MALE', 81, 120, 3.17, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 20, 'FEMALE', 81, 120, 4.89, 7.01, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 0, 1, 3.35, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 0, 1, 4.46, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 2, 5, 4.56, 6.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 2, 5, 3.61, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 6, 12, 4.41, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 6, 12, 3.33, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 13, 18, 3.88, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 13, 18, 4.24, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 19, 35, 3.92, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 19, 35, 3.29, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 36, 50, 3.26, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 36, 50, 4.79, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 51, 65, 3.57, 5.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 51, 65, 4.42, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 66, 80, 4.15, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 66, 80, 3.79, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'MALE', 81, 120, 3.18, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 21, 'FEMALE', 81, 120, 4.49, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 0, 1, 3.83, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 0, 1, 3.08, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 2, 5, 4.22, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 2, 5, 3.38, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 6, 12, 4.59, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 6, 12, 3.65, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 13, 18, 3.85, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 13, 18, 3.8, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 19, 35, 4.64, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 19, 35, 3.8, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 36, 50, 4.19, 5.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 36, 50, 4.75, 6.96, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 51, 65, 3.23, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 51, 65, 4.77, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 66, 80, 4.44, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 66, 80, 3.53, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'MALE', 81, 120, 3.03, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 22, 'FEMALE', 81, 120, 3.28, 4.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 0, 1, 3.57, 4.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 0, 1, 3.22, 4.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 2, 5, 4.53, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 2, 5, 3.29, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 6, 12, 4.83, 7.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 6, 12, 3.44, 4.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 13, 18, 4.79, 7.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 13, 18, 4.47, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 19, 35, 4.71, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 19, 35, 4.48, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 36, 50, 4.97, 6.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 36, 50, 3.78, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 51, 65, 3.79, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 51, 65, 3.43, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 66, 80, 3.07, 4.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 66, 80, 4.4, 6.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'MALE', 81, 120, 4.71, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 23, 'FEMALE', 81, 120, 3.47, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 0, 1, 3.14, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 0, 1, 3.2, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 2, 5, 3.22, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 2, 5, 4.29, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 6, 12, 3.4, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 6, 12, 3.89, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 13, 18, 3.37, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 13, 18, 4.86, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 19, 35, 3.19, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 19, 35, 4.28, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 36, 50, 4.83, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 36, 50, 4.67, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 51, 65, 4.1, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 51, 65, 3.79, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 66, 80, 4.44, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 66, 80, 3.15, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'MALE', 81, 120, 4.1, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 24, 'FEMALE', 81, 120, 4.31, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 0, 1, 3.18, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 0, 1, 4.8, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 2, 5, 3.97, 5.11, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 2, 5, 3.21, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 6, 12, 4.84, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 6, 12, 3.92, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 13, 18, 3.81, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 13, 18, 4.6, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 19, 35, 4.71, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 19, 35, 3.46, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 36, 50, 3.69, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 36, 50, 3.83, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 51, 65, 4.21, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 51, 65, 3.72, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 66, 80, 4.88, 7.34, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 66, 80, 3.64, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'MALE', 81, 120, 4.29, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 25, 'FEMALE', 81, 120, 4.96, 6.95, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 0, 1, 3.75, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 0, 1, 3.42, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 2, 5, 4.44, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 2, 5, 4.16, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 6, 12, 3.47, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 6, 12, 4.55, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 13, 18, 3.73, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 13, 18, 4.95, 6.19, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 19, 35, 4.8, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 19, 35, 3.61, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 36, 50, 3.23, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 36, 50, 4.19, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 51, 65, 4.06, 5.8999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 51, 65, 3.15, 4.81, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 66, 80, 3.48, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 66, 80, 3.54, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'MALE', 81, 120, 4.72, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 26, 'FEMALE', 81, 120, 3.84, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 0, 1, 4.6, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 0, 1, 3.9, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 2, 5, 4.71, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 2, 5, 3.72, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 6, 12, 3.05, 4.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 6, 12, 4.2, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 13, 18, 3.29, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 13, 18, 4.19, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 19, 35, 4.15, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 19, 35, 3.54, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 36, 50, 4.48, 5.760000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 36, 50, 4.4, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 51, 65, 3.3, 5.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 51, 65, 3.38, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 66, 80, 4.66, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 66, 80, 4.38, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'MALE', 81, 120, 3.31, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 27, 'FEMALE', 81, 120, 4.68, 6.1499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 0, 1, 4.76, 6.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 0, 1, 4.26, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 2, 5, 4.31, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 2, 5, 3.33, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 6, 12, 4.07, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 6, 12, 3.86, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 13, 18, 3.36, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 13, 18, 3.07, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 19, 35, 4.41, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 19, 35, 4.54, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 36, 50, 4.95, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 36, 50, 3.5, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 51, 65, 4.2, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 51, 65, 3.54, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 66, 80, 4.16, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 66, 80, 3.77, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'MALE', 81, 120, 4.0, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 28, 'FEMALE', 81, 120, 3.14, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 0, 1, 4.79, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 0, 1, 4.72, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 2, 5, 4.3, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 2, 5, 3.67, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 6, 12, 4.65, 5.69, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 6, 12, 3.59, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 13, 18, 3.47, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 13, 18, 4.01, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 19, 35, 4.71, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 19, 35, 4.05, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 36, 50, 4.79, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 36, 50, 3.01, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 51, 65, 3.22, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 51, 65, 3.65, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 66, 80, 3.71, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 66, 80, 3.71, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'MALE', 81, 120, 4.11, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 29, 'FEMALE', 81, 120, 4.08, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 0, 1, 4.93, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 0, 1, 3.9, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 2, 5, 3.84, 4.89, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 2, 5, 3.78, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 6, 12, 4.23, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 6, 12, 3.66, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 13, 18, 4.35, 5.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 13, 18, 4.73, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 19, 35, 3.47, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 19, 35, 4.25, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 36, 50, 4.97, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 36, 50, 3.03, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 51, 65, 4.32, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 51, 65, 4.98, 7.41, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 66, 80, 4.82, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 66, 80, 3.46, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'MALE', 81, 120, 4.69, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 30, 'FEMALE', 81, 120, 3.76, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 0, 1, 3.21, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 0, 1, 3.66, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 2, 5, 3.33, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 2, 5, 4.94, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 6, 12, 4.21, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 6, 12, 3.22, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 13, 18, 3.47, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 13, 18, 3.41, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 19, 35, 3.16, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 19, 35, 4.86, 7.24, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 36, 50, 3.85, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 36, 50, 3.69, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 51, 65, 4.74, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 51, 65, 4.93, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 66, 80, 3.06, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 66, 80, 3.1, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'MALE', 81, 120, 3.79, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 31, 'FEMALE', 81, 120, 4.92, 6.47, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 0, 1, 4.64, 7.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 0, 1, 3.07, 4.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 2, 5, 4.26, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 2, 5, 4.78, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 6, 12, 3.19, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 6, 12, 4.58, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 13, 18, 3.2, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 13, 18, 4.05, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 19, 35, 4.9, 7.26, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 19, 35, 3.16, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 36, 50, 4.13, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 36, 50, 3.95, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 51, 65, 4.0, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 51, 65, 3.31, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 66, 80, 3.26, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 66, 80, 4.65, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'MALE', 81, 120, 3.04, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 32, 'FEMALE', 81, 120, 4.92, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 0, 1, 4.53, 6.98, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 0, 1, 3.53, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 2, 5, 3.52, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 2, 5, 4.62, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 6, 12, 4.37, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 6, 12, 4.28, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 13, 18, 4.07, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 13, 18, 3.14, 5.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 19, 35, 4.94, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 19, 35, 4.48, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 36, 50, 4.7, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 36, 50, 3.67, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 51, 65, 3.96, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 51, 65, 3.13, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 66, 80, 4.74, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 66, 80, 3.59, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'MALE', 81, 120, 3.16, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 33, 'FEMALE', 81, 120, 3.98, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 0, 1, 4.49, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 0, 1, 3.82, 4.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 2, 5, 4.63, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 2, 5, 3.5, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 6, 12, 4.89, 7.14, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 6, 12, 4.85, 5.89, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 13, 18, 3.43, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 13, 18, 3.66, 5.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 19, 35, 3.24, 5.44, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 19, 35, 4.26, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 36, 50, 3.68, 5.03, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 36, 50, 3.24, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 51, 65, 3.26, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 51, 65, 3.5, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 66, 80, 3.51, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 66, 80, 4.66, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'MALE', 81, 120, 3.55, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 34, 'FEMALE', 81, 120, 4.58, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 0, 1, 3.92, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 0, 1, 3.52, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 2, 5, 3.94, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 2, 5, 4.8, 7.22, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 6, 12, 5.0, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 6, 12, 4.3, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 13, 18, 4.02, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 13, 18, 3.65, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 19, 35, 4.65, 6.95, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 19, 35, 4.8, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 36, 50, 3.62, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 36, 50, 4.4, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 51, 65, 4.87, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 51, 65, 4.24, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 66, 80, 4.03, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 66, 80, 3.89, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'MALE', 81, 120, 4.0, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 35, 'FEMALE', 81, 120, 4.11, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 0, 1, 4.1, 5.1499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 0, 1, 4.9, 7.28, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 2, 5, 3.2, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 2, 5, 3.42, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 6, 12, 3.57, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 6, 12, 4.3, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 13, 18, 3.21, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 13, 18, 3.66, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 19, 35, 4.32, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 19, 35, 3.47, 4.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 36, 50, 3.42, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 36, 50, 4.68, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 51, 65, 4.04, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 51, 65, 4.51, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 66, 80, 4.73, 5.840000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 66, 80, 4.95, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'MALE', 81, 120, 4.43, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 36, 'FEMALE', 81, 120, 4.64, 5.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 0, 1, 3.75, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 0, 1, 4.64, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 2, 5, 3.63, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 2, 5, 4.08, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 6, 12, 4.25, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 6, 12, 3.35, 4.54, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 13, 18, 4.58, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 13, 18, 4.92, 7.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 19, 35, 4.84, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 19, 35, 4.69, 6.99, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 36, 50, 3.15, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 36, 50, 4.0, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 51, 65, 4.89, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 51, 65, 3.87, 6.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 66, 80, 4.36, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 66, 80, 3.46, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'MALE', 81, 120, 4.0, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 37, 'FEMALE', 81, 120, 4.83, 6.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 0, 1, 4.24, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 0, 1, 3.46, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 2, 5, 3.84, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 2, 5, 3.01, 4.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 6, 12, 4.74, 6.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 6, 12, 3.94, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 13, 18, 3.01, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 13, 18, 3.81, 4.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 19, 35, 4.82, 7.040000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 19, 35, 4.62, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 36, 50, 3.15, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 36, 50, 3.61, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 51, 65, 3.85, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 51, 65, 3.58, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 66, 80, 3.88, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 66, 80, 4.09, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'MALE', 81, 120, 4.32, 6.1000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 38, 'FEMALE', 81, 120, 3.29, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 0, 1, 4.55, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 0, 1, 4.93, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 2, 5, 3.5, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 2, 5, 3.06, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 6, 12, 4.81, 5.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 6, 12, 4.35, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 13, 18, 3.21, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 13, 18, 4.89, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 19, 35, 3.8, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 19, 35, 4.17, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 36, 50, 3.27, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 36, 50, 3.54, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 51, 65, 3.5, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 51, 65, 3.33, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 66, 80, 3.51, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 66, 80, 4.58, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'MALE', 81, 120, 4.55, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 39, 'FEMALE', 81, 120, 4.26, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 0, 1, 4.63, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 0, 1, 3.46, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 2, 5, 3.5, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 2, 5, 3.83, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 6, 12, 4.98, 7.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 6, 12, 4.26, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 13, 18, 3.49, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 13, 18, 3.26, 5.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 19, 35, 3.4, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 19, 35, 3.28, 4.31, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 36, 50, 3.67, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 36, 50, 4.73, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 51, 65, 4.7, 7.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 51, 65, 3.28, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 66, 80, 4.09, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 66, 80, 4.94, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'MALE', 81, 120, 4.32, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 40, 'FEMALE', 81, 120, 3.06, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 0, 1, 3.92, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 0, 1, 5.0, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 2, 5, 3.72, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 2, 5, 4.45, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 6, 12, 3.31, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 6, 12, 4.69, 6.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 13, 18, 3.6, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 13, 18, 4.29, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 19, 35, 4.22, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 19, 35, 4.18, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 36, 50, 3.49, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 36, 50, 4.53, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 51, 65, 3.9, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 51, 65, 3.16, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 66, 80, 3.88, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 66, 80, 4.27, 5.569999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'MALE', 81, 120, 4.4, 5.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 41, 'FEMALE', 81, 120, 3.04, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 0, 1, 4.34, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 0, 1, 3.39, 4.41, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 2, 5, 3.03, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 2, 5, 3.84, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 6, 12, 3.51, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 6, 12, 3.65, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 13, 18, 3.52, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 13, 18, 3.16, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 19, 35, 4.78, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 19, 35, 4.21, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 36, 50, 4.45, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 36, 50, 3.19, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 51, 65, 3.85, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 51, 65, 4.2, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 66, 80, 4.84, 7.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 66, 80, 3.78, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'MALE', 81, 120, 3.18, 5.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 42, 'FEMALE', 81, 120, 4.84, 7.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 0, 1, 4.84, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 0, 1, 4.52, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 2, 5, 3.17, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 2, 5, 3.1, 4.55, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 6, 12, 3.71, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 6, 12, 4.99, 6.3500000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 13, 18, 3.15, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 13, 18, 3.83, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 19, 35, 3.78, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 19, 35, 3.72, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 36, 50, 3.8, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 36, 50, 3.58, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 51, 65, 4.22, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 51, 65, 4.86, 6.960000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 66, 80, 3.91, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 66, 80, 4.34, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'MALE', 81, 120, 3.11, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 43, 'FEMALE', 81, 120, 4.12, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 0, 1, 4.52, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 0, 1, 3.88, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 2, 5, 4.38, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 2, 5, 4.94, 7.42, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 6, 12, 4.52, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 6, 12, 4.73, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 13, 18, 3.18, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 13, 18, 4.98, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 19, 35, 3.02, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 19, 35, 3.08, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 36, 50, 4.68, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 36, 50, 3.17, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 51, 65, 4.65, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 51, 65, 4.13, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 66, 80, 3.4, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 66, 80, 3.66, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'MALE', 81, 120, 4.88, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 44, 'FEMALE', 81, 120, 3.74, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 0, 1, 3.45, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 0, 1, 3.25, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 2, 5, 4.7, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 2, 5, 3.53, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 6, 12, 4.8, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 6, 12, 3.98, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 13, 18, 4.28, 6.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 13, 18, 4.97, 6.81, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 19, 35, 3.2, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 19, 35, 3.87, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 36, 50, 4.0, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 36, 50, 3.12, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 51, 65, 3.06, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 51, 65, 3.78, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 66, 80, 3.87, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 66, 80, 4.16, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'MALE', 81, 120, 3.81, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 45, 'FEMALE', 81, 120, 3.77, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 0, 1, 4.2, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 0, 1, 3.4, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 2, 5, 3.33, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 2, 5, 4.26, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 6, 12, 3.31, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 6, 12, 4.26, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 13, 18, 4.2, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 13, 18, 4.18, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 19, 35, 3.93, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 19, 35, 3.54, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 36, 50, 4.68, 6.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 36, 50, 3.07, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 51, 65, 3.16, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 51, 65, 4.37, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 66, 80, 4.52, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 66, 80, 4.52, 5.909999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'MALE', 81, 120, 4.14, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 46, 'FEMALE', 81, 120, 4.4, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 0, 1, 3.6, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 0, 1, 3.03, 4.13, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 2, 5, 4.13, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 2, 5, 3.18, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 6, 12, 3.82, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 6, 12, 3.34, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 13, 18, 3.48, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 13, 18, 4.93, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 19, 35, 4.69, 6.040000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 19, 35, 4.64, 6.6499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 36, 50, 3.53, 5.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 36, 50, 4.17, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 51, 65, 4.23, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 51, 65, 3.13, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 66, 80, 4.45, 5.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 66, 80, 3.07, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'MALE', 81, 120, 4.62, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 47, 'FEMALE', 81, 120, 3.67, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 0, 1, 4.3, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 0, 1, 4.78, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 2, 5, 3.78, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 2, 5, 4.6, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 6, 12, 3.54, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 6, 12, 3.91, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 13, 18, 4.0, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 13, 18, 3.57, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 19, 35, 4.11, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 19, 35, 4.88, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 36, 50, 4.72, 6.88, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 36, 50, 4.73, 7.200000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 51, 65, 4.87, 7.25, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 51, 65, 3.25, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 66, 80, 4.83, 7.15, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 66, 80, 4.17, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'MALE', 81, 120, 3.7, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 48, 'FEMALE', 81, 120, 4.07, 6.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 0, 1, 4.77, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 0, 1, 3.67, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 2, 5, 3.49, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 2, 5, 3.07, 4.12, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 6, 12, 4.27, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 6, 12, 3.5, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 13, 18, 4.01, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 13, 18, 4.08, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 19, 35, 4.01, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 19, 35, 4.65, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 36, 50, 4.93, 6.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 36, 50, 3.95, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 51, 65, 3.63, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 51, 65, 4.2, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 66, 80, 3.33, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 66, 80, 4.42, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'MALE', 81, 120, 3.85, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 49, 'FEMALE', 81, 120, 3.3, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 0, 1, 4.06, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 0, 1, 4.06, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 2, 5, 3.45, 4.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 2, 5, 4.05, 6.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 6, 12, 3.8, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 6, 12, 3.84, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 13, 18, 3.53, 5.22, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 13, 18, 4.37, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 19, 35, 3.57, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 19, 35, 3.48, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 36, 50, 4.91, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 36, 50, 4.51, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 51, 65, 4.0, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 51, 65, 4.44, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 66, 80, 4.64, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 66, 80, 4.05, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'MALE', 81, 120, 4.57, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 50, 'FEMALE', 81, 120, 3.16, 4.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 0, 1, 4.26, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 0, 1, 3.44, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 2, 5, 3.69, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 2, 5, 3.99, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 6, 12, 3.1, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 6, 12, 4.96, 7.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 13, 18, 4.0, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 13, 18, 4.42, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 19, 35, 4.86, 6.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 19, 35, 4.89, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 36, 50, 4.32, 5.680000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 36, 50, 4.04, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 51, 65, 3.79, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 51, 65, 3.98, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 66, 80, 3.66, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 66, 80, 3.05, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'MALE', 81, 120, 3.88, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 51, 'FEMALE', 81, 120, 4.71, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 0, 1, 3.44, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 0, 1, 3.56, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 2, 5, 4.07, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 2, 5, 4.8, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 6, 12, 4.27, 6.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 6, 12, 4.12, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 13, 18, 3.56, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 13, 18, 3.11, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 19, 35, 3.04, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 19, 35, 3.98, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 36, 50, 3.1, 4.69, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 36, 50, 3.73, 5.22, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 51, 65, 4.81, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 51, 65, 3.83, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 66, 80, 4.5, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 66, 80, 3.69, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'MALE', 81, 120, 4.46, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 52, 'FEMALE', 81, 120, 3.51, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 0, 1, 3.27, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 0, 1, 4.69, 6.180000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 2, 5, 3.75, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 2, 5, 4.02, 5.239999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 6, 12, 4.97, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 6, 12, 4.75, 6.55, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 13, 18, 4.89, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 13, 18, 3.13, 4.26, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 19, 35, 3.11, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 19, 35, 4.8, 7.04, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 36, 50, 3.04, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 36, 50, 4.69, 7.17, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 51, 65, 4.42, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 51, 65, 4.37, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 66, 80, 4.75, 7.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 66, 80, 3.41, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'MALE', 81, 120, 4.09, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 53, 'FEMALE', 81, 120, 3.68, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 0, 1, 4.08, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 0, 1, 3.38, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 2, 5, 3.39, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 2, 5, 3.23, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 6, 12, 4.15, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 6, 12, 3.14, 4.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 13, 18, 4.72, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 13, 18, 4.77, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 19, 35, 4.55, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 19, 35, 4.95, 7.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 36, 50, 4.21, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 36, 50, 4.24, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 51, 65, 3.87, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 51, 65, 4.59, 6.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 66, 80, 3.7, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 66, 80, 3.96, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'MALE', 81, 120, 4.98, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 54, 'FEMALE', 81, 120, 4.18, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 0, 1, 4.82, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 0, 1, 3.68, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 2, 5, 3.95, 5.19, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 2, 5, 3.81, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 6, 12, 3.98, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 6, 12, 3.27, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 13, 18, 4.46, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 13, 18, 3.93, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 19, 35, 4.27, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 19, 35, 4.15, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 36, 50, 3.32, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 36, 50, 3.45, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 51, 65, 3.34, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 51, 65, 4.79, 7.24, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 66, 80, 3.82, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 66, 80, 3.32, 4.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'MALE', 81, 120, 4.9, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 55, 'FEMALE', 81, 120, 4.71, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 0, 1, 4.86, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 0, 1, 3.23, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 2, 5, 3.64, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 2, 5, 3.48, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 6, 12, 3.19, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 6, 12, 3.02, 4.25, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 13, 18, 3.24, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 13, 18, 4.44, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 19, 35, 3.11, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 19, 35, 4.26, 5.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 36, 50, 3.71, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 36, 50, 3.65, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 51, 65, 3.92, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 51, 65, 4.72, 6.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 66, 80, 3.27, 5.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 66, 80, 3.72, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'MALE', 81, 120, 3.3, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 56, 'FEMALE', 81, 120, 4.84, 6.89, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 0, 1, 3.01, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 0, 1, 4.35, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 2, 5, 4.47, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 2, 5, 4.58, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 6, 12, 4.1, 5.539999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 6, 12, 4.29, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 13, 18, 3.52, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 13, 18, 3.23, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 19, 35, 3.31, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 19, 35, 4.99, 7.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 36, 50, 3.84, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 36, 50, 4.88, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 51, 65, 4.13, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 51, 65, 3.86, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 66, 80, 3.31, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 66, 80, 3.96, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'MALE', 81, 120, 3.15, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 57, 'FEMALE', 81, 120, 3.39, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 0, 1, 3.96, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 0, 1, 3.06, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 2, 5, 4.71, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 2, 5, 4.7, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 6, 12, 3.76, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 6, 12, 3.41, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 13, 18, 4.85, 6.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 13, 18, 3.66, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 19, 35, 4.25, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 19, 35, 4.6, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 36, 50, 3.15, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 36, 50, 3.49, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 51, 65, 3.17, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 51, 65, 3.37, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 66, 80, 3.44, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 66, 80, 4.09, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'MALE', 81, 120, 3.51, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 58, 'FEMALE', 81, 120, 3.46, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 0, 1, 3.94, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 0, 1, 3.31, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 2, 5, 3.7, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 2, 5, 4.77, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 6, 12, 3.15, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 6, 12, 4.73, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 13, 18, 3.66, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 13, 18, 3.91, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 19, 35, 4.56, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 19, 35, 4.17, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 36, 50, 4.79, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 36, 50, 3.08, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 51, 65, 4.7, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 51, 65, 4.0, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 66, 80, 3.92, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 66, 80, 3.34, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'MALE', 81, 120, 3.39, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 59, 'FEMALE', 81, 120, 3.58, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 0, 1, 3.76, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 0, 1, 3.56, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 2, 5, 4.05, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 2, 5, 3.92, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 6, 12, 4.51, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 6, 12, 4.91, 6.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 13, 18, 4.27, 5.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 13, 18, 4.02, 5.989999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 19, 35, 3.07, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 19, 35, 3.97, 6.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 36, 50, 4.35, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 36, 50, 3.42, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 51, 65, 3.12, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 51, 65, 4.59, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 66, 80, 4.48, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 66, 80, 3.46, 4.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'MALE', 81, 120, 4.32, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 60, 'FEMALE', 81, 120, 3.49, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 0, 1, 3.85, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 0, 1, 3.22, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 2, 5, 4.73, 6.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 2, 5, 3.37, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 6, 12, 3.88, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 6, 12, 4.47, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 13, 18, 3.09, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 13, 18, 4.02, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 19, 35, 4.61, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 19, 35, 3.71, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 36, 50, 4.46, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 36, 50, 3.31, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 51, 65, 3.28, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 51, 65, 4.99, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 66, 80, 4.34, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 66, 80, 4.86, 6.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'MALE', 81, 120, 3.35, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 61, 'FEMALE', 81, 120, 4.05, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 0, 1, 3.69, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 0, 1, 3.66, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 2, 5, 4.53, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 2, 5, 4.36, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 6, 12, 3.61, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 6, 12, 3.74, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 13, 18, 3.17, 4.34, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 13, 18, 3.53, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 19, 35, 4.02, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 19, 35, 3.79, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 36, 50, 3.66, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 36, 50, 3.87, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 51, 65, 4.08, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 51, 65, 3.81, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 66, 80, 3.74, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 66, 80, 4.68, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'MALE', 81, 120, 4.99, 7.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 62, 'FEMALE', 81, 120, 3.39, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 0, 1, 4.16, 5.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 0, 1, 3.45, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 2, 5, 4.47, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 2, 5, 3.14, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 6, 12, 3.76, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 6, 12, 4.8, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 13, 18, 3.83, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 13, 18, 3.68, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 19, 35, 3.29, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 19, 35, 3.82, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 36, 50, 3.24, 5.03, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 36, 50, 3.55, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 51, 65, 4.38, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 51, 65, 4.7, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 66, 80, 3.43, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 66, 80, 3.45, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'MALE', 81, 120, 3.52, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 63, 'FEMALE', 81, 120, 3.77, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 0, 1, 5.0, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 0, 1, 4.98, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 2, 5, 4.8, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 2, 5, 3.34, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 6, 12, 3.88, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 6, 12, 4.75, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 13, 18, 3.48, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 13, 18, 3.44, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 19, 35, 3.61, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 19, 35, 4.72, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 36, 50, 3.26, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 36, 50, 3.01, 4.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 51, 65, 4.74, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 51, 65, 4.83, 7.13, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 66, 80, 4.16, 5.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 66, 80, 3.28, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'MALE', 81, 120, 4.06, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 64, 'FEMALE', 81, 120, 4.02, 5.489999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 0, 1, 3.13, 4.6, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 0, 1, 3.63, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 2, 5, 3.56, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 2, 5, 4.23, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 6, 12, 4.05, 5.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 6, 12, 4.51, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 13, 18, 3.33, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 13, 18, 3.38, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 19, 35, 3.29, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 19, 35, 4.83, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 36, 50, 3.6, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 36, 50, 3.71, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 51, 65, 4.48, 5.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 51, 65, 4.35, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 66, 80, 3.04, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 66, 80, 4.38, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'MALE', 81, 120, 4.54, 6.88, 'mg/dL', 'Giá trị bình thường'),
    ( 65, 'FEMALE', 81, 120, 3.77, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 0, 1, 3.39, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 0, 1, 4.55, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 2, 5, 4.44, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 2, 5, 3.29, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 6, 12, 3.54, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 6, 12, 3.5, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 13, 18, 4.11, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 13, 18, 4.71, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 19, 35, 4.8, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 19, 35, 4.44, 6.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 36, 50, 4.33, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 36, 50, 3.22, 4.26, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 51, 65, 4.36, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 51, 65, 3.26, 4.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 66, 80, 4.0, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 66, 80, 3.04, 4.16, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'MALE', 81, 120, 3.89, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 66, 'FEMALE', 81, 120, 3.61, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 0, 1, 4.74, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 0, 1, 4.74, 7.04, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 2, 5, 3.49, 4.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 2, 5, 4.12, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 6, 12, 3.41, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 6, 12, 3.41, 4.66, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 13, 18, 3.16, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 13, 18, 4.85, 6.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 19, 35, 3.89, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 19, 35, 3.77, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 36, 50, 3.35, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 36, 50, 4.62, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 51, 65, 3.59, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 51, 65, 3.18, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 66, 80, 4.45, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 66, 80, 3.77, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'MALE', 81, 120, 3.87, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 67, 'FEMALE', 81, 120, 4.69, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 0, 1, 3.52, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 0, 1, 3.68, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 2, 5, 3.5, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 2, 5, 4.77, 6.069999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 6, 12, 4.4, 6.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 6, 12, 3.7, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 13, 18, 3.82, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 13, 18, 4.23, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 19, 35, 3.33, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 19, 35, 4.28, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 36, 50, 4.09, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 36, 50, 4.05, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 51, 65, 3.41, 4.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 51, 65, 3.12, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 66, 80, 3.16, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 66, 80, 3.55, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'MALE', 81, 120, 3.92, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 68, 'FEMALE', 81, 120, 4.01, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 0, 1, 4.1, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 0, 1, 4.29, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 2, 5, 4.83, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 2, 5, 3.84, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 6, 12, 4.01, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 6, 12, 3.5, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 13, 18, 4.24, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 13, 18, 3.11, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 19, 35, 3.54, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 19, 35, 3.08, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 36, 50, 4.39, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 36, 50, 3.93, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 51, 65, 5.0, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 51, 65, 4.18, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 66, 80, 3.16, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 66, 80, 3.47, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'MALE', 81, 120, 4.85, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 69, 'FEMALE', 81, 120, 4.45, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 0, 1, 3.51, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 0, 1, 4.99, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 2, 5, 3.27, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 2, 5, 3.09, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 6, 12, 3.75, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 6, 12, 4.66, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 13, 18, 4.34, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 13, 18, 3.39, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 19, 35, 4.17, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 19, 35, 3.9, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 36, 50, 4.17, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 36, 50, 3.91, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 51, 65, 4.12, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 51, 65, 3.69, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 66, 80, 3.72, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 66, 80, 3.37, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'MALE', 81, 120, 4.81, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 70, 'FEMALE', 81, 120, 4.31, 5.6499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 0, 1, 4.22, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 0, 1, 3.96, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 2, 5, 3.79, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 2, 5, 3.25, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 6, 12, 3.72, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 6, 12, 3.44, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 13, 18, 4.79, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 13, 18, 4.89, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 19, 35, 3.95, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 19, 35, 4.02, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 36, 50, 3.98, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 36, 50, 4.49, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 51, 65, 3.1, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 51, 65, 4.97, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 66, 80, 4.46, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 66, 80, 4.42, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'MALE', 81, 120, 3.08, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 71, 'FEMALE', 81, 120, 3.6, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 0, 1, 3.03, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 0, 1, 3.67, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 2, 5, 4.48, 6.010000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 2, 5, 4.1, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 6, 12, 4.27, 5.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 6, 12, 3.58, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 13, 18, 3.05, 4.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 13, 18, 3.4, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 19, 35, 3.81, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 19, 35, 4.19, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 36, 50, 3.14, 4.26, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 36, 50, 3.15, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 51, 65, 3.74, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 51, 65, 4.16, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 66, 80, 4.11, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 66, 80, 4.89, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'MALE', 81, 120, 4.14, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 72, 'FEMALE', 81, 120, 4.15, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 0, 1, 3.44, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 0, 1, 3.14, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 2, 5, 3.64, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 2, 5, 4.64, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 6, 12, 3.46, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 6, 12, 3.09, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 13, 18, 4.02, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 13, 18, 4.6, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 19, 35, 4.84, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 19, 35, 4.4, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 36, 50, 3.53, 4.64, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 36, 50, 3.18, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 51, 65, 4.71, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 51, 65, 4.7, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 66, 80, 4.75, 7.15, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 66, 80, 4.06, 6.55, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'MALE', 81, 120, 3.21, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 73, 'FEMALE', 81, 120, 4.16, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 0, 1, 4.76, 6.96, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 0, 1, 4.48, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 2, 5, 3.44, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 2, 5, 4.59, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 6, 12, 3.79, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 6, 12, 3.75, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 13, 18, 4.66, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 13, 18, 3.8, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 19, 35, 4.78, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 19, 35, 3.88, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 36, 50, 4.41, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 36, 50, 4.09, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 51, 65, 4.52, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 51, 65, 3.65, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 66, 80, 4.23, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 66, 80, 3.83, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'MALE', 81, 120, 3.85, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 74, 'FEMALE', 81, 120, 3.21, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 0, 1, 4.34, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 0, 1, 3.94, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 2, 5, 4.84, 7.27, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 2, 5, 3.16, 5.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 6, 12, 4.62, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 6, 12, 4.48, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 13, 18, 4.88, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 13, 18, 4.94, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 19, 35, 3.71, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 19, 35, 3.53, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 36, 50, 4.62, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 36, 50, 4.56, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 51, 65, 3.36, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 51, 65, 4.25, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 66, 80, 3.98, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 66, 80, 3.57, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'MALE', 81, 120, 4.27, 6.459999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 75, 'FEMALE', 81, 120, 3.54, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 0, 1, 3.55, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 0, 1, 3.36, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 2, 5, 4.44, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 2, 5, 4.28, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 6, 12, 3.4, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 6, 12, 3.67, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 13, 18, 4.86, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 13, 18, 4.39, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 19, 35, 4.1, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 19, 35, 3.35, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 36, 50, 4.38, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 36, 50, 3.16, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 51, 65, 3.02, 4.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 51, 65, 3.44, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 66, 80, 3.71, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 66, 80, 3.45, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'MALE', 81, 120, 3.22, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 76, 'FEMALE', 81, 120, 4.66, 6.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 0, 1, 3.1, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 0, 1, 3.91, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 2, 5, 3.51, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 2, 5, 3.89, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 6, 12, 3.63, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 6, 12, 3.52, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 13, 18, 3.53, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 13, 18, 4.03, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 19, 35, 3.81, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 19, 35, 3.67, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 36, 50, 4.69, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 36, 50, 3.66, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 51, 65, 4.58, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 51, 65, 3.26, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 66, 80, 4.76, 6.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 66, 80, 3.76, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'MALE', 81, 120, 3.9, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 77, 'FEMALE', 81, 120, 3.15, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 0, 1, 4.94, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 0, 1, 4.76, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 2, 5, 4.78, 7.04, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 2, 5, 3.73, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 6, 12, 4.18, 6.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 6, 12, 4.84, 7.15, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 13, 18, 3.21, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 13, 18, 3.45, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 19, 35, 3.82, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 19, 35, 3.46, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 36, 50, 3.59, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 36, 50, 3.15, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 51, 65, 3.77, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 51, 65, 3.16, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 66, 80, 3.51, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 66, 80, 3.47, 4.6, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'MALE', 81, 120, 4.11, 6.290000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 78, 'FEMALE', 81, 120, 3.28, 4.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 0, 1, 3.59, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 0, 1, 4.41, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 2, 5, 3.36, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 2, 5, 4.45, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 6, 12, 4.11, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 6, 12, 3.71, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 13, 18, 4.04, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 13, 18, 4.76, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 19, 35, 4.0, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 19, 35, 4.07, 5.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 36, 50, 3.14, 4.27, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 36, 50, 3.03, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 51, 65, 3.11, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 51, 65, 3.4, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 66, 80, 3.86, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 66, 80, 3.94, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'MALE', 81, 120, 3.71, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 79, 'FEMALE', 81, 120, 4.45, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 0, 1, 3.69, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 0, 1, 3.15, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 2, 5, 4.88, 7.29, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 2, 5, 3.23, 4.6, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 6, 12, 4.77, 6.789999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 6, 12, 3.34, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 13, 18, 4.98, 7.08, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 13, 18, 4.74, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 19, 35, 4.6, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 19, 35, 4.74, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 36, 50, 3.06, 4.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 36, 50, 3.59, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 51, 65, 4.72, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 51, 65, 4.74, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 66, 80, 4.56, 6.709999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 66, 80, 3.63, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'MALE', 81, 120, 4.28, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 80, 'FEMALE', 81, 120, 4.86, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 0, 1, 4.46, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 0, 1, 4.28, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 2, 5, 4.78, 6.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 2, 5, 3.79, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 6, 12, 3.23, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 6, 12, 3.42, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 13, 18, 3.1, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 13, 18, 3.39, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 19, 35, 3.01, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 19, 35, 4.14, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 36, 50, 3.6, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 36, 50, 4.7, 7.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 51, 65, 4.21, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 51, 65, 4.49, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 66, 80, 4.87, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 66, 80, 4.73, 6.86, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'MALE', 81, 120, 4.46, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 81, 'FEMALE', 81, 120, 3.32, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 0, 1, 4.68, 6.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 0, 1, 4.14, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 2, 5, 4.01, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 2, 5, 3.05, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 6, 12, 3.19, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 6, 12, 4.99, 7.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 13, 18, 3.35, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 13, 18, 4.84, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 19, 35, 3.3, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 19, 35, 3.91, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 36, 50, 3.03, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 36, 50, 3.11, 4.66, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 51, 65, 3.14, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 51, 65, 3.54, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 66, 80, 3.52, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 66, 80, 4.08, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'MALE', 81, 120, 3.57, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 82, 'FEMALE', 81, 120, 3.38, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 0, 1, 4.58, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 0, 1, 3.09, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 2, 5, 3.31, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 2, 5, 4.41, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 6, 12, 3.52, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 6, 12, 4.72, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 13, 18, 4.59, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 13, 18, 4.46, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 19, 35, 3.32, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 19, 35, 4.74, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 36, 50, 3.04, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 36, 50, 3.58, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 51, 65, 4.74, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 51, 65, 3.99, 5.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 66, 80, 4.87, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 66, 80, 3.82, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'MALE', 81, 120, 3.6, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 83, 'FEMALE', 81, 120, 3.72, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 0, 1, 3.78, 4.89, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 0, 1, 3.22, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 2, 5, 4.77, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 2, 5, 4.25, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 6, 12, 3.06, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 6, 12, 4.14, 6.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 13, 18, 3.34, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 13, 18, 3.01, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 19, 35, 4.46, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 19, 35, 4.56, 7.01, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 36, 50, 3.03, 4.41, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 36, 50, 3.71, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 51, 65, 4.6, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 51, 65, 4.68, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 66, 80, 3.56, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 66, 80, 4.05, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'MALE', 81, 120, 4.32, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 84, 'FEMALE', 81, 120, 4.64, 6.88, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 0, 1, 3.88, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 0, 1, 4.43, 5.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 2, 5, 3.2, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 2, 5, 3.23, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 6, 12, 3.33, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 6, 12, 3.39, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 13, 18, 3.18, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 13, 18, 4.04, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 19, 35, 4.58, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 19, 35, 4.64, 6.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 36, 50, 4.3, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 36, 50, 3.27, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 51, 65, 4.89, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 51, 65, 3.35, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 66, 80, 3.27, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 66, 80, 3.4, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'MALE', 81, 120, 4.94, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 85, 'FEMALE', 81, 120, 4.19, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 0, 1, 3.05, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 0, 1, 3.48, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 2, 5, 3.94, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 2, 5, 3.12, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 6, 12, 3.1, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 6, 12, 3.6, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 13, 18, 4.63, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 13, 18, 4.77, 6.789999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 19, 35, 3.17, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 19, 35, 3.74, 5.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 36, 50, 4.94, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 36, 50, 3.78, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 51, 65, 4.3, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 51, 65, 3.2, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 66, 80, 3.48, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 66, 80, 4.82, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'MALE', 81, 120, 4.59, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 86, 'FEMALE', 81, 120, 3.51, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 0, 1, 3.43, 5.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 0, 1, 3.36, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 2, 5, 4.4, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 2, 5, 4.52, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 6, 12, 4.16, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 6, 12, 3.67, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 13, 18, 3.44, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 13, 18, 4.16, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 19, 35, 4.34, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 19, 35, 3.63, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 36, 50, 3.01, 4.06, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 36, 50, 3.18, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 51, 65, 3.13, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 51, 65, 3.81, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 66, 80, 4.51, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 66, 80, 4.23, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'MALE', 81, 120, 4.74, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 87, 'FEMALE', 81, 120, 4.44, 6.48, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 0, 1, 4.14, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 0, 1, 3.03, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 2, 5, 4.73, 6.19, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 2, 5, 4.25, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 6, 12, 4.5, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 6, 12, 3.04, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 13, 18, 4.82, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 13, 18, 3.36, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 19, 35, 3.53, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 19, 35, 4.96, 7.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 36, 50, 4.16, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 36, 50, 4.89, 7.209999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 51, 65, 4.15, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 51, 65, 3.8, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 66, 80, 3.87, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 66, 80, 4.45, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'MALE', 81, 120, 3.31, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 88, 'FEMALE', 81, 120, 3.11, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 0, 1, 4.33, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 0, 1, 3.59, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 2, 5, 3.07, 4.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 2, 5, 3.32, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 6, 12, 3.39, 4.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 6, 12, 3.77, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 13, 18, 3.12, 4.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 13, 18, 4.71, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 19, 35, 3.8, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 19, 35, 3.24, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 36, 50, 4.95, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 36, 50, 4.35, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 51, 65, 3.32, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 51, 65, 4.91, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 66, 80, 3.33, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 66, 80, 3.04, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'MALE', 81, 120, 4.49, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 89, 'FEMALE', 81, 120, 3.5, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 0, 1, 4.66, 7.15, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 0, 1, 3.24, 5.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 2, 5, 4.35, 6.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 2, 5, 4.6, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 6, 12, 3.31, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 6, 12, 4.33, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 13, 18, 4.43, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 13, 18, 3.08, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 19, 35, 4.85, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 19, 35, 3.12, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 36, 50, 4.75, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 36, 50, 3.3, 4.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 51, 65, 3.74, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 51, 65, 4.47, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 66, 80, 4.84, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 66, 80, 4.59, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'MALE', 81, 120, 4.37, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 90, 'FEMALE', 81, 120, 4.71, 6.3, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 0, 1, 4.32, 5.8500000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 0, 1, 4.27, 6.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 2, 5, 4.0, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 2, 5, 4.61, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 6, 12, 4.33, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 6, 12, 3.67, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 13, 18, 3.02, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 13, 18, 3.38, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 19, 35, 4.67, 6.98, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 19, 35, 3.3, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 36, 50, 3.87, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 36, 50, 4.76, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 51, 65, 4.43, 6.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 51, 65, 4.05, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 66, 80, 4.95, 6.48, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 66, 80, 4.96, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'MALE', 81, 120, 4.34, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 91, 'FEMALE', 81, 120, 4.88, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 0, 1, 4.96, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 0, 1, 4.27, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 2, 5, 4.87, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 2, 5, 3.86, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 6, 12, 4.97, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 6, 12, 3.3, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 13, 18, 3.19, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 13, 18, 4.38, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 19, 35, 3.71, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 19, 35, 4.41, 6.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 36, 50, 4.47, 6.06, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 36, 50, 4.05, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 51, 65, 3.07, 4.1, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 51, 65, 3.88, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 66, 80, 3.13, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 66, 80, 4.79, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'MALE', 81, 120, 3.52, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 92, 'FEMALE', 81, 120, 3.45, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 0, 1, 3.38, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 0, 1, 3.76, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 2, 5, 3.86, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 2, 5, 3.06, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 6, 12, 3.36, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 6, 12, 4.68, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 13, 18, 3.99, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 13, 18, 3.45, 4.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 19, 35, 4.96, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 19, 35, 4.79, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 36, 50, 3.66, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 36, 50, 3.18, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 51, 65, 3.02, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 51, 65, 4.01, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 66, 80, 3.85, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 66, 80, 4.24, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'MALE', 81, 120, 4.96, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 93, 'FEMALE', 81, 120, 3.89, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 0, 1, 3.8, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 0, 1, 3.19, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 2, 5, 3.19, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 2, 5, 3.92, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 6, 12, 4.53, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 6, 12, 3.43, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 13, 18, 3.55, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 13, 18, 4.52, 6.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 19, 35, 3.86, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 19, 35, 3.01, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 36, 50, 4.4, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 36, 50, 4.69, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 51, 65, 3.31, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 51, 65, 4.29, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 66, 80, 4.85, 6.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 66, 80, 3.73, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'MALE', 81, 120, 3.75, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 94, 'FEMALE', 81, 120, 3.94, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 0, 1, 3.39, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 0, 1, 3.14, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 2, 5, 4.06, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 2, 5, 4.32, 5.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 6, 12, 3.2, 4.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 6, 12, 4.73, 6.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 13, 18, 4.5, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 13, 18, 4.69, 6.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 19, 35, 4.05, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 19, 35, 4.1, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 36, 50, 4.49, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 36, 50, 4.67, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 51, 65, 3.53, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 51, 65, 4.07, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 66, 80, 3.36, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 66, 80, 4.54, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'MALE', 81, 120, 3.64, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 95, 'FEMALE', 81, 120, 4.09, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 0, 1, 4.06, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 0, 1, 4.61, 6.37, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 2, 5, 3.66, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 2, 5, 3.92, 4.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 6, 12, 4.32, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 6, 12, 3.86, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 13, 18, 4.06, 6.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 13, 18, 4.83, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 19, 35, 3.4, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 19, 35, 3.8, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 36, 50, 3.57, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 36, 50, 3.78, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 51, 65, 3.47, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 51, 65, 3.42, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 66, 80, 4.01, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 66, 80, 4.49, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'MALE', 81, 120, 3.33, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 96, 'FEMALE', 81, 120, 3.46, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 0, 1, 3.37, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 0, 1, 3.79, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 2, 5, 3.45, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 2, 5, 3.35, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 6, 12, 3.38, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 6, 12, 3.98, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 13, 18, 4.0, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 13, 18, 3.68, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 19, 35, 4.85, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 19, 35, 3.89, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 36, 50, 3.73, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 36, 50, 3.48, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 51, 65, 4.43, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 51, 65, 4.81, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 66, 80, 3.21, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 66, 80, 4.98, 6.290000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'MALE', 81, 120, 4.22, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 97, 'FEMALE', 81, 120, 4.18, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 0, 1, 3.58, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 0, 1, 4.06, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 2, 5, 3.99, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 2, 5, 3.94, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 6, 12, 4.88, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 6, 12, 4.62, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 13, 18, 3.15, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 13, 18, 4.51, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 19, 35, 3.64, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 19, 35, 4.7, 6.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 36, 50, 3.74, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 36, 50, 4.58, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 51, 65, 3.61, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 51, 65, 4.68, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 66, 80, 3.17, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 66, 80, 3.97, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'MALE', 81, 120, 3.21, 4.37, 'mg/dL', 'Giá trị bình thường'),
    ( 98, 'FEMALE', 81, 120, 4.61, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 0, 1, 4.78, 6.86, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 0, 1, 4.61, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 2, 5, 4.41, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 2, 5, 3.85, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 6, 12, 3.5, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 6, 12, 3.72, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 13, 18, 3.32, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 13, 18, 4.77, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 19, 35, 3.63, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 19, 35, 4.93, 7.31, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 36, 50, 3.34, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 36, 50, 4.39, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 51, 65, 4.24, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 51, 65, 3.56, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 66, 80, 3.84, 5.89, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 66, 80, 4.06, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'MALE', 81, 120, 3.87, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 99, 'FEMALE', 81, 120, 3.77, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 0, 1, 4.88, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 0, 1, 4.99, 7.44, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 2, 5, 3.75, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 2, 5, 4.09, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 6, 12, 4.68, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 6, 12, 4.34, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 13, 18, 3.08, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 13, 18, 3.03, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 19, 35, 3.95, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 19, 35, 4.5, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 36, 50, 4.91, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 36, 50, 4.82, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 51, 65, 3.17, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 51, 65, 4.33, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 66, 80, 4.62, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 66, 80, 4.56, 6.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'MALE', 81, 120, 4.28, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 100, 'FEMALE', 81, 120, 4.37, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 0, 1, 3.47, 5.960000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 0, 1, 4.41, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 2, 5, 3.73, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 2, 5, 4.99, 7.23, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 6, 12, 4.05, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 6, 12, 3.9, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 13, 18, 3.17, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 13, 18, 4.26, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 19, 35, 4.94, 7.040000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 19, 35, 3.81, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 36, 50, 4.74, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 36, 50, 3.65, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 51, 65, 4.92, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 51, 65, 3.03, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 66, 80, 3.1, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 66, 80, 4.81, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'MALE', 81, 120, 4.33, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 101, 'FEMALE', 81, 120, 3.38, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 0, 1, 3.55, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 0, 1, 4.98, 7.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 2, 5, 4.64, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 2, 5, 4.26, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 6, 12, 4.58, 6.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 6, 12, 4.24, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 13, 18, 4.32, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 13, 18, 3.7, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 19, 35, 4.13, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 19, 35, 4.36, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 36, 50, 3.28, 4.32, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 36, 50, 3.95, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 51, 65, 4.11, 5.430000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 51, 65, 3.1, 4.41, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 66, 80, 3.31, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 66, 80, 4.71, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'MALE', 81, 120, 3.13, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 102, 'FEMALE', 81, 120, 3.82, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 0, 1, 3.47, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 0, 1, 3.21, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 2, 5, 4.3, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 2, 5, 4.31, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 6, 12, 3.28, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 6, 12, 4.78, 6.99, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 13, 18, 3.43, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 13, 18, 4.69, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 19, 35, 4.37, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 19, 35, 3.06, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 36, 50, 4.44, 6.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 36, 50, 3.08, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 51, 65, 3.14, 4.16, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 51, 65, 3.88, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 66, 80, 3.94, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 66, 80, 3.39, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'MALE', 81, 120, 3.13, 4.3, 'mg/dL', 'Giá trị bình thường'),
    ( 103, 'FEMALE', 81, 120, 3.05, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 0, 1, 3.09, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 0, 1, 3.96, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 2, 5, 4.05, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 2, 5, 3.12, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 6, 12, 4.99, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 6, 12, 4.81, 6.97, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 13, 18, 3.72, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 13, 18, 3.48, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 19, 35, 4.9, 7.370000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 19, 35, 4.04, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 36, 50, 4.92, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 36, 50, 4.35, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 51, 65, 3.46, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 51, 65, 4.91, 6.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 66, 80, 4.75, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 66, 80, 4.39, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'MALE', 81, 120, 3.32, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 104, 'FEMALE', 81, 120, 4.3, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 0, 1, 4.34, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 0, 1, 4.07, 6.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 2, 5, 4.4, 6.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 2, 5, 4.97, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 6, 12, 4.28, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 6, 12, 4.15, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 13, 18, 3.63, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 13, 18, 3.38, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 19, 35, 3.96, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 19, 35, 3.59, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 36, 50, 3.94, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 36, 50, 4.02, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 51, 65, 4.1, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 51, 65, 4.77, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 66, 80, 4.34, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 66, 80, 4.91, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'MALE', 81, 120, 3.46, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 105, 'FEMALE', 81, 120, 4.07, 5.1000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 0, 1, 3.35, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 0, 1, 3.88, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 2, 5, 3.85, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 2, 5, 3.25, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 6, 12, 3.02, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 6, 12, 3.24, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 13, 18, 4.5, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 13, 18, 4.66, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 19, 35, 3.24, 5.460000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 19, 35, 3.9, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 36, 50, 4.08, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 36, 50, 4.52, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 51, 65, 4.96, 7.41, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 51, 65, 3.01, 4.32, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 66, 80, 4.54, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 66, 80, 3.46, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'MALE', 81, 120, 3.66, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 106, 'FEMALE', 81, 120, 4.38, 6.55, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 0, 1, 4.86, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 0, 1, 3.21, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 2, 5, 4.58, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 2, 5, 4.86, 6.95, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 6, 12, 3.72, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 6, 12, 3.97, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 13, 18, 4.77, 6.3, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 13, 18, 3.33, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 19, 35, 3.85, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 19, 35, 4.09, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 36, 50, 3.85, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 36, 50, 4.52, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 51, 65, 4.44, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 51, 65, 3.26, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 66, 80, 3.54, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 66, 80, 4.73, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'MALE', 81, 120, 4.29, 6.37, 'mg/dL', 'Giá trị bình thường'),
    ( 107, 'FEMALE', 81, 120, 4.22, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 0, 1, 3.76, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 0, 1, 4.82, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 2, 5, 3.19, 4.39, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 2, 5, 3.08, 4.28, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 6, 12, 3.51, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 6, 12, 4.64, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 13, 18, 3.53, 4.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 13, 18, 3.54, 4.55, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 19, 35, 3.36, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 19, 35, 3.07, 4.26, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 36, 50, 4.76, 7.22, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 36, 50, 4.62, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 51, 65, 4.56, 6.3, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 51, 65, 4.8, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 66, 80, 4.99, 6.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 66, 80, 3.19, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'MALE', 81, 120, 3.12, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 108, 'FEMALE', 81, 120, 3.15, 4.22, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 0, 1, 4.57, 5.8500000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 0, 1, 4.07, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 2, 5, 3.18, 4.37, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 2, 5, 3.75, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 6, 12, 4.22, 6.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 6, 12, 4.96, 6.8, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 13, 18, 4.23, 6.180000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 13, 18, 3.59, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 19, 35, 4.74, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 19, 35, 3.81, 5.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 36, 50, 4.27, 6.379999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 36, 50, 3.56, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 51, 65, 3.56, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 51, 65, 3.24, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 66, 80, 4.25, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 66, 80, 3.92, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'MALE', 81, 120, 4.9, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 109, 'FEMALE', 81, 120, 4.38, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 0, 1, 4.58, 6.3, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 0, 1, 3.31, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 2, 5, 3.04, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 2, 5, 3.26, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 6, 12, 3.67, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 6, 12, 3.82, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 13, 18, 4.18, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 13, 18, 4.09, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 19, 35, 3.79, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 19, 35, 4.28, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 36, 50, 3.47, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 36, 50, 3.66, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 51, 65, 3.22, 4.78, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 51, 65, 4.3, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 66, 80, 4.62, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 66, 80, 3.19, 4.64, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'MALE', 81, 120, 3.02, 4.24, 'mg/dL', 'Giá trị bình thường'),
    ( 110, 'FEMALE', 81, 120, 4.71, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 0, 1, 3.76, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 0, 1, 3.21, 4.32, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 2, 5, 3.87, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 2, 5, 3.57, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 6, 12, 3.37, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 6, 12, 4.56, 5.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 13, 18, 3.94, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 13, 18, 4.39, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 19, 35, 3.52, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 19, 35, 3.45, 5.69, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 36, 50, 4.02, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 36, 50, 4.97, 7.34, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 51, 65, 4.49, 6.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 51, 65, 3.05, 4.39, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 66, 80, 4.58, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 66, 80, 3.01, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'MALE', 81, 120, 3.92, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 111, 'FEMALE', 81, 120, 4.3, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 0, 1, 3.45, 4.54, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 0, 1, 3.32, 4.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 2, 5, 4.87, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 2, 5, 4.99, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 6, 12, 3.08, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 6, 12, 3.07, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 13, 18, 3.49, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 13, 18, 4.76, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 19, 35, 4.19, 5.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 19, 35, 3.18, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 36, 50, 3.95, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 36, 50, 3.43, 4.87, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 51, 65, 3.95, 6.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 51, 65, 4.87, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 66, 80, 3.18, 4.29, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 66, 80, 3.4, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'MALE', 81, 120, 4.01, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 112, 'FEMALE', 81, 120, 3.6, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 0, 1, 4.3, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 0, 1, 3.07, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 2, 5, 4.35, 5.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 2, 5, 3.68, 4.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 6, 12, 3.19, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 6, 12, 4.82, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 13, 18, 3.68, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 13, 18, 4.95, 7.24, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 19, 35, 4.2, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 19, 35, 4.86, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 36, 50, 3.91, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 36, 50, 4.48, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 51, 65, 4.99, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 51, 65, 3.37, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 66, 80, 4.23, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 66, 80, 4.07, 5.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'MALE', 81, 120, 4.89, 7.38, 'mg/dL', 'Giá trị bình thường'),
    ( 113, 'FEMALE', 81, 120, 3.36, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 0, 1, 4.26, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 0, 1, 3.34, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 2, 5, 3.47, 4.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 2, 5, 4.17, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 6, 12, 3.63, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 6, 12, 4.23, 6.1000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 13, 18, 3.72, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 13, 18, 4.71, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 19, 35, 4.92, 6.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 19, 35, 4.19, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 36, 50, 4.36, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 36, 50, 3.93, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 51, 65, 3.75, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 51, 65, 4.17, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 66, 80, 3.69, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 66, 80, 3.94, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'MALE', 81, 120, 4.04, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 114, 'FEMALE', 81, 120, 4.85, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 0, 1, 4.37, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 0, 1, 3.4, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 2, 5, 3.55, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 2, 5, 4.56, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 6, 12, 3.8, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 6, 12, 4.3, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 13, 18, 4.85, 7.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 13, 18, 3.89, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 19, 35, 4.16, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 19, 35, 3.87, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 36, 50, 4.3, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 36, 50, 4.27, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 51, 65, 3.95, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 51, 65, 3.18, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 66, 80, 4.0, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 66, 80, 4.38, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'MALE', 81, 120, 3.32, 4.47, 'mg/dL', 'Giá trị bình thường'),
    ( 115, 'FEMALE', 81, 120, 4.22, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 0, 1, 3.83, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 0, 1, 4.27, 6.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 2, 5, 3.5, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 2, 5, 3.98, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 6, 12, 3.05, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 6, 12, 3.5, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 13, 18, 3.23, 4.55, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 13, 18, 3.34, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 19, 35, 3.26, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 19, 35, 4.24, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 36, 50, 4.09, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 36, 50, 4.1, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 51, 65, 4.89, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 51, 65, 4.02, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 66, 80, 3.86, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 66, 80, 3.03, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'MALE', 81, 120, 4.49, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 116, 'FEMALE', 81, 120, 3.37, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 0, 1, 3.52, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 0, 1, 4.5, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 2, 5, 3.16, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 2, 5, 4.34, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 6, 12, 3.59, 5.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 6, 12, 3.59, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 13, 18, 4.47, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 13, 18, 4.67, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 19, 35, 4.02, 5.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 19, 35, 3.53, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 36, 50, 4.59, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 36, 50, 4.73, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 51, 65, 3.41, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 51, 65, 4.28, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 66, 80, 4.48, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 66, 80, 4.48, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'MALE', 81, 120, 3.42, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 117, 'FEMALE', 81, 120, 3.63, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 0, 1, 3.22, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 0, 1, 3.8, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 2, 5, 3.51, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 2, 5, 4.56, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 6, 12, 4.88, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 6, 12, 3.23, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 13, 18, 4.38, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 13, 18, 4.87, 7.03, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 19, 35, 3.86, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 19, 35, 3.13, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 36, 50, 4.35, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 36, 50, 4.0, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 51, 65, 3.44, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 51, 65, 3.91, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 66, 80, 3.62, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 66, 80, 4.79, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'MALE', 81, 120, 3.08, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 118, 'FEMALE', 81, 120, 3.42, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 0, 1, 3.11, 4.87, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 0, 1, 3.68, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 2, 5, 4.78, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 2, 5, 4.08, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 6, 12, 4.3, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 6, 12, 3.24, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 13, 18, 3.87, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 13, 18, 4.42, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 19, 35, 3.39, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 19, 35, 4.71, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 36, 50, 4.78, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 36, 50, 3.34, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 51, 65, 3.09, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 51, 65, 4.0, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 66, 80, 4.79, 7.23, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 66, 80, 4.1, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'MALE', 81, 120, 4.85, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 119, 'FEMALE', 81, 120, 4.6, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 0, 1, 3.21, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 0, 1, 3.08, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 2, 5, 3.91, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 2, 5, 4.37, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 6, 12, 4.38, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 6, 12, 4.15, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 13, 18, 3.16, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 13, 18, 3.68, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 19, 35, 3.33, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 19, 35, 3.71, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 36, 50, 3.25, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 36, 50, 3.02, 4.21, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 51, 65, 3.27, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 51, 65, 3.6, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 66, 80, 3.89, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 66, 80, 4.71, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'MALE', 81, 120, 3.35, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 120, 'FEMALE', 81, 120, 3.56, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 0, 1, 4.18, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 0, 1, 4.6, 6.8, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 2, 5, 4.97, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 2, 5, 3.26, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 6, 12, 3.11, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 6, 12, 3.75, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 13, 18, 3.79, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 13, 18, 4.85, 7.32, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 19, 35, 3.6, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 19, 35, 4.33, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 36, 50, 3.55, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 36, 50, 4.93, 6.319999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 51, 65, 4.42, 6.73, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 51, 65, 3.56, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 66, 80, 3.81, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 66, 80, 4.46, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'MALE', 81, 120, 4.41, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 121, 'FEMALE', 81, 120, 3.06, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 0, 1, 3.38, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 0, 1, 4.14, 6.069999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 2, 5, 3.96, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 2, 5, 4.94, 6.84, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 6, 12, 3.77, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 6, 12, 3.74, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 13, 18, 4.06, 5.959999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 13, 18, 4.13, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 19, 35, 3.72, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 19, 35, 3.75, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 36, 50, 4.91, 7.12, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 36, 50, 4.91, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 51, 65, 4.6, 6.709999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 51, 65, 4.93, 7.34, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 66, 80, 3.49, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 66, 80, 4.73, 7.17, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'MALE', 81, 120, 3.17, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 122, 'FEMALE', 81, 120, 3.03, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 0, 1, 4.03, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 0, 1, 3.71, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 2, 5, 3.43, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 2, 5, 3.07, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 6, 12, 3.96, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 6, 12, 3.27, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 13, 18, 3.73, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 13, 18, 3.29, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 19, 35, 4.49, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 19, 35, 3.32, 4.55, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 36, 50, 3.63, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 36, 50, 3.69, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 51, 65, 3.58, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 51, 65, 4.99, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 66, 80, 3.28, 4.37, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 66, 80, 4.92, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'MALE', 81, 120, 3.78, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 123, 'FEMALE', 81, 120, 4.14, 6.06, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 0, 1, 3.54, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 0, 1, 4.78, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 2, 5, 3.64, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 2, 5, 3.27, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 6, 12, 4.2, 6.19, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 6, 12, 4.8, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 13, 18, 4.55, 6.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 13, 18, 4.78, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 19, 35, 3.46, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 19, 35, 4.0, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 36, 50, 4.97, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 36, 50, 3.53, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 51, 65, 4.32, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 51, 65, 3.35, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 66, 80, 4.14, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 66, 80, 3.28, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'MALE', 81, 120, 4.05, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 124, 'FEMALE', 81, 120, 4.05, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 0, 1, 3.7, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 0, 1, 3.58, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 2, 5, 3.17, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 2, 5, 3.54, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 6, 12, 3.64, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 6, 12, 3.06, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 13, 18, 3.21, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 13, 18, 3.03, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 19, 35, 3.77, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 19, 35, 3.32, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 36, 50, 4.85, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 36, 50, 4.42, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 51, 65, 4.5, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 51, 65, 3.96, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 66, 80, 4.39, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 66, 80, 4.5, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'MALE', 81, 120, 3.02, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 125, 'FEMALE', 81, 120, 3.44, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 0, 1, 4.26, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 0, 1, 5.0, 7.37, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 2, 5, 3.12, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 2, 5, 4.56, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 6, 12, 4.06, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 6, 12, 4.57, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 13, 18, 3.25, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 13, 18, 3.02, 4.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 19, 35, 3.71, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 19, 35, 4.16, 6.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 36, 50, 3.92, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 36, 50, 3.85, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 51, 65, 3.65, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 51, 65, 3.24, 4.63, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 66, 80, 3.32, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 66, 80, 3.74, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'MALE', 81, 120, 3.02, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 126, 'FEMALE', 81, 120, 3.01, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 0, 1, 4.78, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 0, 1, 3.19, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 2, 5, 3.58, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 2, 5, 3.06, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 6, 12, 3.71, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 6, 12, 3.02, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 13, 18, 4.81, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 13, 18, 4.27, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 19, 35, 4.44, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 19, 35, 3.14, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 36, 50, 4.73, 6.48, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 36, 50, 3.53, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 51, 65, 4.63, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 51, 65, 3.76, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 66, 80, 3.62, 4.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 66, 80, 3.03, 5.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'MALE', 81, 120, 4.53, 5.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 127, 'FEMALE', 81, 120, 4.13, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 0, 1, 3.62, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 0, 1, 4.5, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 2, 5, 3.08, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 2, 5, 3.81, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 6, 12, 3.68, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 6, 12, 3.64, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 13, 18, 4.24, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 13, 18, 3.58, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 19, 35, 4.64, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 19, 35, 3.82, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 36, 50, 4.97, 7.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 36, 50, 3.28, 5.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 51, 65, 3.34, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 51, 65, 3.9, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 66, 80, 3.26, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 66, 80, 4.07, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'MALE', 81, 120, 4.9, 7.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 128, 'FEMALE', 81, 120, 3.73, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 0, 1, 3.87, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 0, 1, 4.68, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 2, 5, 4.18, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 2, 5, 4.39, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 6, 12, 3.93, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 6, 12, 4.35, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 13, 18, 4.07, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 13, 18, 4.55, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 19, 35, 4.17, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 19, 35, 4.15, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 36, 50, 3.47, 4.58, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 36, 50, 3.36, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 51, 65, 4.52, 6.409999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 51, 65, 4.63, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 66, 80, 3.59, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 66, 80, 4.32, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'MALE', 81, 120, 3.53, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 129, 'FEMALE', 81, 120, 4.37, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 0, 1, 3.91, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 0, 1, 4.29, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 2, 5, 4.39, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 2, 5, 3.65, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 6, 12, 4.21, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 6, 12, 3.53, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 13, 18, 4.22, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 13, 18, 3.23, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 19, 35, 3.22, 4.34, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 19, 35, 3.62, 4.87, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 36, 50, 4.71, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 36, 50, 4.33, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 51, 65, 3.31, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 51, 65, 3.75, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 66, 80, 4.83, 6.88, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 66, 80, 4.79, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'MALE', 81, 120, 3.66, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 130, 'FEMALE', 81, 120, 4.38, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 0, 1, 3.76, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 0, 1, 4.14, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 2, 5, 4.94, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 2, 5, 4.5, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 6, 12, 4.23, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 6, 12, 3.8, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 13, 18, 3.95, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 13, 18, 4.93, 7.06, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 19, 35, 4.44, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 19, 35, 4.01, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 36, 50, 4.44, 5.960000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 36, 50, 3.64, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 51, 65, 4.31, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 51, 65, 3.19, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 66, 80, 4.49, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 66, 80, 4.99, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'MALE', 81, 120, 4.38, 6.84, 'mg/dL', 'Giá trị bình thường'),
    ( 131, 'FEMALE', 81, 120, 3.29, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 0, 1, 3.85, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 0, 1, 3.93, 5.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 2, 5, 4.58, 7.0, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 2, 5, 3.86, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 6, 12, 4.65, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 6, 12, 4.05, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 13, 18, 4.99, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 13, 18, 3.67, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 19, 35, 4.74, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 19, 35, 4.18, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 36, 50, 4.88, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 36, 50, 4.33, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 51, 65, 4.06, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 51, 65, 4.65, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 66, 80, 4.13, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 66, 80, 3.93, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'MALE', 81, 120, 3.67, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 132, 'FEMALE', 81, 120, 4.36, 6.61, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 0, 1, 4.91, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 0, 1, 3.02, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 2, 5, 3.39, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 2, 5, 4.12, 6.61, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 6, 12, 4.24, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 6, 12, 3.97, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 13, 18, 4.2, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 13, 18, 3.41, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 19, 35, 4.96, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 19, 35, 4.61, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 36, 50, 3.71, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 36, 50, 4.19, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 51, 65, 3.65, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 51, 65, 3.57, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 66, 80, 3.39, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 66, 80, 4.38, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'MALE', 81, 120, 3.13, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 133, 'FEMALE', 81, 120, 4.15, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 0, 1, 3.94, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 0, 1, 3.49, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 2, 5, 4.5, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 2, 5, 4.05, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 6, 12, 4.45, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 6, 12, 4.64, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 13, 18, 4.18, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 13, 18, 3.76, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 19, 35, 3.1, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 19, 35, 4.32, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 36, 50, 3.9, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 36, 50, 3.74, 5.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 51, 65, 4.16, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 51, 65, 4.28, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 66, 80, 3.99, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 66, 80, 4.34, 6.72, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'MALE', 81, 120, 4.57, 6.84, 'mg/dL', 'Giá trị bình thường'),
    ( 134, 'FEMALE', 81, 120, 4.02, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 0, 1, 3.87, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 0, 1, 3.98, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 2, 5, 4.13, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 2, 5, 4.67, 6.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 6, 12, 3.74, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 6, 12, 3.63, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 13, 18, 4.66, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 13, 18, 3.81, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 19, 35, 4.08, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 19, 35, 3.63, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 36, 50, 4.18, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 36, 50, 4.16, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 51, 65, 3.16, 4.24, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 51, 65, 4.61, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 66, 80, 3.37, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 66, 80, 3.46, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'MALE', 81, 120, 4.16, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 135, 'FEMALE', 81, 120, 3.71, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 0, 1, 3.6, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 0, 1, 4.67, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 2, 5, 4.22, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 2, 5, 3.39, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 6, 12, 3.67, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 6, 12, 3.79, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 13, 18, 3.43, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 13, 18, 3.63, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 19, 35, 4.25, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 19, 35, 3.51, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 36, 50, 3.51, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 36, 50, 3.43, 4.44, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 51, 65, 4.66, 6.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 51, 65, 3.62, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 66, 80, 4.11, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 66, 80, 4.43, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'MALE', 81, 120, 4.09, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 136, 'FEMALE', 81, 120, 3.04, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 0, 1, 3.87, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 0, 1, 4.91, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 2, 5, 4.84, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 2, 5, 3.47, 4.54, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 6, 12, 3.79, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 6, 12, 4.99, 7.07, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 13, 18, 4.11, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 13, 18, 3.17, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 19, 35, 4.67, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 19, 35, 4.79, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 36, 50, 3.73, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 36, 50, 4.2, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 51, 65, 3.74, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 51, 65, 4.98, 7.42, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 66, 80, 3.34, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 66, 80, 4.54, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'MALE', 81, 120, 3.19, 4.48, 'mg/dL', 'Giá trị bình thường'),
    ( 137, 'FEMALE', 81, 120, 3.92, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 0, 1, 4.54, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 0, 1, 4.37, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 2, 5, 3.64, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 2, 5, 4.56, 6.569999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 6, 12, 3.24, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 6, 12, 4.54, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 13, 18, 3.47, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 13, 18, 3.6, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 19, 35, 4.05, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 19, 35, 4.79, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 36, 50, 3.28, 4.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 36, 50, 4.93, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 51, 65, 3.6, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 51, 65, 4.73, 6.950000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 66, 80, 3.46, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 66, 80, 4.4, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'MALE', 81, 120, 4.67, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 138, 'FEMALE', 81, 120, 4.58, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 0, 1, 3.87, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 0, 1, 4.6, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 2, 5, 4.55, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 2, 5, 3.31, 4.48, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 6, 12, 4.6, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 6, 12, 3.26, 5.369999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 13, 18, 3.88, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 13, 18, 4.97, 7.32, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 19, 35, 3.88, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 19, 35, 4.7, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 36, 50, 4.97, 6.6499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 36, 50, 3.01, 4.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 51, 65, 3.07, 4.66, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 51, 65, 4.05, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 66, 80, 3.59, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 66, 80, 4.71, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'MALE', 81, 120, 3.22, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 139, 'FEMALE', 81, 120, 3.67, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 0, 1, 3.36, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 0, 1, 3.69, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 2, 5, 4.44, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 2, 5, 3.02, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 6, 12, 4.8, 7.07, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 6, 12, 4.51, 6.31, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 13, 18, 3.71, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 13, 18, 3.5, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 19, 35, 4.41, 6.77, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 19, 35, 3.98, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 36, 50, 3.35, 5.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 36, 50, 3.62, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 51, 65, 4.73, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 51, 65, 4.84, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 66, 80, 3.92, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 66, 80, 4.84, 7.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'MALE', 81, 120, 4.14, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 140, 'FEMALE', 81, 120, 4.09, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 0, 1, 4.31, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 0, 1, 4.4, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 2, 5, 4.88, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 2, 5, 3.32, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 6, 12, 4.98, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 6, 12, 4.73, 6.7, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 13, 18, 4.19, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 13, 18, 4.26, 5.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 19, 35, 4.85, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 19, 35, 4.04, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 36, 50, 3.32, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 36, 50, 4.8, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 51, 65, 4.36, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 51, 65, 4.65, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 66, 80, 4.66, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 66, 80, 4.4, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'MALE', 81, 120, 3.16, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 141, 'FEMALE', 81, 120, 3.02, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 0, 1, 4.21, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 0, 1, 4.92, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 2, 5, 3.31, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 2, 5, 4.97, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 6, 12, 4.71, 6.8, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 6, 12, 4.18, 6.069999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 13, 18, 4.42, 6.55, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 13, 18, 4.61, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 19, 35, 3.57, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 19, 35, 3.09, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 36, 50, 3.25, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 36, 50, 3.97, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 51, 65, 4.45, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 51, 65, 3.05, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 66, 80, 4.46, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 66, 80, 4.71, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'MALE', 81, 120, 3.53, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 142, 'FEMALE', 81, 120, 4.14, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 0, 1, 3.58, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 0, 1, 3.11, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 2, 5, 3.84, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 2, 5, 4.68, 6.709999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 6, 12, 4.62, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 6, 12, 4.55, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 13, 18, 3.33, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 13, 18, 4.28, 6.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 19, 35, 4.97, 7.31, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 19, 35, 4.61, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 36, 50, 3.64, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 36, 50, 4.04, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 51, 65, 4.75, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 51, 65, 4.99, 7.23, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 66, 80, 4.44, 5.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 66, 80, 4.67, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'MALE', 81, 120, 4.49, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 143, 'FEMALE', 81, 120, 3.03, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 0, 1, 4.13, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 0, 1, 4.21, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 2, 5, 3.47, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 2, 5, 4.37, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 6, 12, 3.06, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 6, 12, 3.25, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 13, 18, 3.07, 4.31, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 13, 18, 3.65, 5.92, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 19, 35, 3.72, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 19, 35, 3.11, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 36, 50, 4.1, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 36, 50, 4.76, 7.17, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 51, 65, 4.17, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 51, 65, 4.37, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 66, 80, 4.89, 6.64, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 66, 80, 3.66, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'MALE', 81, 120, 3.48, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 144, 'FEMALE', 81, 120, 4.38, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 0, 1, 4.51, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 0, 1, 4.53, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 2, 5, 4.55, 6.64, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 2, 5, 4.09, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 6, 12, 3.33, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 6, 12, 4.77, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 13, 18, 3.31, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 13, 18, 4.48, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 19, 35, 3.08, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 19, 35, 4.68, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 36, 50, 4.38, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 36, 50, 4.84, 7.3, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 51, 65, 4.26, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 51, 65, 3.0, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 66, 80, 3.26, 4.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 66, 80, 3.34, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'MALE', 81, 120, 3.24, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 145, 'FEMALE', 81, 120, 4.96, 6.78, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 0, 1, 4.49, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 0, 1, 4.03, 6.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 2, 5, 4.03, 5.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 2, 5, 4.74, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 6, 12, 3.93, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 6, 12, 3.61, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 13, 18, 3.37, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 13, 18, 4.21, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 19, 35, 3.55, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 19, 35, 3.14, 4.34, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 36, 50, 4.37, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 36, 50, 4.86, 6.84, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 51, 65, 3.64, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 51, 65, 3.66, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 66, 80, 3.86, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 66, 80, 3.21, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'MALE', 81, 120, 3.47, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 146, 'FEMALE', 81, 120, 3.01, 4.56, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 0, 1, 4.16, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 0, 1, 3.42, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 2, 5, 3.3, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 2, 5, 3.82, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 6, 12, 4.91, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 6, 12, 3.87, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 13, 18, 4.31, 5.8999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 13, 18, 4.25, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 19, 35, 3.93, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 19, 35, 3.06, 4.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 36, 50, 4.62, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 36, 50, 3.11, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 51, 65, 4.51, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 51, 65, 3.4, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 66, 80, 4.84, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 66, 80, 3.28, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'MALE', 81, 120, 4.89, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 147, 'FEMALE', 81, 120, 3.4, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 0, 1, 4.59, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 0, 1, 4.48, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 2, 5, 3.93, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 2, 5, 4.39, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 6, 12, 3.02, 4.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 6, 12, 4.65, 6.48, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 13, 18, 3.5, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 13, 18, 4.39, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 19, 35, 4.88, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 19, 35, 4.69, 6.86, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 36, 50, 4.26, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 36, 50, 4.29, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 51, 65, 4.72, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 51, 65, 3.52, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 66, 80, 4.69, 6.430000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 66, 80, 3.67, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'MALE', 81, 120, 3.87, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 148, 'FEMALE', 81, 120, 4.72, 7.13, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 0, 1, 3.99, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 0, 1, 3.77, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 2, 5, 3.71, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 2, 5, 4.18, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 6, 12, 4.82, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 6, 12, 4.39, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 13, 18, 3.62, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 13, 18, 3.38, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 19, 35, 4.65, 5.8500000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 19, 35, 4.8, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 36, 50, 4.14, 6.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 36, 50, 3.21, 4.22, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 51, 65, 4.74, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 51, 65, 3.04, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 66, 80, 4.23, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 66, 80, 3.72, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'MALE', 81, 120, 4.3, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 149, 'FEMALE', 81, 120, 3.69, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 0, 1, 3.51, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 0, 1, 3.46, 4.76, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 2, 5, 3.69, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 2, 5, 3.39, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 6, 12, 4.84, 7.22, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 6, 12, 3.78, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 13, 18, 3.89, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 13, 18, 4.49, 6.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 19, 35, 3.02, 4.29, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 19, 35, 3.6, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 36, 50, 3.33, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 36, 50, 3.26, 4.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 51, 65, 4.34, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 51, 65, 3.97, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 66, 80, 3.66, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 66, 80, 4.34, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'MALE', 81, 120, 3.9, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 150, 'FEMALE', 81, 120, 3.19, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 0, 1, 4.12, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 0, 1, 3.06, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 2, 5, 4.59, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 2, 5, 4.35, 5.3999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 6, 12, 4.04, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 6, 12, 3.96, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 13, 18, 3.27, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 13, 18, 3.12, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 19, 35, 3.14, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 19, 35, 4.18, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 36, 50, 4.53, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 36, 50, 3.76, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 51, 65, 4.47, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 51, 65, 4.84, 6.39, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 66, 80, 4.9, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 66, 80, 3.79, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'MALE', 81, 120, 3.81, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 151, 'FEMALE', 81, 120, 4.17, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 0, 1, 3.47, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 0, 1, 4.67, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 2, 5, 3.8, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 2, 5, 4.55, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 6, 12, 4.54, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 6, 12, 4.09, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 13, 18, 3.58, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 13, 18, 3.72, 5.44, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 19, 35, 4.21, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 19, 35, 3.53, 4.83, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 36, 50, 4.72, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 36, 50, 4.27, 5.569999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 51, 65, 3.9, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 51, 65, 3.71, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 66, 80, 3.62, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 66, 80, 4.9, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'MALE', 81, 120, 4.3, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 152, 'FEMALE', 81, 120, 4.63, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 0, 1, 3.61, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 0, 1, 4.09, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 2, 5, 3.23, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 2, 5, 3.08, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 6, 12, 4.86, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 6, 12, 3.26, 4.29, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 13, 18, 4.05, 6.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 13, 18, 3.68, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 19, 35, 3.08, 4.24, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 19, 35, 4.88, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 36, 50, 3.42, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 36, 50, 3.82, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 51, 65, 3.21, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 51, 65, 4.98, 7.040000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 66, 80, 3.39, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 66, 80, 3.08, 4.59, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'MALE', 81, 120, 3.39, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 153, 'FEMALE', 81, 120, 3.69, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 0, 1, 4.81, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 0, 1, 4.02, 5.209999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 2, 5, 4.45, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 2, 5, 4.43, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 6, 12, 4.45, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 6, 12, 4.18, 6.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 13, 18, 3.23, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 13, 18, 4.06, 5.989999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 19, 35, 3.12, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 19, 35, 4.12, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 36, 50, 4.49, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 36, 50, 3.76, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 51, 65, 3.01, 4.199999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 51, 65, 3.33, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 66, 80, 4.1, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 66, 80, 3.82, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'MALE', 81, 120, 4.03, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 154, 'FEMALE', 81, 120, 3.2, 5.11, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 0, 1, 4.64, 6.47, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 0, 1, 3.37, 5.19, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 2, 5, 3.22, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 2, 5, 4.53, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 6, 12, 4.87, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 6, 12, 3.49, 5.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 13, 18, 3.1, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 13, 18, 4.36, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 19, 35, 3.52, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 19, 35, 3.94, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 36, 50, 4.03, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 36, 50, 4.29, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 51, 65, 4.33, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 51, 65, 3.16, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 66, 80, 3.66, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 66, 80, 4.39, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'MALE', 81, 120, 4.86, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 155, 'FEMALE', 81, 120, 4.32, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 0, 1, 4.19, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 0, 1, 4.84, 6.65, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 2, 5, 4.74, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 2, 5, 4.28, 5.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 6, 12, 3.47, 4.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 6, 12, 4.85, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 13, 18, 4.14, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 13, 18, 4.06, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 19, 35, 4.25, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 19, 35, 4.48, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 36, 50, 4.86, 6.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 36, 50, 4.78, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 51, 65, 3.89, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 51, 65, 3.35, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 66, 80, 4.32, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 66, 80, 4.06, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'MALE', 81, 120, 3.44, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 156, 'FEMALE', 81, 120, 3.27, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 0, 1, 3.15, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 0, 1, 4.38, 6.47, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 2, 5, 3.06, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 2, 5, 3.2, 4.92, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 6, 12, 3.48, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 6, 12, 4.71, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 13, 18, 4.79, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 13, 18, 4.8, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 19, 35, 3.85, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 19, 35, 3.87, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 36, 50, 4.72, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 36, 50, 3.08, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 51, 65, 3.03, 4.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 51, 65, 4.62, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 66, 80, 3.42, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 66, 80, 3.2, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'MALE', 81, 120, 4.75, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 157, 'FEMALE', 81, 120, 4.33, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 0, 1, 3.62, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 0, 1, 4.68, 6.709999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 2, 5, 4.22, 5.8999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 2, 5, 4.87, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 6, 12, 3.49, 5.960000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 6, 12, 3.68, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 13, 18, 4.47, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 13, 18, 3.88, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 19, 35, 4.31, 5.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 19, 35, 3.76, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 36, 50, 3.55, 5.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 36, 50, 3.22, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 51, 65, 3.96, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 51, 65, 4.04, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 66, 80, 3.21, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 66, 80, 3.94, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'MALE', 81, 120, 3.75, 4.86, 'mg/dL', 'Giá trị bình thường'),
    ( 158, 'FEMALE', 81, 120, 3.63, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 0, 1, 3.83, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 0, 1, 3.76, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 2, 5, 3.15, 4.34, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 2, 5, 4.52, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 6, 12, 3.45, 5.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 6, 12, 3.46, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 13, 18, 3.75, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 13, 18, 4.08, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 19, 35, 3.23, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 19, 35, 3.73, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 36, 50, 3.74, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 36, 50, 4.14, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 51, 65, 5.0, 6.95, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 51, 65, 4.68, 5.8999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 66, 80, 3.16, 4.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 66, 80, 4.58, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'MALE', 81, 120, 4.98, 6.36, 'mg/dL', 'Giá trị bình thường'),
    ( 159, 'FEMALE', 81, 120, 3.84, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 0, 1, 4.43, 5.449999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 0, 1, 3.68, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 2, 5, 4.91, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 2, 5, 3.53, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 6, 12, 3.15, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 6, 12, 4.29, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 13, 18, 3.83, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 13, 18, 3.99, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 19, 35, 4.99, 6.96, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 19, 35, 4.26, 5.97, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 36, 50, 4.36, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 36, 50, 3.48, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 51, 65, 4.0, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 51, 65, 3.94, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 66, 80, 4.65, 6.11, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 66, 80, 3.37, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'MALE', 81, 120, 3.57, 5.869999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 160, 'FEMALE', 81, 120, 4.81, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 0, 1, 3.15, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 0, 1, 4.37, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 2, 5, 4.45, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 2, 5, 3.33, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 6, 12, 4.92, 7.25, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 6, 12, 4.85, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 13, 18, 4.18, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 13, 18, 3.34, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 19, 35, 4.68, 7.119999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 19, 35, 3.09, 4.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 36, 50, 3.85, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 36, 50, 3.55, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 51, 65, 4.65, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 51, 65, 4.93, 6.369999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 66, 80, 3.71, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 66, 80, 4.47, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'MALE', 81, 120, 4.01, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 161, 'FEMALE', 81, 120, 3.1, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 0, 1, 3.78, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 0, 1, 4.57, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 2, 5, 3.03, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 2, 5, 3.99, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 6, 12, 4.64, 6.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 6, 12, 3.75, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 13, 18, 4.62, 7.12, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 13, 18, 3.79, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 19, 35, 4.76, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 19, 35, 4.44, 6.050000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 36, 50, 3.53, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 36, 50, 4.47, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 51, 65, 3.24, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 51, 65, 3.24, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 66, 80, 3.24, 5.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 66, 80, 3.73, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'MALE', 81, 120, 3.55, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 162, 'FEMALE', 81, 120, 4.21, 5.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 0, 1, 3.6, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 0, 1, 4.57, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 2, 5, 3.57, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 2, 5, 4.75, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 6, 12, 3.22, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 6, 12, 4.25, 5.89, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 13, 18, 4.23, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 13, 18, 4.81, 6.55, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 19, 35, 4.15, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 19, 35, 3.95, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 36, 50, 3.01, 5.109999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 36, 50, 3.84, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 51, 65, 3.9, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 51, 65, 3.02, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 66, 80, 4.53, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 66, 80, 3.27, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'MALE', 81, 120, 4.55, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 163, 'FEMALE', 81, 120, 4.95, 7.03, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 0, 1, 4.82, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 0, 1, 3.18, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 2, 5, 4.99, 6.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 2, 5, 4.39, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 6, 12, 4.09, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 6, 12, 3.9, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 13, 18, 4.21, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 13, 18, 3.94, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 19, 35, 4.3, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 19, 35, 3.88, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 36, 50, 4.83, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 36, 50, 4.37, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 51, 65, 4.53, 6.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 51, 65, 3.91, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 66, 80, 4.95, 6.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 66, 80, 3.27, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'MALE', 81, 120, 4.4, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 164, 'FEMALE', 81, 120, 4.84, 7.0, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 0, 1, 4.63, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 0, 1, 4.43, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 2, 5, 4.11, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 2, 5, 3.28, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 6, 12, 4.76, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 6, 12, 4.58, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 13, 18, 3.15, 4.66, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 13, 18, 4.5, 6.76, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 19, 35, 3.44, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 19, 35, 3.26, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 36, 50, 4.25, 6.46, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 36, 50, 4.29, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 51, 65, 4.27, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 51, 65, 3.45, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 66, 80, 3.23, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 66, 80, 4.88, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'MALE', 81, 120, 4.48, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 165, 'FEMALE', 81, 120, 3.97, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 0, 1, 3.38, 4.7, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 0, 1, 4.54, 6.41, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 2, 5, 3.65, 5.3, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 2, 5, 4.39, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 6, 12, 3.14, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 6, 12, 4.38, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 13, 18, 3.18, 4.36, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 13, 18, 3.3, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 19, 35, 3.8, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 19, 35, 4.26, 6.74, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 36, 50, 4.12, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 36, 50, 4.54, 7.03, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 51, 65, 3.86, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 51, 65, 4.69, 6.36, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 66, 80, 3.96, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 66, 80, 4.07, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'MALE', 81, 120, 3.15, 4.56, 'mg/dL', 'Giá trị bình thường'),
    ( 166, 'FEMALE', 81, 120, 4.58, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 0, 1, 3.88, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 0, 1, 4.2, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 2, 5, 4.71, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 2, 5, 4.94, 6.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 6, 12, 3.51, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 6, 12, 3.89, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 13, 18, 3.55, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 13, 18, 3.48, 5.03, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 19, 35, 3.28, 4.89, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 19, 35, 4.04, 6.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 36, 50, 3.54, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 36, 50, 3.29, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 51, 65, 3.03, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 51, 65, 4.42, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 66, 80, 4.17, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 66, 80, 3.19, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'MALE', 81, 120, 3.51, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 167, 'FEMALE', 81, 120, 4.61, 6.130000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 0, 1, 4.09, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 0, 1, 3.49, 5.960000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 2, 5, 4.95, 7.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 2, 5, 3.46, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 6, 12, 3.45, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 6, 12, 3.49, 5.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 13, 18, 4.78, 6.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 13, 18, 3.71, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 19, 35, 4.22, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 19, 35, 3.34, 5.48, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 36, 50, 3.03, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 36, 50, 4.51, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 51, 65, 3.59, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 51, 65, 4.37, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 66, 80, 3.57, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 66, 80, 4.65, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'MALE', 81, 120, 3.12, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 168, 'FEMALE', 81, 120, 3.4, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 0, 1, 4.8, 7.21, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 0, 1, 4.21, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 2, 5, 3.41, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 2, 5, 4.32, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 6, 12, 3.07, 4.27, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 6, 12, 3.43, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 13, 18, 4.61, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 13, 18, 4.48, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 19, 35, 4.63, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 19, 35, 3.84, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 36, 50, 4.56, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 36, 50, 3.21, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 51, 65, 4.36, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 51, 65, 4.94, 6.260000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 66, 80, 3.37, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 66, 80, 3.67, 5.45, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'MALE', 81, 120, 4.92, 6.72, 'mg/dL', 'Giá trị bình thường'),
    ( 169, 'FEMALE', 81, 120, 3.7, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 0, 1, 3.88, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 0, 1, 4.99, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 2, 5, 3.19, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 2, 5, 4.64, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 6, 12, 3.71, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 6, 12, 5.0, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 13, 18, 4.83, 6.68, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 13, 18, 4.86, 5.94, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 19, 35, 3.7, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 19, 35, 4.41, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 36, 50, 4.64, 6.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 36, 50, 3.08, 4.28, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 51, 65, 3.08, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 51, 65, 4.45, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 66, 80, 4.08, 5.44, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 66, 80, 3.11, 4.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'MALE', 81, 120, 3.14, 4.29, 'mg/dL', 'Giá trị bình thường'),
    ( 170, 'FEMALE', 81, 120, 4.77, 7.049999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 0, 1, 3.6, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 0, 1, 4.24, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 2, 5, 3.33, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 2, 5, 4.8, 6.47, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 6, 12, 4.02, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 6, 12, 3.17, 4.32, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 13, 18, 3.0, 4.28, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 13, 18, 4.81, 7.209999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 19, 35, 4.73, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 19, 35, 3.98, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 36, 50, 3.79, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 36, 50, 3.72, 5.720000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 51, 65, 4.77, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 51, 65, 4.74, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 66, 80, 4.26, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 66, 80, 4.91, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'MALE', 81, 120, 4.08, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 171, 'FEMALE', 81, 120, 4.71, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 0, 1, 4.4, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 0, 1, 4.86, 7.2, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 2, 5, 4.68, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 2, 5, 4.06, 6.2299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 6, 12, 3.79, 4.87, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 6, 12, 3.9, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 13, 18, 4.98, 7.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 13, 18, 3.06, 4.72, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 19, 35, 4.24, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 19, 35, 4.2, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 36, 50, 4.72, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 36, 50, 4.11, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 51, 65, 4.93, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 51, 65, 3.65, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 66, 80, 4.0, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 66, 80, 3.89, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'MALE', 81, 120, 3.67, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 172, 'FEMALE', 81, 120, 3.99, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 0, 1, 3.64, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 0, 1, 3.15, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 2, 5, 4.63, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 2, 5, 3.38, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 6, 12, 3.24, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 6, 12, 3.0, 4.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 13, 18, 3.83, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 13, 18, 4.53, 6.25, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 19, 35, 3.88, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 19, 35, 4.0, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 36, 50, 4.07, 5.880000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 36, 50, 3.78, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 51, 65, 4.39, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 51, 65, 4.2, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 66, 80, 4.87, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 66, 80, 3.2, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'MALE', 81, 120, 4.03, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 173, 'FEMALE', 81, 120, 4.6, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 0, 1, 4.01, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 0, 1, 4.72, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 2, 5, 4.78, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 2, 5, 3.13, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 6, 12, 3.86, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 6, 12, 3.19, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 13, 18, 3.73, 5.64, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 13, 18, 4.13, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 19, 35, 3.86, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 19, 35, 3.64, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 36, 50, 3.36, 4.52, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 36, 50, 4.61, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 51, 65, 3.42, 4.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 51, 65, 4.35, 6.75, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 66, 80, 3.47, 4.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 66, 80, 3.5, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'MALE', 81, 120, 3.6, 4.66, 'mg/dL', 'Giá trị bình thường'),
    ( 174, 'FEMALE', 81, 120, 4.54, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 0, 1, 4.98, 6.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 0, 1, 3.21, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 2, 5, 4.37, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 2, 5, 4.68, 7.08, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 6, 12, 4.82, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 6, 12, 4.83, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 13, 18, 4.87, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 13, 18, 3.51, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 19, 35, 3.6, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 19, 35, 4.68, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 36, 50, 4.48, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 36, 50, 3.22, 4.32, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 51, 65, 4.06, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 51, 65, 4.27, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 66, 80, 4.24, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 66, 80, 4.2, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'MALE', 81, 120, 3.95, 5.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 175, 'FEMALE', 81, 120, 4.83, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 0, 1, 4.91, 6.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 0, 1, 4.82, 6.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 2, 5, 3.15, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 2, 5, 4.63, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 6, 12, 4.19, 6.620000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 6, 12, 4.04, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 13, 18, 3.02, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 13, 18, 4.18, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 19, 35, 4.18, 5.8999999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 19, 35, 4.18, 5.56, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 36, 50, 4.29, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 36, 50, 4.41, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 51, 65, 3.13, 4.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 51, 65, 4.9, 7.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 66, 80, 4.74, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 66, 80, 3.15, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'MALE', 81, 120, 4.74, 6.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 176, 'FEMALE', 81, 120, 4.15, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 0, 1, 3.86, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 0, 1, 3.74, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 2, 5, 4.3, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 2, 5, 4.49, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 6, 12, 4.66, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 6, 12, 3.87, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 13, 18, 3.93, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 13, 18, 3.53, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 19, 35, 4.33, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 19, 35, 4.16, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 36, 50, 4.01, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 36, 50, 4.67, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 51, 65, 4.61, 5.84, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 51, 65, 3.15, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 66, 80, 3.41, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 66, 80, 3.92, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'MALE', 81, 120, 3.17, 4.89, 'mg/dL', 'Giá trị bình thường'),
    ( 177, 'FEMALE', 81, 120, 4.69, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 0, 1, 4.4, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 0, 1, 3.62, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 2, 5, 4.27, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 2, 5, 4.73, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 6, 12, 3.33, 4.38, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 6, 12, 4.92, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 13, 18, 4.23, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 13, 18, 4.74, 7.01, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 19, 35, 3.11, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 19, 35, 4.99, 6.69, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 36, 50, 3.62, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 36, 50, 3.85, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 51, 65, 3.51, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 51, 65, 4.43, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 66, 80, 4.64, 5.789999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 66, 80, 3.36, 4.6, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'MALE', 81, 120, 3.69, 5.22, 'mg/dL', 'Giá trị bình thường'),
    ( 178, 'FEMALE', 81, 120, 3.33, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 0, 1, 4.18, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 0, 1, 3.02, 4.35, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 2, 5, 4.71, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 2, 5, 3.61, 4.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 6, 12, 5.0, 6.95, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 6, 12, 4.32, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 13, 18, 4.85, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 13, 18, 4.45, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 19, 35, 4.64, 6.9799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 19, 35, 4.55, 6.59, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 36, 50, 3.16, 4.71, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 36, 50, 3.48, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 51, 65, 4.41, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 51, 65, 4.47, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 66, 80, 4.96, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 66, 80, 4.46, 6.54, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'MALE', 81, 120, 4.77, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 179, 'FEMALE', 81, 120, 4.46, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 0, 1, 3.91, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 0, 1, 4.55, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 2, 5, 4.06, 5.239999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 2, 5, 4.78, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 6, 12, 4.85, 7.1, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 6, 12, 3.15, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 13, 18, 4.43, 6.63, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 13, 18, 3.48, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 19, 35, 4.82, 6.79, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 19, 35, 3.08, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 36, 50, 3.81, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 36, 50, 4.6, 6.959999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 51, 65, 3.81, 5.41, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 51, 65, 4.01, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 66, 80, 4.7, 6.21, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 66, 80, 4.13, 6.32, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'MALE', 81, 120, 4.21, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 180, 'FEMALE', 81, 120, 4.53, 7.0200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 0, 1, 3.57, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 0, 1, 4.28, 6.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 2, 5, 3.29, 4.53, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 2, 5, 4.29, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 6, 12, 3.16, 4.2, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 6, 12, 3.9, 5.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 13, 18, 3.41, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 13, 18, 3.68, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 19, 35, 4.9, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 19, 35, 3.08, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 36, 50, 3.63, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 36, 50, 4.62, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 51, 65, 4.34, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 51, 65, 4.18, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 66, 80, 4.4, 5.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 66, 80, 3.44, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'MALE', 81, 120, 3.65, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 181, 'FEMALE', 81, 120, 3.37, 4.55, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 0, 1, 3.41, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 0, 1, 4.52, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 2, 5, 3.28, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 2, 5, 4.06, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 6, 12, 3.41, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 6, 12, 3.97, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 13, 18, 3.06, 5.24, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 13, 18, 3.16, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 19, 35, 4.81, 7.25, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 19, 35, 3.65, 4.74, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 36, 50, 4.23, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 36, 50, 4.74, 6.94, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 51, 65, 4.26, 5.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 51, 65, 4.09, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 66, 80, 3.59, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 66, 80, 4.91, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'MALE', 81, 120, 4.89, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 182, 'FEMALE', 81, 120, 3.25, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 0, 1, 3.56, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 0, 1, 3.71, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 2, 5, 4.7, 6.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 2, 5, 3.56, 4.57, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 6, 12, 4.4, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 6, 12, 4.94, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 13, 18, 4.39, 5.569999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 13, 18, 3.02, 5.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 19, 35, 3.41, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 19, 35, 4.13, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 36, 50, 4.39, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 36, 50, 4.78, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 51, 65, 4.89, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 51, 65, 3.17, 5.54, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 66, 80, 4.54, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 66, 80, 3.61, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'MALE', 81, 120, 4.44, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 183, 'FEMALE', 81, 120, 4.52, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 0, 1, 3.63, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 0, 1, 4.81, 6.1, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 2, 5, 4.53, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 2, 5, 3.95, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 6, 12, 4.23, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 6, 12, 3.65, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 13, 18, 4.37, 6.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 13, 18, 4.31, 5.81, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 19, 35, 3.23, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 19, 35, 3.44, 5.85, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 36, 50, 4.12, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 36, 50, 4.48, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 51, 65, 4.67, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 51, 65, 4.85, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 66, 80, 3.13, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 66, 80, 3.65, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'MALE', 81, 120, 3.4, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 184, 'FEMALE', 81, 120, 4.1, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 0, 1, 4.52, 6.92, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 0, 1, 3.56, 5.05, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 2, 5, 3.41, 4.49, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 2, 5, 4.62, 6.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 6, 12, 3.4, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 6, 12, 3.78, 5.699999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 13, 18, 4.57, 7.01, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 13, 18, 4.2, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 19, 35, 4.83, 6.5, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 19, 35, 3.58, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 36, 50, 4.07, 6.44, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 36, 50, 3.9, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 51, 65, 4.25, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 51, 65, 3.98, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 66, 80, 3.65, 5.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 66, 80, 4.16, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'MALE', 81, 120, 3.01, 4.41, 'mg/dL', 'Giá trị bình thường'),
    ( 185, 'FEMALE', 81, 120, 4.92, 7.27, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 0, 1, 4.67, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 0, 1, 3.8, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 2, 5, 4.74, 7.09, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 2, 5, 3.87, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 6, 12, 4.36, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 6, 12, 4.38, 6.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 13, 18, 3.52, 5.91, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 13, 18, 4.52, 5.659999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 19, 35, 4.99, 7.07, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 19, 35, 3.07, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 36, 50, 3.57, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 36, 50, 3.95, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 51, 65, 3.85, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 51, 65, 3.08, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 66, 80, 4.4, 5.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 66, 80, 3.42, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'MALE', 81, 120, 4.86, 6.220000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 186, 'FEMALE', 81, 120, 3.15, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 0, 1, 3.01, 4.05, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 0, 1, 4.24, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 2, 5, 4.17, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 2, 5, 3.74, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 6, 12, 4.44, 5.61, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 6, 12, 3.33, 5.0600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 13, 18, 3.78, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 13, 18, 3.01, 4.16, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 19, 35, 4.12, 5.82, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 19, 35, 3.53, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 36, 50, 4.54, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 36, 50, 4.0, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 51, 65, 4.56, 5.72, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 51, 65, 3.16, 5.12, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 66, 80, 4.34, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 66, 80, 3.04, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'MALE', 81, 120, 4.1, 5.539999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 187, 'FEMALE', 81, 120, 3.78, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 0, 1, 4.79, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 0, 1, 3.44, 5.8, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 2, 5, 3.84, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 2, 5, 3.55, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 6, 12, 4.08, 6.04, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 6, 12, 4.46, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 13, 18, 4.44, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 13, 18, 4.12, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 19, 35, 3.59, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 19, 35, 3.95, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 36, 50, 3.33, 4.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 36, 50, 4.63, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 51, 65, 3.64, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 51, 65, 3.12, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 66, 80, 3.3, 5.369999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 66, 80, 4.46, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'MALE', 81, 120, 4.23, 6.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 188, 'FEMALE', 81, 120, 4.74, 7.19, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 0, 1, 4.98, 7.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 0, 1, 4.45, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 2, 5, 4.17, 6.4, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 2, 5, 3.21, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 6, 12, 4.17, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 6, 12, 4.2, 5.46, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 13, 18, 4.5, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 13, 18, 4.86, 6.300000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 19, 35, 3.85, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 19, 35, 3.99, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 36, 50, 4.28, 6.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 36, 50, 4.09, 6.24, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 51, 65, 3.77, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 51, 65, 3.79, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 66, 80, 3.84, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 66, 80, 4.98, 6.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'MALE', 81, 120, 3.41, 4.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 189, 'FEMALE', 81, 120, 4.2, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 0, 1, 4.01, 6.13, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 0, 1, 4.12, 6.38, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 2, 5, 4.38, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 2, 5, 3.04, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 6, 12, 4.41, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 6, 12, 3.23, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 13, 18, 3.13, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 13, 18, 4.45, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 19, 35, 3.69, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 19, 35, 4.54, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 36, 50, 4.71, 5.79, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 36, 50, 4.07, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 51, 65, 3.58, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 51, 65, 4.0, 6.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 66, 80, 4.61, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 66, 80, 4.31, 6.74, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'MALE', 81, 120, 3.32, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 190, 'FEMALE', 81, 120, 3.93, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 0, 1, 3.58, 4.91, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 0, 1, 3.84, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 2, 5, 4.92, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 2, 5, 4.92, 6.98, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 6, 12, 4.18, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 6, 12, 3.98, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 13, 18, 4.37, 5.87, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 13, 18, 3.78, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 19, 35, 4.83, 6.85, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 19, 35, 3.18, 4.95, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 36, 50, 3.87, 6.3100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 36, 50, 3.61, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 51, 65, 4.28, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 51, 65, 4.15, 5.2700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 66, 80, 4.17, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 66, 80, 3.04, 5.04, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'MALE', 81, 120, 3.59, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 191, 'FEMALE', 81, 120, 3.69, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 0, 1, 4.08, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 0, 1, 4.43, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 2, 5, 4.32, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 2, 5, 3.94, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 6, 12, 3.71, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 6, 12, 4.61, 6.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 13, 18, 3.52, 4.67, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 13, 18, 3.27, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 19, 35, 4.06, 5.1499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 19, 35, 4.41, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 36, 50, 4.22, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 36, 50, 3.04, 4.45, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 51, 65, 3.37, 4.54, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 51, 65, 4.49, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 66, 80, 3.95, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 66, 80, 4.55, 6.72, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'MALE', 81, 120, 3.47, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 192, 'FEMALE', 81, 120, 3.64, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 0, 1, 4.37, 5.58, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 0, 1, 3.81, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 2, 5, 3.42, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 2, 5, 4.98, 6.430000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 6, 12, 3.81, 5.07, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 6, 12, 3.55, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 13, 18, 4.25, 6.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 13, 18, 4.76, 7.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 19, 35, 4.65, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 19, 35, 4.1, 5.539999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 36, 50, 4.6, 6.489999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 36, 50, 3.4, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 51, 65, 4.43, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 51, 65, 4.5, 6.14, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 66, 80, 3.99, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 66, 80, 4.14, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'MALE', 81, 120, 4.67, 6.22, 'mg/dL', 'Giá trị bình thường'),
    ( 193, 'FEMALE', 81, 120, 4.5, 6.36, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 0, 1, 4.33, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 0, 1, 4.69, 5.930000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 2, 5, 3.86, 6.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 2, 5, 3.95, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 6, 12, 3.01, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 6, 12, 4.13, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 13, 18, 4.31, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 13, 18, 3.59, 5.38, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 19, 35, 3.15, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 19, 35, 3.86, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 36, 50, 3.7, 5.17, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 36, 50, 3.76, 4.88, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 51, 65, 3.37, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 51, 65, 4.14, 6.56, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 66, 80, 4.51, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 66, 80, 4.21, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'MALE', 81, 120, 4.33, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 194, 'FEMALE', 81, 120, 3.71, 5.18, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 0, 1, 4.31, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 0, 1, 4.62, 6.9, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 2, 5, 4.61, 5.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 2, 5, 4.5, 6.66, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 6, 12, 4.4, 5.680000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 6, 12, 4.78, 6.87, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 13, 18, 3.28, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 13, 18, 3.04, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 19, 35, 4.78, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 19, 35, 4.11, 5.99, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 36, 50, 4.75, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 36, 50, 4.6, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 51, 65, 4.36, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 51, 65, 4.41, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 66, 80, 4.41, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 66, 80, 3.29, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'MALE', 81, 120, 4.32, 6.28, 'mg/dL', 'Giá trị bình thường'),
    ( 195, 'FEMALE', 81, 120, 3.93, 6.34, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 0, 1, 4.35, 5.819999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 0, 1, 4.88, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 2, 5, 3.16, 5.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 2, 5, 3.72, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 6, 12, 3.84, 5.55, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 6, 12, 4.17, 5.98, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 13, 18, 3.13, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 13, 18, 3.98, 6.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 19, 35, 3.05, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 19, 35, 4.82, 7.0, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 36, 50, 4.97, 7.05, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 36, 50, 3.57, 5.5, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 51, 65, 4.99, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 51, 65, 4.88, 7.3, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 66, 80, 3.1, 4.77, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 66, 80, 4.55, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'MALE', 81, 120, 4.46, 6.52, 'mg/dL', 'Giá trị bình thường'),
    ( 196, 'FEMALE', 81, 120, 3.99, 5.5200000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 0, 1, 4.57, 6.93, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 0, 1, 3.59, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 2, 5, 3.33, 5.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 2, 5, 4.46, 5.78, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 6, 12, 3.76, 5.96, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 6, 12, 4.4, 6.800000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 13, 18, 3.21, 4.46, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 13, 18, 3.96, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 19, 35, 4.36, 6.550000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 19, 35, 3.33, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 36, 50, 3.06, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 36, 50, 3.14, 4.33, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 51, 65, 4.85, 7.34, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 51, 65, 3.19, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 66, 80, 4.47, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 66, 80, 3.53, 4.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'MALE', 81, 120, 4.84, 6.91, 'mg/dL', 'Giá trị bình thường'),
    ( 197, 'FEMALE', 81, 120, 3.21, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 0, 1, 3.18, 4.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 0, 1, 4.59, 6.29, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 2, 5, 4.95, 7.33, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 2, 5, 3.79, 6.08, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 6, 12, 3.93, 4.96, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 6, 12, 4.72, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 13, 18, 3.96, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 13, 18, 3.3, 4.79, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 19, 35, 3.49, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 19, 35, 4.58, 6.45, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 36, 50, 4.82, 6.17, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 36, 50, 4.95, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 51, 65, 4.12, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 51, 65, 4.26, 6.48, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 66, 80, 3.62, 4.84, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 66, 80, 4.79, 6.33, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'MALE', 81, 120, 3.12, 4.94, 'mg/dL', 'Giá trị bình thường'),
    ( 198, 'FEMALE', 81, 120, 3.54, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 0, 1, 4.41, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 0, 1, 3.59, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 2, 5, 4.31, 5.47, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 2, 5, 3.51, 5.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 6, 12, 4.34, 5.529999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 6, 12, 4.97, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 13, 18, 4.22, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 13, 18, 4.01, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 19, 35, 4.93, 6.42, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 19, 35, 4.35, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 36, 50, 4.3, 5.77, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 36, 50, 3.24, 4.65, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 51, 65, 4.16, 5.86, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 51, 65, 3.06, 4.43, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 66, 80, 4.5, 6.99, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 66, 80, 4.23, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'MALE', 81, 120, 4.56, 6.1499999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 199, 'FEMALE', 81, 120, 3.16, 4.46, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 0, 1, 4.85, 7.31, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 0, 1, 3.77, 4.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 2, 5, 3.01, 4.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 2, 5, 3.81, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 6, 12, 3.79, 5.68, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 6, 12, 3.37, 4.390000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 13, 18, 4.25, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 13, 18, 4.09, 6.27, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 19, 35, 3.1, 5.32, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 19, 35, 3.39, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 36, 50, 3.55, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 36, 50, 4.94, 6.37, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 51, 65, 4.98, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 51, 65, 3.19, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 66, 80, 4.66, 5.93, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 66, 80, 4.98, 6.94, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'MALE', 81, 120, 3.84, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 200, 'FEMALE', 81, 120, 4.57, 6.26, 'mg/dL', 'Giá trị bình thường'),
-- ( 201, 'MALE', 0, 1, 3.15, 4.6, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 0, 1, 3.74, 4.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 2, 5, 3.29, 5.2, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 2, 5, 3.4, 5.88, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 6, 12, 4.82, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 6, 12, 3.06, 4.42, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 13, 18, 4.06, 5.39, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 13, 18, 4.95, 7.32, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 19, 35, 4.09, 6.51, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 19, 35, 4.44, 6.12, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 36, 50, 3.08, 5.09, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 36, 50, 3.86, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 51, 65, 3.47, 5.69, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 51, 65, 4.22, 6.4799999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 66, 80, 3.15, 5.42, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 66, 80, 3.57, 5.4399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'MALE', 81, 120, 3.75, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 201, 'FEMALE', 81, 120, 3.96, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 0, 1, 4.04, 5.08, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 0, 1, 3.75, 5.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 2, 5, 3.31, 5.57, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 2, 5, 4.18, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 6, 12, 3.37, 5.71, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 6, 12, 3.31, 5.27, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 13, 18, 4.9, 6.1000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 13, 18, 3.64, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 19, 35, 4.53, 6.82, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 19, 35, 4.49, 5.73, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 36, 50, 3.99, 6.09, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 36, 50, 3.59, 5.67, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 51, 65, 4.38, 5.9, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 51, 65, 3.71, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 66, 80, 4.11, 5.7, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 66, 80, 3.71, 4.98, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'MALE', 81, 120, 3.86, 4.9, 'mg/dL', 'Giá trị bình thường'),
    ( 202, 'FEMALE', 81, 120, 4.65, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 0, 1, 4.53, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 0, 1, 3.58, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 2, 5, 3.88, 6.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 2, 5, 4.2, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 6, 12, 3.03, 4.4, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 6, 12, 3.52, 4.93, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 13, 18, 3.4, 4.68, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 13, 18, 5.0, 6.16, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 19, 35, 3.96, 5.02, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 19, 35, 4.11, 5.74, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 36, 50, 4.78, 6.2, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 36, 50, 3.32, 4.8, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 51, 65, 3.82, 5.9399999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 51, 65, 3.2, 5.0, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 66, 80, 4.4, 5.75, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 66, 80, 4.61, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'MALE', 81, 120, 3.53, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 203, 'FEMALE', 81, 120, 4.31, 6.629999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 0, 1, 3.66, 5.26, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 0, 1, 4.04, 5.37, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 2, 5, 4.84, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 2, 5, 3.72, 6.07, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 6, 12, 4.18, 5.35, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 6, 12, 4.24, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 13, 18, 3.66, 5.01, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 13, 18, 3.39, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 19, 35, 3.6, 5.140000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 19, 35, 3.53, 4.56, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 36, 50, 3.59, 6.05, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 36, 50, 4.21, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 51, 65, 3.37, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 51, 65, 3.59, 4.6899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 66, 80, 3.33, 5.28, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 66, 80, 4.97, 6.49, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'MALE', 81, 120, 4.44, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 204, 'FEMALE', 81, 120, 4.52, 6.619999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 0, 1, 4.34, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 0, 1, 4.26, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 2, 5, 4.11, 5.470000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 2, 5, 3.93, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 6, 12, 3.37, 4.73, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 6, 12, 4.82, 7.16, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 13, 18, 3.88, 6.23, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 13, 18, 4.16, 5.16, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 19, 35, 4.26, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 19, 35, 3.75, 6.02, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 36, 50, 4.74, 6.03, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 36, 50, 4.99, 6.86, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 51, 65, 3.45, 4.85, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 51, 65, 4.75, 6.01, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 66, 80, 4.71, 6.6, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 66, 80, 4.51, 5.76, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'MALE', 81, 120, 4.28, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 205, 'FEMALE', 81, 120, 3.21, 5.29, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 0, 1, 3.22, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 0, 1, 3.29, 5.1, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 2, 5, 3.06, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 2, 5, 4.68, 6.06, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 6, 12, 4.19, 5.4, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 6, 12, 4.43, 5.63, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 13, 18, 3.92, 5.06, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 13, 18, 3.02, 4.3, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 19, 35, 4.49, 6.630000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 19, 35, 4.64, 5.7299999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 36, 50, 3.99, 6.26, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 36, 50, 3.89, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 51, 65, 3.84, 5.25, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 51, 65, 4.37, 5.51, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 66, 80, 4.6, 6.779999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 66, 80, 4.61, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'MALE', 81, 120, 3.83, 5.19, 'mg/dL', 'Giá trị bình thường'),
    ( 206, 'FEMALE', 81, 120, 4.14, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 0, 1, 3.54, 4.75, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 0, 1, 3.94, 5.49, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 2, 5, 3.16, 5.15, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 2, 5, 4.31, 5.949999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 6, 12, 4.75, 6.53, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 6, 12, 4.44, 6.260000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 13, 18, 3.0, 4.61, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 13, 18, 3.81, 5.36, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 19, 35, 3.04, 4.97, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 19, 35, 3.62, 5.23, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 36, 50, 4.5, 6.67, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 36, 50, 4.93, 6.43, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 51, 65, 3.22, 4.7700000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 51, 65, 4.49, 5.6000000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 66, 80, 3.78, 5.279999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 66, 80, 3.41, 4.5, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'MALE', 81, 120, 3.49, 5.13, 'mg/dL', 'Giá trị bình thường'),
    ( 207, 'FEMALE', 81, 120, 4.43, 6.789999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 0, 1, 3.52, 5.95, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 0, 1, 3.22, 5.33, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 2, 5, 4.0, 6.1899999999999995, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 2, 5, 4.97, 7.47, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 6, 12, 3.03, 5.31, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 6, 12, 4.51, 6.71, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 13, 18, 3.34, 4.62, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 13, 18, 3.14, 5.59, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 19, 35, 4.48, 5.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 19, 35, 4.13, 6.62, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 36, 50, 3.41, 4.82, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 36, 50, 3.78, 5.14, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 51, 65, 4.67, 6.57, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 51, 65, 3.12, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 66, 80, 4.73, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 66, 80, 4.58, 6.5600000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'MALE', 81, 120, 3.88, 5.83, 'mg/dL', 'Giá trị bình thường'),
    ( 208, 'FEMALE', 81, 120, 3.22, 4.54, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 0, 1, 4.8, 6.72, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 0, 1, 4.94, 6.83, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 2, 5, 4.97, 6.609999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 2, 5, 4.49, 5.65, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 6, 12, 4.75, 6.970000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 6, 12, 3.75, 4.99, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 13, 18, 3.3, 4.51, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 13, 18, 4.39, 5.6, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 19, 35, 4.42, 5.859999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 19, 35, 4.12, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 36, 50, 3.44, 5.66, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 36, 50, 4.59, 7.02, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 51, 65, 4.3, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 51, 65, 3.31, 5.34, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 66, 80, 3.35, 5.62, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 66, 80, 4.04, 5.890000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'MALE', 81, 120, 4.96, 7.17, 'mg/dL', 'Giá trị bình thường'),
    ( 209, 'FEMALE', 81, 120, 4.21, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 0, 1, 3.17, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 0, 1, 3.66, 5.21, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 2, 5, 4.07, 6.380000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 2, 5, 4.64, 7.029999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 6, 12, 4.71, 6.0, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 6, 12, 3.29, 5.359999999999999, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 13, 18, 4.41, 5.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 13, 18, 4.02, 5.43, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 19, 35, 4.95, 6.35, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 19, 35, 4.75, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 36, 50, 3.49, 5.53, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 36, 50, 3.87, 5.640000000000001, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 51, 65, 4.0, 6.18, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 51, 65, 4.08, 5.52, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 66, 80, 4.54, 6.15, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 66, 80, 3.31, 4.8100000000000005, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'MALE', 81, 120, 4.96, 6.58, 'mg/dL', 'Giá trị bình thường'),
    ( 210, 'FEMALE', 81, 120, 4.85, 5.89, 'mg/dL', 'Giá trị bình thường');

-- Chèn dữ liệu vào bảng products, KHÔNG bao gồm loại TEST
INSERT INTO products (
    product_type,
    name,
    description,
    price,
    unit,
    product_status,
    stock_quantities,
    image_url,
    label
)
WITH RECURSIVE product_generator AS (
    SELECT 1 AS id,
           ELT(FLOOR(1 + RAND() * 4), 'PRICING_PLAN', 'SERVICE', 'MEDICINE', 'MEDICAL_PRODUCT') AS prod_type
    UNION ALL
    SELECT id + 1,
           ELT(FLOOR(1 + RAND() * 4), 'PRICING_PLAN', 'SERVICE', 'MEDICINE', 'MEDICAL_PRODUCT')
    FROM product_generator
    WHERE id < @product_count
)
SELECT
    prod_type,
    CONCAT(
            ELT(FLOOR(1 + RAND() * 10),
                'Premium', 'Advanced', 'Standard', 'Basic', 'Professional',
                'Complete', 'Specialized', 'Essential', 'Daily', 'Critical'),
            ' ',
            ELT(FLOOR(1 + RAND() * 10),
                'Health', 'Medical', 'Care', 'Wellness', 'Treatment',
                'Therapy', 'Diagnostic', 'Screening', 'Support', 'Assessment'),
            ' ',
            ELT(FLOOR(1 + RAND() * 10),
                'Package', 'Service', 'Product', 'Solution',
                'Kit', 'System', 'Set', 'Formula', 'Suite', 'Tool')
    ),
    CONCAT(
            ELT(FLOOR(1 + RAND() * 5),
                'Comprehensive solution for ',
                'Effective approach to ',
                'Professional grade product for ',
                'Specialized service addressing ',
                'Quality care focused on '),
            ELT(FLOOR(1 + RAND() * 5),
                'maintaining optimal health. ',
                'improving patient outcomes. ',
                'addressing common health concerns. ',
                'enhancing quality of life. ',
                'supporting medical treatment. '),
            ELT(FLOOR(1 + RAND() * 5),
                'Recommended by healthcare professionals. ',
                'Backed by scientific research. ',
                'Used in leading medical facilities. ',
                'Trusted by patients worldwide. ',
                'Following industry best practices. '),
            ELT(FLOOR(1 + RAND() * 5),
                'Includes detailed guidance for best results.',
                'Features premium components for reliability.',
                'Designed with patient comfort in mind.',
                'Offers excellent value for quality medical care.',
                'Developed by experts in the field.')
    ),
    CASE
        WHEN prod_type = 'PRICING_PLAN' THEN 199.99 + FLOOR(RAND() * 10) * 100
        WHEN prod_type = 'SERVICE' THEN 39.99 + FLOOR(RAND() * 16) * 10
        WHEN prod_type = 'MEDICINE' THEN 4.99 + FLOOR(RAND() * 30)
        ELSE 9.99 + FLOOR(RAND() * 15) * 5
        END,
    CASE
        WHEN prod_type = 'PRICING_PLAN' THEN ELT(FLOOR(1 + RAND() * 3), 'package', 'plan', 'program')
        WHEN prod_type = 'SERVICE' THEN ELT(FLOOR(1 + RAND() * 4), 'session', 'procedure', 'visit', 'service')
        WHEN prod_type = 'MEDICINE' THEN ELT(FLOOR(1 + RAND() * 4), 'bottle', 'pack', 'box', 'dose')
        ELSE ELT(FLOOR(1 + RAND() * 4), 'piece', 'kit', 'unit', 'set')
        END,
    IF(RAND() < 0.9, 'ACTIVE', 'INACTIVE'),
    CASE
        WHEN prod_type = 'PRICING_PLAN' THEN 10 + FLOOR(RAND() * 30)
        WHEN prod_type = 'SERVICE' THEN 800 + FLOOR(RAND() * 200)
        WHEN prod_type = 'MEDICINE' THEN 50 + FLOOR(RAND() * 150)
        ELSE 20 + FLOOR(RAND() * 80)
        END,
    CONCAT('https://api.dicebear.com/9.x/icons/svg?seed=', 210 + id),
    CASE
        WHEN RAND() < 0.2 THEN 'NEW'
        WHEN RAND() < 0.35 THEN 'SALE'
        ELSE 'STANDARD'
        END
FROM product_generator;


-- Generate sample data for services table
-- Chèn dữ liệu vào bảng services từ các sản phẩm loại SERVICE
INSERT INTO services (service_id, department_id)
SELECT p.product_id,
       (SELECT department_id FROM departments ORDER BY RAND() LIMIT 1) AS department_id
FROM products p
WHERE p.product_type = 'SERVICE'
  AND p.product_status = 'ACTIVE';

-- Generate sample data for service_features table
INSERT INTO service_features (service_id, name, description)
WITH RECURSIVE feature_generator AS (SELECT service_id,
                                            1 AS feature_num
                                     FROM services

                                     UNION ALL

                                     SELECT service_id,
                                            feature_num + 1
                                     FROM feature_generator
                                     WHERE feature_num < 3 + MOD(service_id, 3) -- 3-5 tính năng cho mỗi dịch vụ
)
SELECT
    -- service_id
    fg.service_id,

    -- name: Tên tính năng dựa trên thứ tự
    CASE
        WHEN fg.feature_num = 1 THEN 'Professional Consultation'
        WHEN fg.feature_num = 2 THEN 'Comprehensive Assessment'
        WHEN fg.feature_num = 3 THEN 'Detailed Documentation'
        WHEN fg.feature_num = 4 THEN 'Follow-up Care'
        ELSE 'Priority Scheduling'
        END,

    -- description: Mô tả chi tiết về tính năng
    CASE
        WHEN fg.feature_num = 1
            THEN 'One-on-one session with a qualified healthcare professional to address your specific needs and concerns.'
        WHEN fg.feature_num = 2
            THEN 'Thorough evaluation of your health status using modern diagnostic methods and professional expertise.'
        WHEN fg.feature_num = 3
            THEN 'Complete medical records and recommendations provided in both digital and print formats for your reference.'
        WHEN fg.feature_num = 4
            THEN 'Additional check-up appointments included to monitor progress and adjust treatment as needed.'
        ELSE 'Flexible scheduling options with priority booking for urgent cases and minimal waiting time.'
        END

FROM feature_generator fg;

-- Generate sample data for appointments table
-- Thiết lập biến
SET @current_date = '2025-06-18';

-- Tạo dữ liệu lịch hẹn
INSERT INTO appointments (doctor_id,
                          patient_id,
                          service_id,
                          start_time,
                          duration_minutes,
                          appointment_status,
                          scheduling_coordinator_id)
WITH random_data AS (
    SELECT d.doctor_id
    FROM doctors d
    ORDER BY RAND()),
     random_patients AS (
         SELECT p.patient_id
         FROM patients p
         ORDER BY RAND()),
     random_services AS (
         SELECT s.service_id
         FROM services s
         ORDER BY RAND()),
     random_schedulers AS (
         SELECT sc.scheduling_coordinator_id
         FROM scheduling_coordinators sc
         ORDER BY RAND()),
     appointment_times AS (
         -- Tạo bảng thời gian
         WITH RECURSIVE counter AS (
             SELECT 1 AS num
             UNION ALL
             SELECT num + 1
             FROM counter
             WHERE num < @appointment_count
         )
         SELECT
             num,
             -- Ngày lịch hẹn: 40% quá khứ, 10% hiện tại, 50% tương lai
             CASE
                 WHEN num <= @appointment_count * 0.7 THEN
                     DATE_ADD(@current_date, INTERVAL -1 * (1 + MOD(num, 15)) DAY)
                 WHEN num <= @appointment_count * 0.8 THEN
                     @current_date
                 ELSE
                     DATE_ADD(@current_date, INTERVAL (1 + MOD(num, 15)) DAY)
                 END AS appointment_date,
             -- Giờ từ 8:00 đến 17:00
             8 + MOD(num, 9) AS hour,
             -- Phút: 15, 30
             15 * (1 + MOD(num, 2)) AS minute
         FROM counter
     )
SELECT
    -- Bác sĩ ngẫu nhiên
    (SELECT doctor_id FROM random_data ORDER BY RAND() LIMIT 1),
    -- Bệnh nhân ngẫu nhiên
    (SELECT patient_id FROM random_patients ORDER BY RAND() LIMIT 1),
    -- Dịch vụ ngẫu nhiên
    (SELECT service_id FROM random_services ORDER BY RAND() LIMIT 1),
    -- Thời gian bắt đầu
    @start_time := TIMESTAMP(at.appointment_date, CONCAT(at.hour, ':', at.minute, ':00')),
    -- Thời lượng 15-30 phút
    15 * (1 + MOD(at.num, 2)),
    -- Trạng thái dựa theo thời gian
    CASE
        -- Quá khứ (đã hoàn thành hoặc hủy)
        WHEN at.appointment_date < @current_date THEN
            IF(RAND() < 0.9, 'COMPLETED', 'CANCELLED')
        -- Hôm nay (đang thực hiện hoặc đã xác nhận)
        WHEN at.appointment_date = @current_date THEN
            IF(at.hour < 4, 'COMPLETED',
               IF(at.hour = 4, 'IN_PROGRESS', 'CONFIRMED'))
        -- 1-3 ngày tới (đã xác nhận)
        WHEN DATEDIFF(at.appointment_date, @current_date) <= 3 THEN
            IF(RAND() < 0.9, 'CONFIRMED', 'CANCELLED')
        -- Tương lai xa (chờ xác nhận hoặc đã xác nhận)
        ELSE
            IF(RAND() < 0.7, 'PENDING', 'CONFIRMED')
        END,
    -- Điều phối viên ngẫu nhiên
    (SELECT scheduling_coordinator_id FROM random_schedulers ORDER BY RAND() LIMIT 1)
FROM appointment_times at;

-- Generate sample data for medical_records table
-- Create data for medical_records table
-- Only create records for COMPLETED appointments
INSERT INTO medical_profiles (patient_id, allergies, chronic_diseases)
SELECT a.patient_id,

       -- Chọn ngẫu nhiên một giá trị JSON cho allergies
       ELT(
               FLOOR(1 + RAND() * 6),
               '["No known allergies"]',
               '["Penicillin", "Amoxicillin"]',
               '["Aspirin", "NSAIDs"]',
               '["Seafood", "Peanuts"]',
               '["Pollen", "House dust"]',
               '["Latex"]'
       ) AS allergies,

       -- Chọn ngẫu nhiên một giá trị JSON cho chronic_diseases
       ELT(
               FLOOR(1 + RAND() * 6),
               '["No chronic diseases"]',
               '["Hypertension", "Type 2 Diabetes"]',
               '["Asthma", "Chronic bronchitis"]',
               '["Rheumatoid arthritis"]',
               '["Thyroid disorder", "Anemia"]',
               '["Dyslipidemia", "Coronary artery disease"]'
       ) AS chronic_diseases

FROM appointments a
WHERE a.appointment_status = 'COMPLETED'
GROUP BY a.patient_id;

INSERT INTO medical_records (
    patient_id,
    appointment_id,
    admission_date,
    discharge_date,
    main_complaint,
    diagnosis,
    treatment_plan,
    outcome
)
SELECT
    a.patient_id,
    a.appointment_id,

    -- admission_date: ngày bắt đầu cuộc hẹn
    DATE(a.start_time) AS admission_date,

    -- discharge_date: 80% cùng ngày, 10% +1 ngày, 10% +2–4 ngày
    CASE
        WHEN RAND() < 0.8
            THEN DATE(a.start_time)
        WHEN RAND() < 0.9
            THEN DATE_ADD(DATE(a.start_time), INTERVAL 1 DAY)
        ELSE DATE_ADD(DATE(a.start_time), INTERVAL 2 + FLOOR(RAND() * 3) DAY)
        END AS discharge_date,

    -- main_complaint: triệu chứng chính
    ELT(
            FLOOR(1 + RAND() * 15),
            'Severe persistent headache',
            'Sore throat with high fever',
            'Upper abdominal pain',
            'Persistent cough with difficulty breathing',
            'Knee joint pain during movement',
            'Dizziness and nausea',
            'Prolonged fatigue of unknown cause',
            'Skin rash with itching',
            'Left chest pain radiating to shoulder',
            'Chronic sleep disorder',
            'Unexplained weight loss',
            'Chronic lower back pain',
            'Diarrhea and abdominal pain',
            'Shortness of breath during exertion',
            'Tinnitus and hearing loss'
    ) AS main_complaint,

    -- diagnosis: chẩn đoán chi tiết
    CASE
        WHEN FLOOR(RAND() * 15) = 0
            THEN 'Migraine headache: common type, episodic, moderate severity. Group II according to international classification.'
        WHEN FLOOR(RAND() * 15) = 1
            THEN 'Acute tonsillitis due to Streptococcus, with persistent fever >38.5°C, purulent exudate on tonsillar surface.'
        WHEN FLOOR(RAND() * 15) = 2
            THEN 'Acute gastritis: gastric mucosa congestion, mild gastroesophageal reflux present.'
        WHEN FLOOR(RAND() * 15) = 3
            THEN 'Acute bronchitis: increased sputum production, airway edema, no signs of pneumonia.'
        WHEN FLOOR(RAND() * 15) = 4
            THEN 'Primary knee osteoarthritis, grade II cartilage damage, small osteophyte formation.'
        WHEN FLOOR(RAND() * 15) = 5
            THEN 'Peripheral vestibular disorder: benign paroxysmal positional vertigo (BPPV), right semicircular canal.'
        WHEN FLOOR(RAND() * 15) = 6
            THEN 'Chronic fatigue syndrome, no organic cause identified, prolonged psychological stress factors present.'
        WHEN FLOOR(RAND() * 15) = 7
            THEN 'Allergic contact dermatitis, type IV reaction, with erythema, itching, scaling in contact areas.'
        WHEN FLOOR(RAND() * 15) = 8
            THEN 'Unstable angina, suspected myocardial ischemia, further cardiac function assessment needed.'
        WHEN FLOOR(RAND() * 15) = 9
            THEN 'Primary sleep disorder: initial insomnia, difficulty maintaining sleep, early waking. Moderate severity.'
        WHEN FLOOR(RAND() * 15) = 10
            THEN 'Physical asthenia, mild nutritional deficiency, suspected vitamin D and iron deficiency.'
        WHEN FLOOR(RAND() * 15) = 11
            THEN 'Lumbar disc herniation L4-L5, mild nerve root compression, no motor function impairment.'
        WHEN FLOOR(RAND() * 15) = 12
            THEN 'Irritable bowel syndrome, diarrhea-predominant, related to psychological stress factors.'
        WHEN FLOOR(RAND() * 15) = 13
            THEN 'Chronic obstructive pulmonary disease (COPD) stage II, mild respiratory function impairment, FEV1/FVC < 70%, FEV1 60% predicted.'
        ELSE
            'Serous otitis media, intact tympanic membrane, middle ear fluid effusion, conductive hearing loss of 30dB.'
        END AS diagnosis,

    -- treatment_plan: phác đồ điều trị
    CASE
        WHEN FLOOR(RAND() * 15) = 0
            THEN 'Sumatriptan 50mg for acute attacks. Propranolol 40mg daily for prevention. Avoid triggers. Follow-up in 4 weeks.'
        WHEN FLOOR(RAND() * 15) = 1
            THEN 'Amoxicillin 500mg three times daily for 7 days. Paracetamol 500mg for fever >38.5°C. Salt water gargles. Rest and hydration.'
        WHEN FLOOR(RAND() * 15) = 2
            THEN 'Omeprazole 20mg once daily before breakfast for 14 days. Light diet, avoid spicy foods, caffeine, alcohol. Follow-up in 2 weeks.'
        WHEN FLOOR(RAND() * 15) = 3
            THEN 'Ambroxol 30mg three times daily. Salbutamol inhaler as needed. Antibiotics if bacterial infection suspected. Increased fluid intake, rest.'
        WHEN FLOOR(RAND() * 15) = 4
            THEN 'Glucosamine sulfate 1500mg daily for 3 months. Physical therapy twice weekly for 1 month. Weight reduction, gentle exercise. Follow-up in 1 month.'
        WHEN FLOOR(RAND() * 15) = 5
            THEN 'Epley maneuver exercises three times daily. Betahistine 16mg three times daily for 14 days. Avoid sudden position changes. Follow-up in 2 weeks.'
        WHEN FLOOR(RAND() * 15) = 6
            THEN 'Work-rest schedule adjustment. Multivitamin and mineral supplements. Psychological counseling. Light exercise. Tests to exclude organic causes.'
        WHEN FLOOR(RAND() * 15) = 7
            THEN 'Methylprednisolone 4mg with tapering dose over 7 days. Loratadine 10mg each morning. Mometasone 0.1% cream twice daily. Avoid allergen contact. Follow-up in 1 week.'
        WHEN FLOOR(RAND() * 15) = 8
            THEN 'Refer to Cardiology. Aspirin 81mg daily. Sublingual nitroglycerin as needed. Stress ECG and echocardiogram. Home blood pressure monitoring.'
        WHEN FLOOR(RAND() * 15) = 9
            THEN 'Sleep hygiene. Zolpidem 5mg before bedtime (for 5 days). Relaxation techniques counseling. Follow-up in 2 weeks.'
        WHEN FLOOR(RAND() * 15) = 10
            THEN 'Multivitamin and mineral supplements. Iron 60mg daily. Vitamin D3 1000 IU daily. Nutrition counseling. Blood tests in 1 month.'
        WHEN FLOOR(RAND() * 15) = 11
            THEN 'Meloxicam 7.5mg once daily after meals for 10 days. Physical therapy. Avoid heavy lifting and prolonged sitting. Follow-up in 2 weeks with MRI results.'
        WHEN FLOOR(RAND() * 15) = 12
            THEN 'Loperamide 2mg as needed for diarrhea. Mebeverine 135mg three times daily 30 minutes before meals. Diet rich in soluble fiber. Stress reduction. Follow-up in 1 month.'
        WHEN FLOOR(RAND() * 15) = 13
            THEN 'Tiotropium 18mcg inhaler once daily. Budesonide inhaler as needed. Breathing exercises. Smoking cessation. Influenza and pneumococcal vaccines.'
        ELSE
            'Cefuroxime 250mg twice daily for 5 days. Fluticasone nasal spray twice daily. Saline solution for sinus irrigation. Follow-up in 1 week.'
        END AS treatment_plan,

    -- outcome: kết quả điều trị
    CASE
        WHEN RAND() < 0.7 THEN 'Condition resolved, good response to treatment'
        WHEN RAND() < 0.9 THEN 'Partially improved, continued monitoring required'
        ELSE 'No significant improvement, treatment plan adjustment needed'
        END AS outcome

FROM appointments a
WHERE a.appointment_status = 'COMPLETED';


INSERT INTO test_requests (appointment_id)
SELECT appointment_id
FROM appointments
WHERE appointment_status = 'COMPLETED'
-- Only create test requests for about 70% of appointments
  AND RAND() < 0.7
ORDER BY appointment_id;

-- Create detailed test request items with realistic medical reasons
INSERT INTO test_request_items (test_id, test_request_id, technician_id, reason, result)
WITH RECURSIVE
    counter AS (SELECT 1 as n
                UNION ALL
                SELECT n + 1
                FROM counter
                WHERE n < @test_request_count),
    test_request_expanded AS (
        -- Generate 1-3 rows per test request
        SELECT tr.test_request_id,
               c.n
        FROM test_requests tr
                 JOIN counter c ON c.n <= 1 + FLOOR(RAND() * 3) -- 1-3 tests per request
    ),
    available_tests AS (SELECT test_id
                        FROM tests)
SELECT
    -- FIXED: Select a test_id that hasn't been used for this test_request_id
    (SELECT test_id
     FROM available_tests
     WHERE test_id NOT IN (SELECT test_id
                           FROM test_request_items
                           WHERE test_request_id = tre.test_request_id)
     ORDER BY RAND()
     LIMIT 1),

    -- test_request_id
    tre.test_request_id,

    -- Assign a technician randomly
    (
        SELECT t.technician_id
        FROM technicians t
                 JOIN staffs s ON t.technician_id = s.staff_id
        ORDER BY RAND()
        LIMIT 1
    ) AS technician_id,

    -- Provide a specific medical reason for each test
    CASE FLOOR(RAND() * 15)
        WHEN 0 THEN 'Screening for anemia and overall blood health assessment. Patient reports fatigue and pallor.'
        WHEN 1 THEN 'Monitor liver function during current medication regimen. Patient on hepatotoxic drugs.'
        WHEN 2 THEN 'Evaluate kidney function. Patient has history of hypertension and recent urinary symptoms.'
        WHEN 3 THEN 'Check lipid profile for cardiovascular risk assessment. Family history of heart disease.'
        WHEN 4 THEN 'Rule out diabetes mellitus. Patient presents with polyuria and unexplained weight loss.'
        WHEN 5
            THEN 'Assess thyroid function. Patient shows symptoms of possible hypothyroidism including fatigue and cold intolerance.'
        WHEN 6 THEN 'Determine vitamin D levels. Patient with history of minimal sun exposure and poor dietary intake.'
        WHEN 7
            THEN 'Check for rheumatoid factor and inflammatory markers. Patient presents with joint pain and morning stiffness.'
        WHEN 8 THEN 'Electrolyte panel to assess hydration status and electrolyte balance. Patient reports dizziness.'
        WHEN 9 THEN 'Evaluate blood coagulation factors. Patient scheduled for surgical procedure.'
        WHEN 10 THEN 'Rule out urinary tract infection. Patient presents with dysuria and frequency.'
        WHEN 11
            THEN 'Assess blood oxygen saturation levels. Patient reports shortness of breath during minimal exertion.'
        WHEN 12 THEN 'Monitor therapeutic drug levels. Patient on medication requiring narrow therapeutic range.'
        WHEN 13 THEN 'Evaluate for potential allergic reactions. Patient reports seasonal symptoms and skin reactions.'
        ELSE 'Complete blood count for preoperative assessment. Standard protocol for surgical preparation.'
        END,

    -- Create JSON test results based on test type (randomly assigned)
    CASE FLOOR(RAND() * 5)
        -- Blood glucose test results
        WHEN 0 THEN
            JSON_OBJECT(
                    'test_date', DATE_FORMAT(DATE_ADD('2025-06-08', INTERVAL -FLOOR(RAND() * 5) DAY), '%Y-%m-%d'),
                    'test_name', 'Blood Glucose Test',
                    'fasting_status', IF(RAND() > 0.5, 'Fasting', 'Non-fasting'),
                    'results', JSON_OBJECT(
                            'glucose_level', ROUND(70 + RAND() * 180, 1),
                            'unit', 'mg/dL',
                            'reference_range', JSON_OBJECT('min', 70, 'max', 99)
                               ),
                    'interpretation', IF(ROUND(70 + RAND() * 180, 1) > 126, 'Elevated - Diabetes suspected', 'Normal'),
                    'comments', 'Results analyzed using Hexokinase method.'
            )

        -- Complete Blood Count (CBC) results
        WHEN 1 THEN
            JSON_OBJECT(
                    'test_date', DATE_FORMAT(DATE_ADD('2025-06-08', INTERVAL -FLOOR(RAND() * 5) DAY), '%Y-%m-%d'),
                    'test_name', 'Complete Blood Count (CBC)',
                    'results', JSON_OBJECT(
                            'hemoglobin', JSON_OBJECT(
                            'value', ROUND(11 + RAND() * 6, 1),
                            'unit', 'g/dL',
                            'reference_range', JSON_OBJECT('min', 12, 'max', 16)
                                          ),
                            'white_blood_cells', JSON_OBJECT(
                                    'value', ROUND(4 + RAND() * 11, 1),
                                    'unit', '10^3/μL',
                                    'reference_range', JSON_OBJECT('min', 4.5, 'max', 11.0)
                                                 ),
                            'platelets', JSON_OBJECT(
                                    'value', ROUND(140 + RAND() * 300, 0),
                                    'unit', '10^3/μL',
                                    'reference_range', JSON_OBJECT('min', 150, 'max', 450)
                                         )
                               ),
                    'interpretation', 'All parameters within normal clinical limits.',
                    'comments', 'Analyzed using automated hematology analyzer.'
            )

        -- Lipid Profile results
        WHEN 2 THEN
            JSON_OBJECT(
                    'test_date', DATE_FORMAT(DATE_ADD('2025-06-08', INTERVAL -FLOOR(RAND() * 5) DAY), '%Y-%m-%d'),
                    'test_name', 'Lipid Profile',
                    'fasting_status', 'Fasting (12 hours)',
                    'results', JSON_OBJECT(
                            'total_cholesterol', JSON_OBJECT(
                            'value', ROUND(150 + RAND() * 100, 0),
                            'unit', 'mg/dL',
                            'reference_range', JSON_OBJECT('min', 0, 'max', 200)
                                                 ),
                            'hdl_cholesterol', JSON_OBJECT(
                                    'value', ROUND(35 + RAND() * 40, 0),
                                    'unit', 'mg/dL',
                                    'reference_range', JSON_OBJECT('min', 40, 'max', 60)
                                               ),
                            'ldl_cholesterol', JSON_OBJECT(
                                    'value', ROUND(70 + RAND() * 130, 0),
                                    'unit', 'mg/dL',
                                    'reference_range', JSON_OBJECT('min', 0, 'max', 100)
                                               ),
                            'triglycerides', JSON_OBJECT(
                                    'value', ROUND(50 + RAND() * 300, 0),
                                    'unit', 'mg/dL',
                                    'reference_range', JSON_OBJECT('min', 0, 'max', 150)
                                             )
                               ),
                    'interpretation',
                    IF(ROUND(150 + RAND() * 100, 0) > 200, 'Elevated cholesterol levels - Dietary changes recommended',
                       'Normal lipid profile'),
                    'comments', 'Patient advised on lifestyle modifications.'
            )

        -- Liver Function Test results
        WHEN 3 THEN
            JSON_OBJECT(
                    'test_date', DATE_FORMAT(DATE_ADD('2025-06-08', INTERVAL -FLOOR(RAND() * 5) DAY), '%Y-%m-%d'),
                    'test_name', 'Liver Function Test',
                    'results', JSON_OBJECT(
                            'ALT', JSON_OBJECT(
                            'value', ROUND(7 + RAND() * 60, 0),
                            'unit', 'U/L',
                            'reference_range', JSON_OBJECT('min', 7, 'max', 55)
                                   ),
                            'AST', JSON_OBJECT(
                                    'value', ROUND(8 + RAND() * 40, 0),
                                    'unit', 'U/L',
                                    'reference_range', JSON_OBJECT('min', 8, 'max', 48)
                                   ),
                            'ALP', JSON_OBJECT(
                                    'value', ROUND(40 + RAND() * 90, 0),
                                    'unit', 'U/L',
                                    'reference_range', JSON_OBJECT('min', 45, 'max', 115)
                                   ),
                            'Total Bilirubin', JSON_OBJECT(
                                    'value', ROUND(0.1 + RAND() * 1.0, 1),
                                    'unit', 'mg/dL',
                                    'reference_range', JSON_OBJECT('min', 0.1, 'max', 1.2)
                                               )
                               ),
                    'interpretation', 'Liver enzymes within normal ranges.',
                    'comments', 'No signs of hepatic dysfunction.'
            )

        -- Thyroid Function Test results
        ELSE
            JSON_OBJECT(
                    'test_date', DATE_FORMAT(DATE_ADD('2025-06-08', INTERVAL -FLOOR(RAND() * 5) DAY), '%Y-%m-%d'),
                    'test_name', 'Thyroid Function Test',
                    'results', JSON_OBJECT(
                            'TSH', JSON_OBJECT(
                            'value', ROUND(0.4 + RAND() * 4.0, 2),
                            'unit', 'mIU/L',
                            'reference_range', JSON_OBJECT('min', 0.4, 'max', 4.0)
                                   ),
                            'Free T4', JSON_OBJECT(
                                    'value', ROUND(0.8 + RAND() * 1.2, 1),
                                    'unit', 'ng/dL',
                                    'reference_range', JSON_OBJECT('min', 0.8, 'max', 1.8)
                                       ),
                            'Free T3', JSON_OBJECT(
                                    'value', ROUND(2.3 + RAND() * 2.1, 1),
                                    'unit', 'pg/mL',
                                    'reference_range', JSON_OBJECT('min', 2.3, 'max', 4.2)
                                       )
                               ),
                    'interpretation', 'Thyroid function within normal parameters.',
                    'comments', 'No evidence of hyper- or hypothyroidism.'
            )
        END

FROM test_request_expanded tre
WHERE NOT EXISTS (
    -- Skip if we've already created the maximum number of items for this request
    SELECT 1
    FROM test_request_items
    WHERE test_request_id = tre.test_request_id
    GROUP BY test_request_id
    HAVING COUNT(*) >= 3)
  AND EXISTS (
    -- Only proceed if there are available tests not yet used for this request
    SELECT 1
    FROM available_tests at
    WHERE at.test_id NOT IN (SELECT test_id
                             FROM test_request_items
                             WHERE test_request_id = tre.test_request_id));

-- Generate sample data for tests and related table
-- First, populate the tests table by finding all products of type TEST

-- Create test requests for appropriate appointments
-- We'll create requests for COMPLETED appointments and some CONFIRMED ones


-- Generate sample data for medicines table
INSERT INTO medicines (medicine_id)
SELECT product_id
FROM products
WHERE product_type = 'MEDICINE'
  AND product_status = 'ACTIVE';

-- Generate sample data for medicine_ingredients table
-- Tạo dữ liệu thành phần thuốc chi tiết, thực tế
-- Modified version without window functions
INSERT INTO medicine_ingredients (medicine_id,
                                  chemical_name,
                                  generic_name,
                                  description,
                                  therapeutic_class,
                                  atc_code,
                                  strength)
WITH medicine_list AS (SELECT medicine_id
                       FROM medicines),
     medicine_with_count AS (
         -- Generate 2-4 rows per medicine
         SELECT medicine_id,
                1 + MOD(medicine_id, 25) as ingredient_type,
                1 + FLOOR(RAND() * 4)    as ingredient_count
         FROM medicine_list),
     medicine_expanded AS (SELECT m.medicine_id,
                                  m.ingredient_type,
                                  1 as ingredient_num
                           FROM medicine_with_count m
                           UNION ALL
                           SELECT m.medicine_id,
                                  m.ingredient_type + 1,
                                  2 as ingredient_num
                           FROM medicine_with_count m
                           WHERE m.ingredient_count >= 2
                           UNION ALL
                           SELECT m.medicine_id,
                                  m.ingredient_type + 2,
                                  3 as ingredient_num
                           FROM medicine_with_count m
                           WHERE m.ingredient_count >= 3
                           UNION ALL
                           SELECT m.medicine_id,
                                  m.ingredient_type + 3,
                                  4 as ingredient_num
                           FROM medicine_with_count m
                           WHERE m.ingredient_count = 4)
SELECT me.medicine_id,

       -- chemical_name: Chemical name of the medicine component
       CASE MOD(me.ingredient_type, 25)
           WHEN 0 THEN 'Acetylsalicylic Acid'
           WHEN 1 THEN 'Paracetamol (N-acetyl-p-aminophenol)'
           WHEN 2 THEN 'Ibuprofen ((RS)-2-(4-(2-methylpropyl)phenyl)propanoic acid)'
           WHEN 3
               THEN 'Atorvastatin ((3R,5R)-7-[2-(4-fluorophenyl)-3-phenyl-4-(phenylcarbamoyl)-5-propan-2-ylpyrrol-1-yl]-3,5-dihydroxyheptanoic acid)'
           WHEN 4 THEN 'Metformin (3-(diaminomethylidene)-1,1-dimethylguanidine)'
           WHEN 5
               THEN 'Amlodipine (3-O-ethyl 5-O-methyl 2-(2-aminoethoxymethyl)-4-(2-chlorophenyl)-6-methyl-1,4-dihydropyridine-3,5-dicarboxylate)'
           WHEN 6
               THEN 'Losartan (2-butyl-4-chloro-1-{[2′-(1H-tetrazol-5-yl)biphenyl-4-yl]methyl}-1H-imidazole-5-methanol)'
           WHEN 7 THEN 'Cetirizine (2-[2-[4-[(4-chlorophenyl)phenylmethyl]piperazin-1-yl]ethoxy]acetic acid)'
           WHEN 8 THEN 'Omeprazole (5-methoxy-2-[(4-methoxy-3,5-dimethylpyridin-2-yl)methylsulfinyl]-1H-benzimidazole)'
           WHEN 9
               THEN 'Amoxicillin ((2S,5R,6R)-6-[[(2R)-2-amino-2-(4-hydroxyphenyl)acetyl]amino]-3,3-dimethyl-7-oxo-4-thia-1-azabicyclo[3.2.0]heptane-2-carboxylic acid)'
           WHEN 10 THEN 'Levothyroxine (L-3,5,3′,5′-tetraiodothyronine)'
           WHEN 11 THEN 'Fluoxetine (N-methyl-3-phenyl-3-[4-(trifluoromethyl)phenoxy]propan-1-amine)'
           WHEN 12
               THEN 'Simvastatin ((1S,3R,7S,8S,8aR)-8-{2-[(2R,4R)-4-hydroxy-6-oxooxan-2-yl]ethyl}-3,7-dimethyl-1,2,3,7,8,8a-hexahydronaphthalen-1-yl 2,2-dimethylbutanoate)'
           WHEN 13
               THEN 'Ciprofloxacin (1-cyclopropyl-6-fluoro-4-oxo-7-(piperazin-1-yl)-1,4-dihydroquinoline-3-carboxylic acid)'
           WHEN 14
               THEN 'Montelukast ((R,E)-2-(1-((1-(3-(2-(7-chloroquinolin-2-yl)vinyl)phenyl)-3-(2-(2-hydroxypropan-2-yl)phenyl)propylthio)methyl)cyclopropyl)acetic acid)'
           WHEN 15
               THEN 'Fluticasone ((6α,11β,16α,17α)-6,9-difluoro-17-[(fluoromethyl)thio]-11-hydroxy-16-methyl-3-oxoandrosta-1,4-dien-17-yl propanoate)'
           WHEN 16 THEN 'Diphenhydramine (2-(diphenylmethoxy)-N,N-dimethylethanamine)'
           WHEN 17
               THEN 'Lisinopril ((2S)-1-[(2S)-6-amino-2-[[(1S)-1-carboxy-3-phenylpropyl]amino]hexanoyl]pyrrolidine-2-carboxylic acid)'
           WHEN 18 THEN 'Metoprolol (1-[4-(2-methoxyethyl)phenoxy]-3-(propan-2-ylamino)propan-2-ol)'
           WHEN 19
               THEN 'Clopidogrel ((+)-(S)-methyl 2-(2-chlorophenyl)-2-(6,7-dihydrothieno[3,2-c]pyridin-5(4H)-yl)acetate)'
           WHEN 20 THEN 'Warfarin (4-hydroxy-3-(3-oxo-1-phenylbutyl)-2H-chromen-2-one)'
           WHEN 21
               THEN 'Pantoprazole (6-(difluoromethoxy)-2-[(3,4-dimethoxypyridin-2-yl)methylsulfinyl]-1H-benzimidazole)'
           WHEN 22 THEN 'Albuterol (4-[2-(tert-butylamino)-1-hydroxyethyl]-2-(hydroxymethyl)phenol)'
           WHEN 23
               THEN 'Rosuvastatin ((3R,5S,6E)-7-[4-(4-fluorophenyl)-2-[methyl(methylsulfonyl)amino]-6-(propan-2-yl)pyrimidin-5-yl]-3,5-dihydroxyhept-6-enoic acid)'
           WHEN 24
               THEN 'Fexofenadine (2-[4-[1-hydroxy-4-[4-(hydroxydiphenylmethyl)piperidin-1-yl]butyl]phenyl]-2-methylpropanoic acid)'
           END,

       -- generic_name: Common name used
       CASE MOD(me.ingredient_type, 25)
           WHEN 0 THEN 'Aspirin'
           WHEN 1 THEN 'Paracetamol'
           WHEN 2 THEN 'Ibuprofen'
           WHEN 3 THEN 'Atorvastatin'
           WHEN 4 THEN 'Metformin'
           WHEN 5 THEN 'Amlodipine'
           WHEN 6 THEN 'Losartan'
           WHEN 7 THEN 'Cetirizine'
           WHEN 8 THEN 'Omeprazole'
           WHEN 9 THEN 'Amoxicillin'
           WHEN 10 THEN 'Levothyroxine'
           WHEN 11 THEN 'Fluoxetine'
           WHEN 12 THEN 'Simvastatin'
           WHEN 13 THEN 'Ciprofloxacin'
           WHEN 14 THEN 'Montelukast'
           WHEN 15 THEN 'Fluticasone'
           WHEN 16 THEN 'Diphenhydramine'
           WHEN 17 THEN 'Lisinopril'
           WHEN 18 THEN 'Metoprolol'
           WHEN 19 THEN 'Clopidogrel'
           WHEN 20 THEN 'Warfarin'
           WHEN 21 THEN 'Pantoprazole'
           WHEN 22 THEN 'Albuterol'
           WHEN 23 THEN 'Rosuvastatin'
           WHEN 24 THEN 'Fexofenadine'
           END,

       -- description: Description of the medicine's action
       CASE MOD(me.ingredient_type, 25)
           WHEN 0
               THEN 'Pain, fever, and inflammation reducer that inhibits cyclooxygenase (COX) enzymes, reducing prostaglandin synthesis.'
           WHEN 1
               THEN 'Pain and fever reducer that acts on temperature control centers in the hypothalamus, inhibiting prostaglandin synthesis.'
           WHEN 2
               THEN 'Non-steroidal anti-inflammatory drug (NSAID) that inhibits COX-1 and COX-2, reducing prostaglandin and thromboxane synthesis.'
           WHEN 3
               THEN 'Lipid-lowering medication that inhibits HMG-CoA reductase, decreasing endogenous cholesterol production.'
           WHEN 4
               THEN 'Type 2 diabetes medication that reduces hepatic glucose production and increases peripheral insulin sensitivity.'
           WHEN 5
               THEN 'Calcium channel blocker that inhibits calcium influx into cardiac and smooth muscle cells, causing vasodilation and lowering blood pressure.'
           WHEN 6 THEN 'Angiotensin II receptor antagonist that blocks the vasoconstricting effects of angiotensin II.'
           WHEN 7
               THEN 'Second-generation H1 antihistamine for treating allergy symptoms such as itching, sneezing, rhinorrhea, and angioedema.'
           WHEN 8
               THEN 'Proton pump inhibitor (PPI) that decreases gastric acid secretion by inhibiting H+/K+ ATPase pump.'
           WHEN 9
               THEN 'Beta-lactam antibiotic in the penicillin group that inhibits bacterial cell wall synthesis by binding to penicillin-binding proteins (PBPs).'
           WHEN 10
               THEN 'Synthetic thyroid hormone replacement for hypothyroidism that plays a crucial role in metabolism.'
           WHEN 11
               THEN 'Selective serotonin reuptake inhibitor (SSRI) that increases serotonin concentration at neuronal synaptic clefts.'
           WHEN 12 THEN 'Lipid-lowering medication that inhibits HMG-CoA reductase, decreasing cholesterol synthesis.'
           WHEN 13 THEN 'Fluoroquinolone antibiotic that inhibits bacterial DNA gyrase and topoisomerase IV enzymes.'
           WHEN 14
               THEN 'Selective leukotriene receptor antagonist that inhibits the action of leukotriene D4 on CysLT1 receptor.'
           WHEN 15
               THEN 'Inhaled corticosteroid with anti-inflammatory effects, reducing inflammatory response in the respiratory tract.'
           WHEN 16
               THEN 'First-generation H1 antihistamine with sedative effects, used for allergies, insomnia, and nausea.'
           WHEN 17
               THEN 'Angiotensin-converting enzyme (ACE) inhibitor that prevents conversion of angiotensin I to angiotensin II.'
           WHEN 18 THEN 'Selective beta-1 blocker that decreases heart rate, cardiac contractility, and blood pressure.'
           WHEN 19 THEN 'Antiplatelet agent that inhibits platelet aggregation by inhibiting P2Y12 receptor.'
           WHEN 20
               THEN 'Anticoagulant that inhibits vitamin K epoxide reductase, decreasing synthesis of clotting factors II, VII, IX, and X.'
           WHEN 21
               THEN 'Proton pump inhibitor (PPI) that decreases gastric acid secretion by inhibiting H+/K+ ATPase pump.'
           WHEN 22
               THEN 'Selective beta-2 agonist that causes bronchodilation, used in the treatment of asthma and COPD.'
           WHEN 23 THEN 'Lipid-lowering medication that inhibits HMG-CoA reductase, decreasing cholesterol synthesis.'
           WHEN 24 THEN 'Third-generation H1 antihistamine for treating allergy symptoms without causing drowsiness.'
           END,

       -- therapeutic_class: Treatment classification
       CASE MOD(me.ingredient_type, 25)
           WHEN 0 THEN 'NSAID (Non-steroidal anti-inflammatory drug)'
           WHEN 1 THEN 'Analgesic/Antipyretic'
           WHEN 2 THEN 'NSAID (Non-steroidal anti-inflammatory drug)'
           WHEN 3 THEN 'HMG-CoA Reductase Inhibitor (Statin)'
           WHEN 4 THEN 'Biguanide Antidiabetic'
           WHEN 5 THEN 'Calcium Channel Blocker'
           WHEN 6 THEN 'Angiotensin II Receptor Blocker (ARB)'
           WHEN 7 THEN 'Second Generation Antihistamine'
           WHEN 8 THEN 'Proton Pump Inhibitor'
           WHEN 9 THEN 'Penicillin Antibiotic'
           WHEN 10 THEN 'Thyroid Hormone Replacement'
           WHEN 11 THEN 'Selective Serotonin Reuptake Inhibitor (SSRI)'
           WHEN 12 THEN 'HMG-CoA Reductase Inhibitor (Statin)'
           WHEN 13 THEN 'Fluoroquinolone Antibiotic'
           WHEN 14 THEN 'Leukotriene Receptor Antagonist'
           WHEN 15 THEN 'Inhaled Corticosteroid'
           WHEN 16 THEN 'First Generation Antihistamine'
           WHEN 17 THEN 'Angiotensin-Converting Enzyme (ACE) Inhibitor'
           WHEN 18 THEN 'Beta-1 Selective Blocker'
           WHEN 19 THEN 'P2Y12 Platelet Inhibitor'
           WHEN 20 THEN 'Vitamin K Antagonist Anticoagulant'
           WHEN 21 THEN 'Proton Pump Inhibitor'
           WHEN 22 THEN 'Short-Acting Beta-2 Agonist'
           WHEN 23 THEN 'HMG-CoA Reductase Inhibitor (Statin)'
           WHEN 24 THEN 'Third Generation Antihistamine'
           END,

       -- atc_code: ATC classification code
       CASE MOD(me.ingredient_type, 25)
           WHEN 0 THEN 'N02BA01'
           WHEN 1 THEN 'N02BE01'
           WHEN 2 THEN 'M01AE01'
           WHEN 3 THEN 'C10AA05'
           WHEN 4 THEN 'A10BA02'
           WHEN 5 THEN 'C08CA01'
           WHEN 6 THEN 'C09CA01'
           WHEN 7 THEN 'R06AE07'
           WHEN 8 THEN 'A02BC01'
           WHEN 9 THEN 'J01CA04'
           WHEN 10 THEN 'H03AA01'
           WHEN 11 THEN 'N06AB03'
           WHEN 12 THEN 'C10AA01'
           WHEN 13 THEN 'J01MA02'
           WHEN 14 THEN 'R03DC03'
           WHEN 15 THEN 'R03BA05'
           WHEN 16 THEN 'R06AA02'
           WHEN 17 THEN 'C09AA03'
           WHEN 18 THEN 'C07AB02'
           WHEN 19 THEN 'B01AC04'
           WHEN 20 THEN 'B01AA03'
           WHEN 21 THEN 'A02BC02'
           WHEN 22 THEN 'R03AC02'
           WHEN 23 THEN 'C10AA07'
           WHEN 24 THEN 'R06AX26'
           END,

       -- strength: Concentration/strength
       CASE MOD(me.ingredient_type, 25)
           WHEN 0 THEN '81mg, 325mg, 500mg'
           WHEN 1 THEN '500mg, 650mg'
           WHEN 2 THEN '200mg, 400mg, 600mg'
           WHEN 3 THEN '10mg, 20mg, 40mg, 80mg'
           WHEN 4 THEN '500mg, 850mg, 1000mg'
           WHEN 5 THEN '2.5mg, 5mg, 10mg'
           WHEN 6 THEN '25mg, 50mg, 100mg'
           WHEN 7 THEN '5mg, 10mg'
           WHEN 8 THEN '20mg, 40mg'
           WHEN 9 THEN '250mg, 500mg'
           WHEN 10 THEN '25mcg, 50mcg, 75mcg, 88mcg, 100mcg'
           WHEN 11 THEN '10mg, 20mg, 40mg'
           WHEN 12 THEN '5mg, 10mg, 20mg, 40mg'
           WHEN 13 THEN '250mg, 500mg, 750mg'
           WHEN 14 THEN '4mg, 5mg, 10mg'
           WHEN 15 THEN '50mcg, 100mcg, 250mcg'
           WHEN 16 THEN '25mg, 50mg'
           WHEN 17 THEN '5mg, 10mg, 20mg'
           WHEN 18 THEN '25mg, 50mg, 100mg'
           WHEN 19 THEN '75mg'
           WHEN 20 THEN '1mg, 2mg, 2.5mg, 5mg, 7.5mg, 10mg'
           WHEN 21 THEN '20mg, 40mg'
           WHEN 22 THEN '2mg, 4mg'
           WHEN 23 THEN '5mg, 10mg, 20mg, 40mg'
           WHEN 24 THEN '30mg, 60mg, 120mg, 180mg'
           END

FROM medicine_expanded me
ORDER BY me.medicine_id, me.ingredient_num;

-- Generate sample data for ingredient_interactions table

-- Create interactions between medicine ingredients
INSERT INTO ingredient_interactions (medicine_ingredient_id_1,
                                     medicine_ingredient_id_2,
                                     severity,
                                     description)
SELECT a.medicine_ingredient_id as id1,
       b.medicine_ingredient_id as id2,

       -- severity: Determine interaction severity
       CASE
           WHEN (a.therapeutic_class LIKE '%Anticoagulant%' AND b.therapeutic_class LIKE '%NSAID%')
               OR (a.therapeutic_class LIKE '%NSAID%' AND b.therapeutic_class LIKE '%Anticoagulant%') THEN 'MAJOR'
           WHEN (a.therapeutic_class LIKE '%Statin%' AND b.therapeutic_class LIKE '%Antibiotic%')
               OR (a.therapeutic_class LIKE '%Antibiotic%' AND b.therapeutic_class LIKE '%Statin%') THEN 'MODERATE'
           WHEN RAND() < 0.3 THEN 'MAJOR'
           WHEN RAND() < 0.6 THEN 'MODERATE'
           ELSE 'MINOR'
           END                  as severity,

       -- description: Detailed interaction information
       CASE
           -- Specific interactions based on drug classes
           WHEN (a.therapeutic_class LIKE '%Anticoagulant%' AND b.therapeutic_class LIKE '%NSAID%')
               OR (a.therapeutic_class LIKE '%NSAID%' AND b.therapeutic_class LIKE '%Anticoagulant%') THEN
               CONCAT('Increased bleeding risk when combining ', a.generic_name, ' and ', b.generic_name,
                      '. Monitor INR and bleeding status closely.')
           WHEN (a.therapeutic_class LIKE '%Statin%' AND b.therapeutic_class LIKE '%Antibiotic%')
               OR (a.therapeutic_class LIKE '%Antibiotic%' AND b.therapeutic_class LIKE '%Statin%') THEN
               CONCAT('Increased risk of myopathy when using ', a.generic_name, ' with ', b.generic_name,
                      '. Consider dose reduction or temporary statin discontinuation.')
           WHEN (a.therapeutic_class LIKE '%SSRI%' AND b.therapeutic_class LIKE '%NSAID%')
               OR (a.therapeutic_class LIKE '%NSAID%' AND b.therapeutic_class LIKE '%SSRI%') THEN
               CONCAT('Increased risk of gastrointestinal bleeding when using ', a.generic_name, ' with ',
                      b.generic_name, '. Monitor for bleeding signs.')
           WHEN (a.therapeutic_class LIKE '%ACE%' AND b.therapeutic_class LIKE '%NSAID%')
               OR (a.therapeutic_class LIKE '%NSAID%' AND b.therapeutic_class LIKE '%ACE%') THEN
               CONCAT('Reduced antihypertensive effects of ', a.generic_name, ' when combined with ', b.generic_name,
                      '. Monitor blood pressure closely.')
           WHEN (a.therapeutic_class LIKE '%Beta%' AND b.therapeutic_class LIKE '%Calcium Channel%')
               OR (a.therapeutic_class LIKE '%Calcium Channel%' AND b.therapeutic_class LIKE '%Beta%') THEN
               CONCAT('Increased risk of hypotension and bradycardia when combining ', a.generic_name, ' with ',
                      b.generic_name, '. Monitor blood pressure and heart rate.')
           -- Generic interaction for other cases
           ELSE
               CONCAT('Interaction between ', a.generic_name, ' and ', b.generic_name,
                      ' may lead to altered therapeutic effects or increased side effects. Consider dosage adjustment or medication substitution.')
           END                  as description

FROM medicine_ingredients a
         JOIN medicine_ingredients b ON a.medicine_ingredient_id < b.medicine_ingredient_id -- Avoid duplicates
WHERE a.medicine_ingredient_id IS NOT NULL
  AND b.medicine_ingredient_id IS NOT NULL -- Ensure non-null values
  AND RAND() < 0.05;
-- Only create interactions for about 15% of possible pairs

-- Generate sample data for ingredient_requests table
INSERT INTO ingredient_requests (appointment_id,
                                 pharmacist_id,
                                 reason)
SELECT a.appointment_id,

-- pharmacist_id: Pharmacist in charge
       (SELECT pharmacist_id FROM pharmacists ORDER BY RAND() LIMIT 1),

-- reason: Reason for ingredient request
       CASE FLOOR(RAND() * 15)
           WHEN 0 THEN 'Treatment of upper respiratory tract infection, with fever and sore throat'
           WHEN 1 THEN 'Control of chronic pain due to arthritis, requiring pain relief and anti-inflammation'
           WHEN 2 THEN 'Treatment of high blood pressure with dyslipidemia'
           WHEN 3 THEN 'Control of seasonal allergy symptoms, with nasal congestion and itchy eyes'
           WHEN 4 THEN 'Treatment of type 2 diabetes, requiring blood sugar stabilization'
           WHEN 5 THEN 'Treatment of gastritis and gastroesophageal reflux'
           WHEN 6 THEN 'Control asthma symptoms and prevent acute asthma attacks'
           WHEN 7 THEN 'Treatment of anxiety and depression disorders'
           WHEN 8 THEN 'Treatment of urinary tract infections, with frequent and painful urination'
           WHEN 9 THEN 'Control of thyroid disorders, requiring hormone adjustment'
           WHEN 10 THEN 'Prevention of post-operative venous thromboembolism'
           WHEN 11 THEN 'Treatment of chronic sinusitis, with nasal congestion and headaches'
           WHEN 12 THEN 'Control of cardiovascular disease and prevent myocardial infarction'
           WHEN 13 THEN 'Treatment of chronic sleep disorders'
           ELSE 'Treatment of chronic obstructive pulmonary disease (COPD) and prevention of acute attacks'
           END

FROM appointments a
WHERE a.appointment_status = 'COMPLETED'
-- Only generate medication requests for approximately 85% of completed appointments
  AND RAND() < 0.85
ORDER BY a.appointment_id;

-- Generate sample data for ingredient_request_items table
-- Tạo liên kết giữa yêu cầu và các thành phần cụ thể
-- Create ingredient request items with non-null values
-- Bulletproof solution for ingredient_request_items
-- Tạo dữ liệu cho bảng ingredient_request_items
INSERT INTO ingredient_request_items (ingredient_request_id, medicine_ingredient_id)
WITH ingredient_assignments AS (
    SELECT
        ir.ingredient_request_id,
        mi.medicine_ingredient_id,
        ROW_NUMBER() OVER (PARTITION BY ir.ingredient_request_id ORDER BY RAND()) AS rn,
        1 + MOD(ir.ingredient_request_id, 5) AS max_ingredients -- Số lượng thành phần (1-5)
    FROM
        ingredient_requests ir
            CROSS JOIN medicine_ingredients mi
    WHERE
        NOT EXISTS (
            SELECT 1 FROM ingredient_request_items iri
            WHERE iri.ingredient_request_id = ir.ingredient_request_id
              AND iri.medicine_ingredient_id = mi.medicine_ingredient_id
        )
)
SELECT
    ingredient_request_id,
    medicine_ingredient_id
FROM
    ingredient_assignments
WHERE
    rn <= max_ingredients;

-- Generate sample data for prescriptions table
-- Tạo đơn thuốc cho mỗi yêu cầu thành phần thuốc
INSERT INTO prescriptions (ingredient_request_id)
SELECT ingredient_request_id
FROM ingredient_requests
ORDER BY ingredient_request_id;

-- Generate sample data for prescription_items table
-- 1) Sinh & chèn vào prescription_items chỉ với một biến duy nhất (@review_count không còn dùng)
INSERT IGNORE INTO prescription_items (prescription_id,
                                       medicine_id,
                                       dosage,
                                       quantity,
                                       duration_days)
WITH
    -- A. Các cặp đã tồn tại để tránh trùng
    existing_pairs AS (SELECT prescription_id, medicine_id
                       FROM prescription_items),
    -- B. Danh sách tất cả medicine với row number
    all_medicines AS (SELECT medicine_id,
                             ROW_NUMBER() OVER (ORDER BY medicine_id) AS med_row,
                             (SELECT COUNT(*) FROM medicines)         AS total_meds
                      FROM medicines),
    -- C. Danh sách prescription với 1–3 slot
    prescription_slots AS (SELECT p.prescription_id, 1 AS slot_num
                           FROM prescriptions p
                           UNION ALL
                           SELECT p.prescription_id, 2
                           FROM prescriptions p
                           WHERE MOD(p.prescription_id, 3) <> 0
                           UNION ALL
                           SELECT p.prescription_id, 3
                           FROM prescriptions p
                           WHERE MOD(p.prescription_id, 3) = 2),
    -- D. Tính target_row không dùng biến ngoài
    slot_assign AS (SELECT ps.prescription_id,
                           ps.slot_num,
                           1 + MOD(
                                   ps.prescription_id * ps.slot_num,
                                   am.total_meds
                               ) AS target_row
                    FROM prescription_slots ps
                             CROSS JOIN (SELECT DISTINCT total_meds FROM all_medicines) am),
    -- E. Ghép slot với medicine
    final_assign AS (SELECT sa.prescription_id,
                            sa.slot_num,
                            am.medicine_id
                     FROM slot_assign sa
                              JOIN all_medicines am ON am.med_row = sa.target_row)
SELECT fa.prescription_id,
       fa.medicine_id,
       -- Dosage instructions
       CASE MOD(fa.medicine_id + fa.slot_num, 20)
           WHEN 0 THEN 'Take 1 tablet daily in the morning, 30 minutes before breakfast.'
           WHEN 1 THEN 'Take 1 tablet twice daily (morning and evening), after meals.'
           WHEN 2 THEN 'Take 1 tablet three times daily (morning, noon, evening), with meals.'
           WHEN 3 THEN 'Take 2 tablets every morning, on empty stomach.'
           WHEN 4 THEN 'Take 1 tablet every night before bedtime.'
           WHEN 5 THEN 'Dissolve 1 packet in water, take after breakfast.'
           WHEN 6 THEN 'Apply a thin layer to affected area, twice daily (morning and evening).'
           WHEN 7 THEN 'Spray 2 puffs into each nostril, once daily.'
           WHEN 8 THEN 'Place 1 drop in each eye, three times daily.'
           WHEN 9 THEN 'Take 1 tablet when pain occurs; no more than 4 daily; space at least 4 hours.'
           WHEN 10 THEN 'Take 1 tablet daily at the same time. Swallow whole, do not chew.'
           WHEN 11 THEN 'Day 1: two tablets; from day 2: one tablet daily in the morning.'
           WHEN 12 THEN 'Take 1 tablet every 12 hours. Drink plenty of water.'
           WHEN 13 THEN 'Inject 10 units subcutaneously every morning 30 min before breakfast.'
           WHEN 14 THEN 'Insert 1 vaginal tablet every night before bedtime.'
           WHEN 15 THEN 'Inhale 2 puffs when having difficulty breathing; no more than 8 puffs daily.'
           WHEN 16 THEN 'Take 2 tablets initially, then 1 after each loose stool; max 8 daily.'
           WHEN 17 THEN 'Dissolve 1 lozenge every 2–3 hours for sore throat; max 8 daily.'
           WHEN 18 THEN 'Take 1 tablet (500 mg) every 4–6 hours when fever > 100.4°F; max 4 daily.'
           ELSE 'Take ½ tablet daily for first week; increase to 1 tablet daily thereafter.'
           END AS dosage,
       -- Quantity options
       CASE MOD(fa.prescription_id + fa.slot_num, 6)
           WHEN 0 THEN 10
           WHEN 1 THEN 15
           WHEN 2 THEN 20
           WHEN 3 THEN 30
           WHEN 4 THEN 60
           ELSE 90
           END AS quantity,
       -- Duration options
       CASE MOD(fa.prescription_id * fa.slot_num, 7)
           WHEN 0 THEN 3
           WHEN 1 THEN 5
           WHEN 2 THEN 7
           WHEN 3 THEN 10
           WHEN 4 THEN 14
           WHEN 5 THEN 30
           ELSE 90
           END AS duration_days
FROM final_assign fa
WHERE NOT EXISTS (SELECT 1
                  FROM existing_pairs ep
                  WHERE ep.prescription_id = fa.prescription_id
                    AND ep.medicine_id = fa.medicine_id);

-- Generate sample data for pricing_plans table
INSERT INTO pricing_plans (pricing_plan_id, plan_type)
SELECT product_id,
       CASE
           WHEN MOD(product_id, 4) = 0 THEN 'MONTHLY'
           WHEN MOD(product_id, 4) = 1 THEN 'YEARLY'
           WHEN MOD(product_id, 4) = 2 THEN 'ONETIME'
           ELSE 'CUSTOM'
           END as plan_type
FROM products
WHERE product_type = 'PRICING_PLAN'
  AND product_status = 'ACTIVE';

-- Generate sample data for pricing_plan_features table
-- Insert features for each pricing plan
INSERT INTO pricing_plan_features (pricing_plan_id, name, description)
WITH plan_list AS (SELECT pricing_plan_id, plan_type
                   FROM pricing_plans)
-- Create multiple features for each plan
SELECT p.pricing_plan_id,

       -- Feature names based on plan type
       CASE
           -- Monthly plan features
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 1 THEN 'Priority Appointments'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 2 THEN 'Video Consultations'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 3 THEN '10% Medicine Discount'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 4 THEN '24/7 Chat Support'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 5 THEN 'Monthly Health Check'

           -- Yearly plan features (more comprehensive)
           WHEN p.plan_type = 'YEARLY' AND feature_num = 1 THEN 'Priority Appointments'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 2 THEN 'Unlimited Video Consultations'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 3 THEN '20% Medicine Discount'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 4 THEN '24/7 Phone & Chat Support'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 5 THEN 'Quarterly Health Check'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 6 THEN 'Family Doctor Assignment'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 7 THEN 'Free Health Screenings'

           -- One-time plan features
           WHEN p.plan_type = 'ONETIME' AND feature_num = 1 THEN 'Complete Health Assessment'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 2 THEN 'Lab Tests Package'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 3 THEN 'Specialist Consultation'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 4 THEN 'Treatment Plan'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 5 THEN 'Follow-up Visit'

           -- Custom plan features
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 1 THEN 'Personalized Care Plan'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 2 THEN 'Dedicated Medical Concierge'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 3 THEN 'Home Visits'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 4 THEN 'Unlimited Specialist Access'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 5 THEN 'Emergency Transport'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 6 THEN 'Premium Hospital Rooms'
           ELSE 'Additional Benefits'
           END as name,

       -- Detailed descriptions of each feature
       CASE
           -- Monthly plan feature descriptions
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 1
               THEN 'Schedule appointments within 24 hours, bypassing standard waiting times'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 2
               THEN 'Up to 5 video consultations per month with general practitioners'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 3
               THEN '10% discount on all prescription medications purchased through our pharmacy'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 4
               THEN 'Round-the-clock access to medical professionals via our secure messaging platform'
           WHEN p.plan_type = 'MONTHLY' AND feature_num = 5
               THEN 'Basic health assessment once per month including vital signs and general health screening'

           -- Yearly plan feature descriptions
           WHEN p.plan_type = 'YEARLY' AND feature_num = 1
               THEN 'Schedule appointments within 24 hours, bypassing standard waiting times'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 2
               THEN 'Unlimited video consultations with general practitioners and up to 12 specialist consultations per year'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 3
               THEN '20% discount on all prescription medications and over-the-counter products purchased through our pharmacy'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 4
               THEN 'Priority 24/7 access to medical professionals via phone and our secure messaging platform'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 5
               THEN 'Comprehensive health assessment every three months including vital signs, blood work, and health risk evaluation'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 6
               THEN 'Dedicated primary care physician who manages your health needs year-round'
           WHEN p.plan_type = 'YEARLY' AND feature_num = 7
               THEN 'Annual cancer screenings, cardiovascular assessments, and other preventive health measures at no additional cost'

           -- One-time plan feature descriptions
           WHEN p.plan_type = 'ONETIME' AND feature_num = 1
               THEN 'Full body check-up including physical examination, medical history review, and health risk assessment'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 2
               THEN 'Comprehensive blood panel, urinalysis, and specialized tests based on age, gender, and risk factors'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 3
               THEN 'One-hour consultation with a specialist in your area of health concern'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 4
               THEN 'Personalized treatment recommendations based on assessment results and specialist input'
           WHEN p.plan_type = 'ONETIME' AND feature_num = 5
               THEN 'Follow-up appointment within 30 days to review progress and adjust recommendations'

           -- Custom plan feature descriptions
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 1
               THEN 'Fully customized healthcare plan designed around your specific health needs and goals'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 2
               THEN 'Personal healthcare coordinator to manage appointments, treatments, and answer questions'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 3
               THEN 'Medical professionals visit your home for consultations, treatments, and routine care'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 4
               THEN 'Direct access to specialists without referrals, including international experts if required'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 5
               THEN 'Priority medical transport services including helicopter evacuation in emergency situations'
           WHEN p.plan_type = 'CUSTOM' AND feature_num = 6
               THEN 'Access to private rooms with enhanced amenities during hospital stays'
           ELSE 'Additional benefits customized to your specific healthcare needs'
           END as description

FROM plan_list p
         CROSS JOIN (
    -- Generate feature numbers (monthly:5, yearly:7, onetime:5, custom:6)
    SELECT 1 AS feature_num
    UNION
    SELECT 2
    UNION
    SELECT 3
    UNION
    SELECT 4
    UNION
    SELECT 5
    UNION
    SELECT 6
    UNION
    SELECT 7) nums
WHERE (p.plan_type = 'MONTHLY' AND nums.feature_num <= 5)
   OR (p.plan_type = 'YEARLY' AND nums.feature_num <= 7)
   OR (p.plan_type = 'ONETIME' AND nums.feature_num <= 5)
   OR (p.plan_type = 'CUSTOM' AND nums.feature_num <= 6);

-- Generate sample data for pricing_plan_subscriptions table
-- Create patient subscriptions with realistic distribution
INSERT INTO pricing_plan_subscriptions (patient_id, pricing_plan_id)
WITH patient_list AS (SELECT patient_id
                      FROM patients
                      ORDER BY patient_id),
     plan_list AS (SELECT pricing_plan_id, plan_type
                   FROM pricing_plans),
-- Create a distribution of plans (not all patients need plans)
     patient_assignments AS (SELECT p.patient_id,
                                    -- Use a deterministic but seemingly random assignment
                                    CASE
                                        -- 40% of patients have no plan (returns NULL)
                                        WHEN RAND() < 0.4 THEN NULL
                                        -- The remaining 60% of patients are assigned to plans with different distributions
                                        WHEN RAND() < 0.5 THEN (SELECT pricing_plan_id
                                                                FROM plan_list
                                                                WHERE plan_type = 'MONTHLY'
                                                                ORDER BY RAND()
                                                                LIMIT 1)
                                        WHEN RAND() < 0.7 THEN (SELECT pricing_plan_id
                                                                FROM plan_list
                                                                WHERE plan_type = 'YEARLY'
                                                                ORDER BY RAND()
                                                                LIMIT 1)
                                        WHEN RAND() < 0.9 THEN (SELECT pricing_plan_id
                                                                FROM plan_list
                                                                WHERE plan_type = 'ONETIME'
                                                                ORDER BY RAND()
                                                                LIMIT 1)
                                        ELSE (SELECT pricing_plan_id
                                              FROM plan_list
                                              WHERE plan_type = 'CUSTOM'
                                              ORDER BY RAND()
                                              LIMIT 1)
                                        END AS assigned_plan_id
                             FROM patient_list p)
-- Only insert patients who have been assigned a plan (not NULL)
SELECT patient_id, assigned_plan_id
FROM patient_assignments
WHERE assigned_plan_id IS NOT NULL;

-- Generate sample data for medical_products table
INSERT INTO medical_products (medical_product_id)
SELECT product_id
FROM products
WHERE product_type = 'MEDICAL_PRODUCT'
  AND product_status = 'ACTIVE';

-- 1.2 Tạo bảng tạm (derived table) đánh số thứ tự
--    Dùng 1 lần ở dưới để join, tránh phải scan + sort nhiều lần.

-- 1) Tạo tmp_review_seq bằng sub-query + WHERE
DROP TEMPORARY TABLE IF EXISTS tmp_review_seq;
CREATE TEMPORARY TABLE tmp_review_seq AS
SELECT t.review_num
FROM (
         SELECT @rn := @rn + 1 AS review_num
         FROM (SELECT @rn := 0) init, information_schema.columns
     ) AS t
WHERE t.review_num <= @review_count;

-- 2) Tạo bảng tạm đánh số bệnh nhân
DROP TEMPORARY TABLE IF EXISTS tmp_patients;
CREATE TEMPORARY TABLE tmp_patients AS
SELECT patient_id,
       @pn := @pn + 1 AS rn
FROM (SELECT @pn := 0) init, patients
ORDER BY patient_id;

-- 3) Insert vào reviews
INSERT INTO reviews (review_type, patient_id, content, rating)
SELECT
    CASE
        WHEN MOD(s.review_num,5) < 3 THEN 'PRODUCT'
        ELSE 'STAFF'
        END AS review_type,
    p.patient_id,
    -- content
    CASE
        WHEN MOD(s.review_num,5)>=3 AND MOD(s.review_num,5)<5
            AND MOD(s.review_num,5) < 3 /* only PRODUCT high‑score */ THEN
            ELT(MOD(s.review_num,6)+1,
                'Excellent product, I feel much better after using it. Will buy again.',
                'This medication relieved symptoms quickly with no side effects. Very satisfied!',
                'Well packaged and fast delivery. The product works exactly as described.',
                'I’ve tried many products but this one is the best. Affordable for the quality.',
                'Fits my needs perfectly. Easy to use and highly effective within a week.',
                'This medicine helped me relieve pain effectively and lasted long. Reliable quality.'
            )
        WHEN MOD(s.review_num,5) < 3 THEN  /* PRODUCT low‑score */
            ELT(MOD(s.review_num,5)+1,
                'Product did not meet expectations. Used for two weeks with no improvement.',
                'Price is high compared to its performance. Unsure if I will repurchase.',
                'Poor packaging, some tablets arrived broken.',
                'Experienced mild side effects like drowsiness and dry mouth.',
                'Instructions were unclear. Packaging information needs improvement.'
            )
        WHEN MOD(s.review_num,5)>=3 THEN  /* STAFF high‑score */
            ELT(MOD(s.review_num,6)+1,
                'The pharmacist was very professional and knowledgeable, explaining usage in detail.',
                'Staff were friendly and attentive. They took time to fully advise me.',
                'Service was quick and efficient. No long waits.',
                'The pharmacist suggested a suitable alternative when my medication was out of stock.',
                'Staff were very caring and followed up on my condition after I used the medicine.',
                'I always trust the professional advice from the staff here. Very satisfied!'
            )
        ELSE  /* STAFF low‑score */
            ELT(MOD(s.review_num,4)+1,
                'Staff were not enthusiastic in advising, answers felt superficial.',
                'Had to wait too long for service even though the store was not busy.',
                'Pharmacist did not clearly explain possible side effects of the medicine.',
                'Service attitude was unfriendly, communication skills need improvement.'
            )
        END AS content,
    -- rating
    CASE
        WHEN MOD(s.review_num,10)=0 THEN 1
        WHEN MOD(s.review_num,10)=1 THEN 2
        WHEN MOD(s.review_num,10) BETWEEN 2 AND 3 THEN 3
        WHEN MOD(s.review_num,10) BETWEEN 4 AND 6 THEN 4
        ELSE 5
        END AS rating
FROM tmp_review_seq s
         JOIN tmp_patients p
              ON p.rn = ((s.review_num - 1) % (SELECT COUNT(*) FROM patients)) + 1
;

-- 4) Tạo bảng tạm đánh số products & staffs (chỉ cần làm một lần)
DROP TEMPORARY TABLE IF EXISTS tmp_products;
CREATE TEMPORARY TABLE tmp_products AS
SELECT product_id,
       @pp := @pp + 1 AS rn
FROM (SELECT @pp := 0) init, products
ORDER BY product_id;

DROP TEMPORARY TABLE IF EXISTS tmp_staffs;
CREATE TEMPORARY TABLE tmp_staffs AS
SELECT staff_id,
       @ss := @ss + 1 AS rn
FROM (SELECT @ss := 0) init, staffs
ORDER BY staff_id;

-- 5) Gán vào product_reviews
INSERT INTO product_reviews (product_review_id, product_id)
SELECT
    r.review_id,
    p.product_id
FROM reviews r
         JOIN tmp_products p
              ON p.rn = FLOOR(RAND(r.review_id) * @product_count) + 1
WHERE r.review_type = 'PRODUCT';

-- 6) Gán vào staff_reviews
INSERT INTO staff_reviews (staff_review_id, staff_id)
SELECT
    r.review_id,
    s.staff_id
FROM reviews r
         JOIN tmp_staffs s
              ON s.rn = FLOOR(RAND(r.review_id) * (SELECT COUNT(*) FROM staffs)) + 1
WHERE r.review_type = 'STAFF';

-- Generate sample data for suppliers and related tables
-- 1. Add suppliers
INSERT INTO suppliers (name, email, phone_number)
VALUES ('Dược Phẩm Traphaco', 'contact@traphaco.com.vn', '02437341092'),
       ('DHG Pharma', 'dhgpharma@dhgpharma.com.vn', '02923891433'),
       ('Imexpharm', 'info@imexpharm.com', '02773851941'),
       ('Dược Hậu Giang', 'info@dhgpharma.com.vn', '02923891433'),
       ('Domesco', 'domesco@domesco.com', '02773859370'),
       ('Pymepharco', 'cntt@pymepharco.com', '02573823228'),
       ('Mekophar', 'info@mekophar.com', '02838650258'),
       ('OPC Pharma', 'info@opcpharma.com', '02839650721'),
       ('Bidiphar', 'office@bidiphar.com.vn', '02563846500'),
       ('Vimedimex', 'vimedimex@vimedimex.com.vn', '02438615201'),
       ('Sanofi Vietnam', 'contact.vietnam@sanofi.com', '02838221691'),
       ('Novartis Vietnam', 'info.vietnam@novartis.com', '02839100755'),
       ('Pfizer Vietnam', 'pfizervn@pfizer.com', '02839390390'),
       ('GlaxoSmithKline Vietnam', 'gsk.vietnam@gsk.com', '02839100151'),
       ('Roche Vietnam', 'vietnam.info@roche.com', '02839390909');

-- 2. Add supplier transactions
INSERT INTO supplier_transactions (inventory_manager_id, supplier_id, total_amount, transaction_date)
WITH RECURSIVE number_sequence AS (SELECT 1 AS n
                                   UNION ALL
                                   SELECT n + 1
                                   FROM number_sequence
                                   WHERE n < @supplier_transaction_count)
SELECT (SELECT inventory_manager_id FROM inventory_managers ORDER BY RAND() LIMIT 1),
       (SELECT supplier_id FROM suppliers ORDER BY RAND() LIMIT 1),
       ROUND(1000 + RAND() * 9000, -3),
       DATE_SUB('2025-06-08', INTERVAL FLOOR(RAND() * 180) DAY)
FROM number_sequence;

-- 3. Add transaction items
INSERT INTO supplier_transaction_items
(supplier_transaction_id, product_id, quantity, unit_price, currency_unit, manufacture_date, expiration_date)
WITH transaction_products AS (SELECT st.supplier_transaction_id,
                                     st.total_amount,
                                     p.product_id,
                                     ROW_NUMBER() OVER (PARTITION BY st.supplier_transaction_id ORDER BY RAND()) AS item_num
                              FROM supplier_transactions st
                                       CROSS JOIN products p
                              WHERE RAND() < 0.2)
SELECT tp.supplier_transaction_id,
       tp.product_id,
       FLOOR(10 + RAND() * 490),
       ROUND(5 + RAND() * 45, -3),
       'VND',
       DATE_SUB(
               (SELECT transaction_date
                FROM supplier_transactions
                WHERE supplier_transaction_id = tp.supplier_transaction_id),
               INTERVAL FLOOR(3 + RAND() * 9) MONTH
       ),
       DATE_ADD(
               DATE_SUB(
                       (SELECT transaction_date
                        FROM supplier_transactions
                        WHERE supplier_transaction_id = tp.supplier_transaction_id),
                       INTERVAL FLOOR(3 + RAND() * 9) MONTH
               ),
               INTERVAL FLOOR(12 + RAND() * 24) MONTH
       )
FROM transaction_products tp
WHERE tp.item_num <= 1 + FLOOR(RAND() * 4)
GROUP BY tp.supplier_transaction_id, tp.product_id, tp.total_amount
HAVING SUM(ROUND(5 + RAND() * 45, -3) * FLOOR(10 + RAND() * 490)) <= tp.total_amount;

-- Generate sample data for categories and related tables
-- 1. Create product categories
INSERT INTO categories (name, description)
VALUES
    -- Main categories
    ('Prescription drugs', 'Medications requiring a prescription from a doctor'),
    ('Over-the-counter drugs (OTC)', 'Medications available without a prescription'),
    ('Dietary supplements', 'Products for nutritional supplementation and health support'),
    ('Medical devices', 'Equipment and tools for healthcare'),
    ('Personal care', 'Products for hygiene and personal care'),

    -- Sub-categories for prescription drugs
    ('Antibiotics', 'Medicines for treating bacterial infections'),
    ('Cardiovascular', 'Medicines for heart diseases and blood pressure'),
    ('Diabetes', 'Medicines for controlling and treating diabetes'),
    ('Gastric', 'Medicines for stomach and digestive disorders'),
    ('Neurological', 'Medicines affecting the central nervous system'),

    -- Sub-categories for OTC drugs
    ('Pain relievers and antipyretics', 'Common pain relief and fever reducing medications'),
    ('Cold and cough', 'Medicines for treating cold symptoms and cough'),
    ('Allergy', 'Medicines for treating allergic reactions'),
    ('Vitamins and minerals', 'Vitamin and mineral supplements'),
    ('Dermatology', 'Medicines for skin conditions'),

    -- Sub-categories for dietary supplements
    ('Immune boosters', 'Products supporting immune system enhancement'),
    ('Heart health', 'Products supporting cardiovascular health'),
    ('Joint health', 'Products supporting bone and joint health'),
    ('Digestive health', 'Products supporting digestion'),
    ('Men/Women health', 'Products specifically for men or women\'s health'),

    -- Sub-categories for medical devices
    ('Monitoring devices', 'Devices for monitoring health indicators'),
    ('Medical bandages and gauzes', 'Medical materials for wound dressing'),
    ('First aid items', 'Tools for initial first aid'),
    ('Care tools', 'Tools for patient care'),
    ('Support devices', 'Devices for movement and daily activity support'),

    -- Sub-categories for personal care
    ('Skin care', 'Products for face and body skin care'),
    ('Hair care', 'Products for hair and scalp care'),
    ('Oral hygiene', 'Products for dental and oral care'),
    ('Women\'s care', 'Hygiene and care products for women'),
    ('Deodorants and sunscreens', 'Body deodorants and sun protection products');

-- 2. Assign products to categories using a more MySQL-friendly approach
-- First create a temporary table for product-category pairs
CREATE TEMPORARY TABLE temp_product_categories
(
    product_id  BIGINT,
    category_id BIGINT
);

-- Populate temp table with basic matches (main categories)
INSERT INTO temp_product_categories (product_id, category_id)
SELECT p.product_id,
       (MOD(p.product_id, 5) + 1) AS category_id
FROM products p;

-- Add additional category matches for 2/3 of products
INSERT INTO temp_product_categories (product_id, category_id)
SELECT p.product_id,
       (MOD(p.product_id, 5) + 6) AS category_id
FROM products p
WHERE MOD(p.product_id, 3) <> 0;

-- Add third category for 1/3 of products
INSERT INTO temp_product_categories (product_id, category_id)
SELECT p.product_id,
       (MOD(p.product_id, 5) + 11) AS category_id
FROM products p
WHERE MOD(p.product_id, 3) = 0;

-- Now insert from temporary table to actual table
INSERT INTO product_categories (product_id, category_id)
SELECT DISTINCT product_id, category_id
FROM temp_product_categories
ORDER BY product_id, category_id;

-- 3. Add product tags with hashtag format - EACH PRODUCT GETS MULTIPLE TAGS
CREATE TEMPORARY TABLE temp_product_tags
(
    product_id BIGINT,
    tag        VARCHAR(255)
);

-- Each INSERT adds a specific tag to products matching the criteria
-- Products will accumulate multiple tags based on their ID
INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#painrelief'
FROM products
WHERE MOD(product_id, 5) = 0;

INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#antibiotic'
FROM products
WHERE MOD(product_id, 7) = 0;

INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#vitamins'
FROM products
WHERE MOD(product_id, 3) = 0;

-- These two ensure EVERY product gets either #imported OR #domestic
INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#imported'
FROM products
WHERE MOD(product_id, 4) = 0;

INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#domestic'
FROM products
WHERE MOD(product_id, 4) <> 0;

-- Additional specialized tags for subsets of products
INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#pediatric'
FROM products
WHERE MOD(product_id, 9) = 0;

INSERT INTO temp_product_tags (product_id, tag)
SELECT product_id, '#geriatric'
FROM products
WHERE MOD(product_id, 11) = 0;

-- Copy all tag combinations to the main table
INSERT INTO product_tags (product_id, name)
SELECT DISTINCT product_id, tag
FROM temp_product_tags
ORDER BY product_id, tag;

-- 4. Add additional product information
CREATE TEMPORARY TABLE temp_product_info
(
    product_id BIGINT,
    info_name  VARCHAR(255),
    info_value VARCHAR(255)
);

-- Add "Main ingredient" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Main ingredient',
       CASE MOD(product_id, 5)
           WHEN 0 THEN 'Paracetamol'
           WHEN 1 THEN 'Ibuprofen'
           WHEN 2 THEN 'Vitamin C'
           WHEN 3 THEN 'Calcium'
           ELSE 'Zinc'
           END
FROM products;

-- Add "Origin" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Origin',
       CASE MOD(product_id, 5)
           WHEN 0 THEN 'Vietnam'
           WHEN 1 THEN 'France'
           WHEN 2 THEN 'USA'
           WHEN 3 THEN 'Japan'
           ELSE 'Germany'
           END
FROM products;

-- Add "Manufacturer" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Manufacturer',
       CASE MOD(product_id, 5)
           WHEN 0 THEN 'DHG Pharma'
           WHEN 1 THEN 'Traphaco'
           WHEN 2 THEN 'Sanofi'
           WHEN 3 THEN 'Pfizer'
           ELSE 'GlaxoSmithKline'
           END
FROM products;

-- Add "Main use" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Main use',
       CASE MOD(product_id, 5)
           WHEN 0 THEN 'Pain relief and fever reduction'
           WHEN 1 THEN 'Anti-inflammatory'
           WHEN 2 THEN 'Immune system boost'
           WHEN 3 THEN 'Vitamin and mineral supplementation'
           ELSE 'Digestive support'
           END
FROM products;

-- Add "Dosage" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Dosage',
       CASE MOD(product_id, 5)
           WHEN 0 THEN 'Adults: 1-2 tablets per time, 3 times daily'
           WHEN 1 THEN 'Children over 12: 1 tablet per time, twice daily'
           WHEN 2 THEN '10ml every 8 hours'
           WHEN 3 THEN '1 sachet dissolved in 200ml warm water'
           ELSE '1 tablet daily after breakfast'
           END
FROM products;

-- Add "Storage" info
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Storage',
       CASE MOD(product_id, 3)
           WHEN 0 THEN 'Store in a dry place, away from direct sunlight'
           WHEN 1 THEN 'Temperature below 30°C'
           ELSE 'Keep out of reach of children'
           END
FROM products;

-- Add "Contraindications" info for 2/3 of products
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Contraindications',
       CASE MOD(product_id, 3)
           WHEN 0 THEN 'People with history of allergy to the product ingredients'
           WHEN 1 THEN 'Pregnant and breastfeeding women'
           ELSE 'Children under 12 years'
           END
FROM products
WHERE MOD(product_id, 3) <> 0;

-- Add "Side effects" info for 1/3 of products
INSERT INTO temp_product_info (product_id, info_name, info_value)
SELECT product_id,
       'Side effects',
       CASE MOD(product_id, 3)
           WHEN 0 THEN 'Nausea, mild headache'
           WHEN 1 THEN 'May cause drowsiness'
           ELSE 'Dry mouth, constipation'
           END
FROM products
WHERE MOD(product_id, 3) = 0;

-- Copy from temporary table to actual table
INSERT INTO product_additional_infos (product_id, name, value)
SELECT product_id, info_name, info_value
FROM temp_product_info
ORDER BY product_id, info_name;

-- Clean up temporary tables
DROP TEMPORARY TABLE temp_product_categories;
DROP TEMPORARY TABLE temp_product_tags;
DROP TEMPORARY TABLE temp_product_info;

-- Generate sample data for shipping_addresses table
-- First address for all users
INSERT INTO shipping_addresses (user_id, address_type, province, district, commune, detail_address)
WITH user_list AS (SELECT user_id
                   FROM users
                   ORDER BY user_id)
SELECT u.user_id,
       CASE WHEN MOD(u.user_id, 10) < 7 THEN 'HOME' ELSE 'COMPANY' END as address_type,

       -- Province
       CASE MOD(u.user_id, 10)
           WHEN 0 THEN 'California'
           WHEN 1 THEN 'New York'
           WHEN 2 THEN 'Texas'
           WHEN 3 THEN 'Florida'
           WHEN 4 THEN 'Illinois'
           WHEN 5 THEN 'Pennsylvania'
           WHEN 6 THEN 'Ohio'
           WHEN 7 THEN 'Georgia'
           WHEN 8 THEN 'North Carolina'
           ELSE 'Michigan'
           END                                                         as province,

       -- District
       CASE MOD(u.user_id, 8)
           WHEN 0 THEN 'Los Angeles'
           WHEN 1 THEN 'New York City'
           WHEN 2 THEN 'Houston'
           WHEN 3 THEN 'Miami'
           WHEN 4 THEN 'Chicago'
           WHEN 5 THEN 'Philadelphia'
           WHEN 6 THEN 'Cleveland'
           ELSE 'Atlanta'
           END                                                         as district,

       -- Commune
       CASE MOD(u.user_id, 6)
           WHEN 0 THEN 'Downtown'
           WHEN 1 THEN 'Westside'
           WHEN 2 THEN 'North End'
           WHEN 3 THEN 'Southpark'
           WHEN 4 THEN 'Eastville'
           ELSE 'Midtown'
           END                                                         as commune,

       -- Detail address (FIXED)
       CASE
           WHEN MOD(u.user_id, 10) < 7 THEN
               CONCAT(
                       FLOOR(100 + MOD(u.user_id * 7, 900)), ' ',
                       CASE MOD(u.user_id, 10)
                           WHEN 0 THEN 'Maple Drive'
                           WHEN 1 THEN 'Oak Street'
                           WHEN 2 THEN 'Pine Avenue'
                           WHEN 3 THEN 'Cedar Lane'
                           WHEN 4 THEN 'Elm Boulevard'
                           WHEN 5 THEN 'Willow Way'
                           WHEN 6 THEN 'Birch Street'
                           WHEN 7 THEN 'Walnut Avenue'
                           WHEN 8 THEN 'Cherry Lane'
                           ELSE 'Spruce Road'
                           END,
                       CASE
                           WHEN MOD(u.user_id, 15) = 0
                               THEN CONCAT(', Apt. ', FLOOR(100 + MOD(u.user_id * 11, 900)))
                           ELSE ''
                           END
               )
           ELSE
               CONCAT(
                       FLOOR(1000 + MOD(u.user_id * 11, 9000)), ' ',
                       CASE MOD(u.user_id, 8)
                           WHEN 0 THEN 'Corporate Plaza'
                           WHEN 1 THEN 'Business Center'
                           WHEN 2 THEN 'Technology Park'
                           WHEN 3 THEN 'Commerce Square'
                           WHEN 4 THEN 'Industrial Boulevard'
                           WHEN 5 THEN 'Financial District'
                           WHEN 6 THEN 'Office Park'
                           ELSE 'Executive Center'
                           END,
                       ', Suite ', FLOOR(100 + MOD(u.user_id * 13, 900))
               )
           END                                                         as detail_address
FROM user_list u;

-- Second address for ~40% of users
INSERT INTO shipping_addresses (user_id, address_type, province, district, commune, detail_address)
WITH user_list AS (SELECT user_id
                   FROM users
                   WHERE MOD(user_id, 5) < 2
                   ORDER BY user_id)
SELECT u.user_id,
       CASE WHEN MOD(u.user_id, 10) < 7 THEN 'COMPANY' ELSE 'HOME' END as address_type,

       -- Province
       CASE MOD(u.user_id + 5, 10)
           WHEN 0 THEN 'Washington'
           WHEN 1 THEN 'Massachusetts'
           WHEN 2 THEN 'Virginia'
           WHEN 3 THEN 'Arizona'
           WHEN 4 THEN 'Tennessee'
           WHEN 5 THEN 'Indiana'
           WHEN 6 THEN 'Missouri'
           WHEN 7 THEN 'Colorado'
           WHEN 8 THEN 'Oregon'
           ELSE 'Wisconsin'
           END                                                         as province,

       -- District
       CASE MOD(u.user_id + 4, 8)
           WHEN 0 THEN 'Seattle'
           WHEN 1 THEN 'Boston'
           WHEN 2 THEN 'Richmond'
           WHEN 3 THEN 'Phoenix'
           WHEN 4 THEN 'Nashville'
           WHEN 5 THEN 'Indianapolis'
           WHEN 6 THEN 'St. Louis'
           ELSE 'Denver'
           END                                                         as district,

       -- Commune
       CASE MOD(u.user_id + 3, 6)
           WHEN 0 THEN 'Riverview'
           WHEN 1 THEN 'Highland Park'
           WHEN 2 THEN 'Lakeshore'
           WHEN 3 THEN 'Hillcrest'
           WHEN 4 THEN 'Valley View'
           ELSE 'Parkside'
           END                                                         as commune,

       -- Detail address (FIXED)
       CASE
           WHEN MOD(u.user_id, 10) >= 7 THEN
               CONCAT(
                       FLOOR(200 + MOD(u.user_id * 9, 700)), ' ',
                       CASE MOD(u.user_id + 5, 10)
                           WHEN 0 THEN 'Sycamore Street'
                           WHEN 1 THEN 'Magnolia Avenue'
                           WHEN 2 THEN 'Juniper Lane'
                           WHEN 3 THEN 'Cypress Road'
                           WHEN 4 THEN 'Redwood Drive'
                           WHEN 5 THEN 'Aspen Court'
                           WHEN 6 THEN 'Laurel Way'
                           WHEN 7 THEN 'Hawthorn Boulevard'
                           WHEN 8 THEN 'Chestnut Street'
                           ELSE 'Poplar Avenue'
                           END,
                       CASE
                           WHEN MOD(u.user_id, 12) = 0
                               THEN CONCAT(', Unit ', FLOOR(10 + MOD(u.user_id * 7, 90)))
                           ELSE ''
                           END
               )
           ELSE
               CONCAT(
                       FLOOR(2000 + MOD(u.user_id * 13, 7000)), ' ',
                       CASE MOD(u.user_id + 6, 8)
                           WHEN 0 THEN 'Innovation Center'
                           WHEN 1 THEN 'Riverside Tower'
                           WHEN 2 THEN 'Metropolitan Building'
                           WHEN 3 THEN 'Gateway Plaza'
                           WHEN 4 THEN 'Summit Office Park'
                           WHEN 5 THEN 'Heritage Square'
                           WHEN 6 THEN 'Commerce Center'
                           ELSE 'Enterprise Building'
                           END,
                       ', Floor ', FLOOR(1 + MOD(u.user_id * 3, 20))
               )
           END                                                         as detail_address
FROM user_list u;

-- Generate sample data for coupons table
INSERT INTO coupons (code, description, discount_type, value, expiration_date, minimum_order_amount)
VALUES
    -- Percentage-based coupons (5% to 50%)
    ('WELCOME10', 'Welcome coupon for new customers - 10% off your first order', 'PERCENTAGE', 10.00,
     DATE_ADD('2025-06-08', INTERVAL 30 DAY), 0.00),
    ('SUMMER25', 'Summer sale special discount - 25% off all purchases', 'PERCENTAGE', 25.00,
     DATE_ADD('2025-06-08', INTERVAL 60 DAY), 0.00),
    ('FLASH15', 'Flash sale - 15% discount for 24 hours only', 'PERCENTAGE', 15.00,
     DATE_ADD('2025-06-08', INTERVAL 1 DAY), 0.00),
    ('LOYAL20', 'Loyalty reward - 20% off for returning customers', 'PERCENTAGE', 20.00,
     DATE_ADD('2025-06-08', INTERVAL 90 DAY), 0.00),
    ('BDAY30', 'Birthday special - 30% discount on your birthday month', 'PERCENTAGE', 30.00,
     DATE_ADD('2025-06-08', INTERVAL 180 DAY), 0.00),
    ('HEALTH5', 'Health promotion - 5% off vitamins and supplements', 'PERCENTAGE', 5.00,
     DATE_ADD('2025-06-08', INTERVAL 45 DAY), 0.00),
    ('APP10', 'Mobile app exclusive - 10% discount', 'PERCENTAGE', 10.00, DATE_ADD('2025-06-08', INTERVAL 30 DAY),
     0.00),
    ('MEMBER15', 'Membership benefit - 15% discount for members', 'PERCENTAGE', 15.00,
     DATE_ADD('2025-06-08', INTERVAL 120 DAY), 0.00),
    ('WEEKEND50', 'Weekend super sale - 50% off select items', 'PERCENTAGE', 50.00,
     DATE_ADD('2025-06-08', INTERVAL 2 DAY), 0.00),
    ('REFER20', 'Referral reward - 20% discount for referring friends', 'PERCENTAGE', 20.00,
     DATE_ADD('2025-06-08', INTERVAL 90 DAY), 0.00),

    -- Fixed-amount coupons ($2 to $20) with order minimums
    ('SAVE2', 'Save $2 on orders over $8', 'FIXED', 2.00, DATE_ADD('2025-06-08', INTERVAL 14 DAY), 8.00),
    ('FLAT4', 'Flat $4 off on minimum purchase of $20', 'FIXED', 4.00, DATE_ADD('2025-06-08', INTERVAL 30 DAY), 20.00),
    ('DISCOUNT8', 'Big savings - $8 off on orders over $40', 'FIXED', 8.00, DATE_ADD('2025-06-08', INTERVAL 60 DAY),
     40.00),
    ('VIP20', 'VIP customer appreciation - $20 discount', 'FIXED', 20.00, DATE_ADD('2025-06-08', INTERVAL 90 DAY),
     100.00),
    ('TRIAL3', 'Trial offer - $3 off your first prescription', 'FIXED', 3.00, DATE_ADD('2025-06-08', INTERVAL 30 DAY),
     10.00),
    ('GIFT6', 'Special gift - $6 discount on premium products', 'FIXED', 6.00, DATE_ADD('2025-06-08', INTERVAL 45 DAY),
     30.00),
    ('SENIOR4', 'Senior citizen benefit - $4 discount', 'FIXED', 4.00, DATE_ADD('2025-06-08', INTERVAL 180 DAY), 15.00),
    ('BUNDLE10', 'Bundle offer - $10 off when buying 3+ items', 'FIXED', 10.00, DATE_ADD('2025-06-08', INTERVAL 21 DAY),
     35.00),
    ('EXPRESS2', 'Express delivery - $2 off shipping fee', 'FIXED', 2.00, DATE_ADD('2025-06-08', INTERVAL 14 DAY),
     10.00),
    ('DEVICE12', 'Medical device discount - $12 off', 'FIXED', 12.00, DATE_ADD('2025-06-08', INTERVAL 60 DAY), 60.00),

    -- Expired coupons
    ('EXPIRED20', 'Limited time offer - expired', 'PERCENTAGE', 20.00, DATE_SUB('2025-06-08', INTERVAL 30 DAY), 0.00),
    ('PAST4', 'Past promotion - no longer valid', 'FIXED', 4.00, DATE_SUB('2025-06-08', INTERVAL 15 DAY), 20.00),
    ('ENDED15', 'Ended promotion campaign', 'PERCENTAGE', 15.00, DATE_SUB('2025-06-08', INTERVAL 5 DAY), 0.00);

-- Generate sample data for user_coupons table
-- Insert user coupons - some used, some not yet used
INSERT INTO user_coupons (coupon_id, user_id, use_at)
WITH user_list AS (SELECT user_id
                   FROM users
                   ORDER BY user_id),
     coupon_list AS (SELECT coupon_id, code, expiration_date
                     FROM coupons
                     ORDER BY coupon_id),
-- Create combinations that make business sense:
-- 1. Active coupons (not expired) assigned to users
-- 2. Some coupons used, some not yet used
-- 3. Realistic distribution pattern
     user_coupon_assignments AS (
         -- WELCOME10 coupons for newest users (20% used)
         SELECT 1       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 5) = 0
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 15)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 3) = 0
           AND u.user_id > 50

         UNION ALL

         -- SUMMER25 for users who made purchases in last 3 months (30% used)
         SELECT 2       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 3) = 0
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 10)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 4) = 0

         UNION ALL

         -- FLASH15 for active users (70% used since it expires in 1 day)
         SELECT 3       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 10) < 7
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(MOD(u.user_id, 12)) HOUR)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 5) = 0

         UNION ALL

         -- LOYAL20 for long-term customers (40% used)
         SELECT 4       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 5) < 2
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 20)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 3) = 1
           AND u.user_id < 100

         UNION ALL

         -- BDAY30 for selected customers (15% used)
         SELECT 5       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 20) < 3
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 30)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 6) = 0

         UNION ALL

         -- HEALTH5 for users interested in health products (50% used)
         SELECT 6       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 2) = 0
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 25)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 4) = 1

         UNION ALL

         -- APP10 for mobile app users (30% used)
         SELECT 7       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 10) < 3
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 18)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 3) = 2

         UNION ALL

         -- MEMBER15 for members (25% used)
         SELECT 8       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 4) = 0
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id, 40)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 5) = 2

         UNION ALL

         -- WEEKEND50 (80% used since expiring soon)
         SELECT 9       as coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id, 5) < 4
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(MOD(u.user_id, 36)) HOUR)
                    ELSE NULL
                    END as use_at
         FROM user_list u
         WHERE MOD(u.user_id, 10) = 0

         UNION ALL

         -- Add some fixed-value coupons to users
         SELECT c.coupon_id,
                u.user_id,
                CASE
                    WHEN MOD(u.user_id + c.coupon_id, 4) = 0
                        THEN DATE_SUB('2025-06-08 10:42:05', INTERVAL FLOOR(1 + MOD(u.user_id + c.coupon_id, 30)) DAY)
                    ELSE NULL
                    END as use_at
         FROM user_list u
                  CROSS JOIN coupon_list c
         WHERE c.coupon_id BETWEEN 11 AND 15
           AND MOD(u.user_id + c.coupon_id, 7) = 0
           AND c.expiration_date > '2025-06-08')
-- Final select with simple duplicate check
SELECT coupon_id, user_id, use_at
FROM user_coupon_assignments;

-- Generate sample data for orders table
INSERT INTO orders (order_type,
                    appointment_id,
                    shipping_address_id,
                    shipping_fee,
                    total_amount,
                    order_status,
                    real_amount,
                    coupon_id)
WITH
-- Use the variable instead of hardcoded value
order_count_param AS (SELECT @order_count as num_orders),
-- Generate more than enough rows
row_generator AS (SELECT ROW_NUMBER() OVER () as row_id
                  FROM (SELECT 1 AS n
                        UNION ALL
                        SELECT 2
                        UNION ALL
                        SELECT 3
                        UNION ALL
                        SELECT 4
                        UNION ALL
                        SELECT 5
                        UNION ALL
                        SELECT 6
                        UNION ALL
                        SELECT 7
                        UNION ALL
                        SELECT 8
                        UNION ALL
                        SELECT 9
                        UNION ALL
                        SELECT 10) t1,
                       (SELECT 1 AS n
                        UNION ALL
                        SELECT 2
                        UNION ALL
                        SELECT 3
                        UNION ALL
                        SELECT 4
                        UNION ALL
                        SELECT 5
                        UNION ALL
                        SELECT 6
                        UNION ALL
                        SELECT 7
                        UNION ALL
                        SELECT 8
                        UNION ALL
                        SELECT 9
                        UNION ALL
                        SELECT 10) t2,
                       (SELECT 1 AS n
                        UNION ALL
                        SELECT 2
                        UNION ALL
                        SELECT 3
                        UNION ALL
                        SELECT 4
                        UNION ALL
                        SELECT 5) t3 -- 10×10×5 = 500 rows (supports up to 500 orders)
),
-- Only keep exactly @order_count rows
filtered_rows AS (SELECT row_id
                  FROM row_generator,
                       order_count_param
                  WHERE row_id <= num_orders),
-- Rest of the query is the same
appointment_list AS (SELECT appointment_id, patient_id
                     FROM appointments
                     ORDER BY appointment_id),
address_list AS (SELECT shipping_address_id, user_id
                 FROM shipping_addresses
                 ORDER BY shipping_address_id),
coupon_list AS (SELECT coupon_id,
                       discount_type,
                       value,
                       minimum_order_amount,
                       expiration_date
                FROM coupons
                WHERE expiration_date > '2025-06-08'
                ORDER BY coupon_id),
order_base AS (SELECT fr.row_id,
                      CASE WHEN RAND() < 0.4 THEN 'APPOINTMENT' ELSE 'DIRECT' END            as order_type,
                      (SELECT appointment_id FROM appointment_list ORDER BY RAND() LIMIT 1)  as appointment_id,
                      (SELECT shipping_address_id FROM address_list ORDER BY RAND() LIMIT 1) as shipping_address_id,
                      ROUND(CASE
                                WHEN RAND() < 0.3 THEN 0 -- 30% free shipping
                                ELSE RAND() * 10 -- 70% paid shipping $0-$10
                                END, 2)                                                      as shipping_fee,
                      CASE
                          WHEN RAND() < 0.75 THEN 'FULLFILED'
                          WHEN RAND() < 0.95 THEN 'PENDING'
                          ELSE 'CANCELLED'
                          END                                                                as order_status,
                      CASE
                          WHEN RAND() < 0.3
                              THEN (SELECT coupon_id FROM coupon_list ORDER BY RAND() LIMIT 1)
                          ELSE NULL
                          END                                                                as coupon_id
               FROM filtered_rows fr),
order_item_counts AS (SELECT row_id,
                             order_type,
                             appointment_id,
                             shipping_address_id,
                             shipping_fee,
                             order_status,
                             coupon_id,
                             FLOOR(1 + RAND() * 5) as item_count
                      FROM order_base),
order_amounts AS (SELECT oic.row_id,
                         oic.order_type,
                         oic.appointment_id,
                         oic.shipping_address_id,
                         oic.shipping_fee,
                         oic.order_status,
                         oic.coupon_id,
                         oic.item_count,
                         ROUND(5 + (oic.item_count * 7.5) + RAND() * 25, 2) as total_amount
                  FROM order_item_counts oic)
SELECT oa.order_type,
       oa.appointment_id,
       oa.shipping_address_id,
       oa.shipping_fee,
       oa.total_amount,
       oa.order_status,
       CASE
           WHEN oa.coupon_id IS NULL THEN
               oa.total_amount + oa.shipping_fee
           WHEN oa.total_amount < (SELECT minimum_order_amount FROM coupon_list WHERE coupon_id = oa.coupon_id) THEN
               oa.total_amount + oa.shipping_fee
           WHEN (SELECT discount_type FROM coupon_list WHERE coupon_id = oa.coupon_id) = 'PERCENTAGE' THEN
               ROUND(oa.total_amount * (1 - (SELECT value FROM coupon_list WHERE coupon_id = oa.coupon_id) / 100), 2) +
               oa.shipping_fee
           ELSE
               GREATEST(0.50,
                        ROUND(oa.total_amount - (SELECT value FROM coupon_list WHERE coupon_id = oa.coupon_id), 2)) +
               oa.shipping_fee
           END as real_amount,
       CASE
           WHEN oa.coupon_id IS NULL THEN NULL
           WHEN oa.total_amount < (SELECT minimum_order_amount FROM coupon_list WHERE coupon_id = oa.coupon_id)
               THEN NULL
           ELSE oa.coupon_id
           END as coupon_id
FROM order_amounts oa;

-- Order items generation remains the same
INSERT INTO order_items (order_id, product_id, quantity)
WITH order_list AS (SELECT order_id, order_status
                    FROM orders
                    ORDER BY order_id),
     product_list AS (SELECT product_id
                      FROM products
                      ORDER BY product_id),
     order_item_counts AS (SELECT ol.order_id,
                                  FLOOR(1 + RAND() * 5) as item_count
                           FROM order_list ol),
     order_product_pairs AS (SELECT oic.order_id,
                                    oic.item_count,
                                    pl.product_id,
                                    ROW_NUMBER() OVER (PARTITION BY oic.order_id ORDER BY RAND()) as seq_num
                             FROM order_item_counts oic
                                      CROSS JOIN product_list pl)
SELECT opp.order_id,
       opp.product_id,
       FLOOR(1 + CASE WHEN RAND() < 0.8 THEN RAND() ELSE RAND() * 4 END) as quantity
FROM order_product_pairs opp
WHERE opp.seq_num <= opp.item_count
ORDER BY opp.order_id, opp.product_id;

-- Generate sample data for wallets and related tables
-- 1. Create multiple wallets for users (1 user = N wallets)
INSERT INTO wallets (user_id, balance)
WITH user_list AS (SELECT user_id
                   FROM users
                   ORDER BY user_id),
-- Generate 1-3 wallets per user
     wallet_generator AS (SELECT user_id,
                                 -- Wallet type indicator (0=main, 1=savings, 2=health)
                                 wallet_num
                          FROM user_list
                                   CROSS JOIN (SELECT 0 as wallet_num
                                               UNION ALL
                                               SELECT 1
                                               UNION ALL
                                               SELECT 2) wallet_types
                          -- Give every user at least one wallet
                          -- Give ~60% of users a second wallet
                          -- Give ~30% of users a third wallet
                          WHERE wallet_num = 0
                             OR (wallet_num = 1 AND MOD(user_id, 5) < 3)
                             OR (wallet_num = 2 AND MOD(user_id, 10) < 3))
SELECT wg.user_id,
       -- Different initial balances based on wallet type
       CASE wg.wallet_num
           WHEN 0 THEN ROUND(50 + RAND() * 950, -3) -- Main: $50-$1,000
           WHEN 1 THEN ROUND(100 + RAND() * 4900, -3) -- Savings: $100-$5,000
           ELSE ROUND(30 + RAND() * 270, -3) -- Health: $30-$300
           END as balance
FROM wallet_generator wg;

-- 2. Generate payments for orders (1 order = 1 payment)
INSERT INTO payments (order_id, amount, payment_method, payment_status, payment_time)
WITH order_data AS (SELECT o.order_id,
                           o.real_amount,
                           o.order_status,
                           -- Join to get the user_id via shipping address
                           sa.user_id
                    FROM orders o
                             JOIN shipping_addresses sa ON o.shipping_address_id = sa.shipping_address_id)
SELECT od.order_id,
       od.real_amount as amount,

       -- Distribute payment methods: 40% CASH, 35% CARD, 25% MOMO
       CASE
           WHEN RAND() < 0.4 THEN 'CASH'
           WHEN RAND() < 0.75 THEN 'CARD'
           ELSE 'MOMO'
           END        as payment_method,

       -- Payment status based on order status
       CASE
           WHEN od.order_status = 'FULLFILED' THEN 'SUCCESSED'
           WHEN od.order_status = 'CANCELLED' THEN 'FAILED'
           ELSE 'PENDING'
           END        as payment_status,

       -- Payment time (using current date from context)
       CASE
           WHEN od.order_status = 'FULLFILED' THEN
               DATE_SUB('2025-06-08 11:12:51', INTERVAL FLOOR(RAND() * 72) HOUR)
           WHEN od.order_status = 'PENDING' THEN
               DATE_SUB('2025-06-08 11:12:51', INTERVAL FLOOR(RAND() * 24) HOUR)
           ELSE
               DATE_SUB('2025-06-08 11:12:51', INTERVAL (1 + FLOOR(RAND() * 10)) DAY)
           END        as payment_time

FROM order_data od;

-- 3. Generate wallet transactions for payments (1 payment = 1 wallet_transaction)
INSERT INTO wallet_transactions (wallet_id, payment_id, amount, wallet_transaction_type, description)
WITH payment_data AS (SELECT p.payment_id,
                             p.order_id,
                             p.amount,
                             p.payment_method,
                             p.payment_status,
                             -- Join to get the user_id
                             sa.user_id
                      FROM payments p
                               JOIN orders o ON p.order_id = o.order_id
                               JOIN shipping_addresses sa ON o.shipping_address_id = sa.shipping_address_id
                      WHERE p.payment_method = 'MOMO' -- Only MOMO payments use wallet
),
-- Get main wallet for each user (wallet_num = 0)
     user_main_wallet AS (SELECT w.wallet_id,
                                 w.user_id,
                                 ROW_NUMBER() OVER (PARTITION BY w.user_id ORDER BY w.wallet_id) as wallet_rank
                          FROM wallets w)
SELECT
    -- Select the main wallet for the user
    (SELECT wallet_id FROM user_main_wallet WHERE user_id = pd.user_id AND wallet_rank = 1) as wallet_id,
    pd.payment_id,
    pd.amount,
    'WITHDRAW'                                                                              as wallet_transaction_type,
    CONCAT('Payment for order #', pd.order_id, ' - $', FORMAT(pd.amount, 2))                as description
FROM payment_data pd;

-- 4. Add additional wallet transactions (1 wallet = N wallet_transactions)
INSERT INTO wallet_transactions (wallet_id, payment_id, amount, wallet_transaction_type, description)
WITH wallet_list AS (SELECT wallet_id, user_id
                     FROM wallets
                     ORDER BY wallet_id),
     payment_list AS (
         -- Get successful payments to reference
         SELECT payment_id FROM payments WHERE payment_status = 'SUCCESSED' ORDER BY payment_id),
-- Generate regular deposits for each wallet
     deposit_generator AS (SELECT w.wallet_id,
                                  -- Reference a payment (for foreign key)
                                  (SELECT payment_id FROM payment_list ORDER BY RAND() LIMIT 1) as payment_id,
                                  -- Generate deposit amount
                                  ROUND(25000 + RAND() * 475000, -3)                            as amount,
                                  -- Sequence number (to create multiple transactions)
                                  ROW_NUMBER() OVER (PARTITION BY w.wallet_id ORDER BY RAND())  as seq_num
                           FROM wallet_list w
                                    CROSS JOIN (SELECT 1 as n UNION ALL SELECT 2 UNION ALL SELECT 3) numbers
                           -- Ensure different number of transactions per wallet
                           WHERE MOD(w.wallet_id + FLOOR(RAND() * 10), 4) < numbers.n)
SELECT dg.wallet_id,
       dg.payment_id,
       dg.amount,
       'DEPOSIT' as wallet_transaction_type,
       CASE MOD(dg.wallet_id, 5)
           WHEN 0 THEN CONCAT('Monthly deposit - $', FORMAT(dg.amount, 2))
           WHEN 1 THEN CONCAT('Savings transfer - $', FORMAT(dg.amount, 2))
           WHEN 2 THEN CONCAT('Healthcare fund - $', FORMAT(dg.amount, 2))
           WHEN 3 THEN CONCAT('Medication budget - $', FORMAT(dg.amount, 2))
           ELSE CONCAT('Regular deposit - $', FORMAT(dg.amount, 2))
           END   as description
FROM deposit_generator dg;

-- 5. Update wallet balances based on transactions
-- Create a temp table with the calculations
CREATE TEMPORARY TABLE temp_wallet_balances AS
SELECT w.wallet_id,
       w.user_id,
       COALESCE(
               (SELECT SUM(
                               CASE
                                   WHEN wt.wallet_transaction_type = 'DEPOSIT' THEN wt.amount
                                   ELSE -wt.amount
                                   END
                       )
                FROM wallet_transactions wt
                WHERE wt.wallet_id = w.wallet_id),
               0
       ) + w.balance as new_balance
FROM wallets w;

-- Update the wallets table
UPDATE wallets w
    JOIN temp_wallet_balances twb ON w.wallet_id = twb.wallet_id
SET w.balance = twb.new_balance;

-- Clean up
DROP TEMPORARY TABLE temp_wallet_balances;

-- Thiết lập các biến kiểm soát số lượng bản ghi
-- Thiết lập các biến kiểm soát số lượng bản ghi
SET @ngay_hien_tai = '2025-06-08 13:21:56';

-- 1. Tạo dữ liệu giỏ hàng (CART_ITEMS)
-- Sử dụng bảng tạm với ID tự tăng
DROP TEMPORARY TABLE IF EXISTS gio_hang_tam;
CREATE TEMPORARY TABLE gio_hang_tam
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,
    product_id BIGINT,
    quantity   INT
);

-- Chèn dữ liệu vào bảng tạm (nhiều hơn cần thiết)
INSERT INTO gio_hang_tam (user_id, product_id, quantity)
SELECT u.user_id,
       p.product_id,
       -- Phần lớn mua 1 sản phẩm, một số mua 2-5
       CASE
           WHEN RAND() < 0.7 THEN 1 -- 70% mua 1 sản phẩm
           WHEN RAND() < 0.9 THEN 2 -- 20% mua 2 sản phẩm
           ELSE FLOOR(3 + RAND() * 3) -- 10% mua 3-5 sản phẩm
           END as quantity
FROM (SELECT user_id FROM users WHERE MOD(user_id, @nguoi_dung_co_gio) = 0) u
         CROSS JOIN
     (SELECT product_id FROM products ORDER BY RAND()) p
LIMIT 1000;
-- Tạo nhiều hơn để đảm bảo đủ sau khi lọc

-- Chèn vào bảng chính với số lượng chính xác từ biến (dùng WHERE thay vì LIMIT)
INSERT INTO cart_items (cart_item_id, user_id, product_id, quantity)
SELECT id as cart_item_id,
       user_id,
       product_id,
       quantity
FROM gio_hang_tam
WHERE id <= @so_luong_gio_hang;

-- 2. Tạo dữ liệu danh sách yêu thích (WISHLIST_PRODUCTS)
-- Sử dụng bảng tạm để lọc trùng lặp với giỏ hàng
DROP TEMPORARY TABLE IF EXISTS yeu_thich_tam;
CREATE TEMPORARY TABLE yeu_thich_tam
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,
    product_id BIGINT
);

-- Chèn dữ liệu vào bảng tạm (tránh trùng với giỏ hàng)
INSERT INTO yeu_thich_tam (user_id, product_id)
SELECT DISTINCT u.user_id,
                p.product_id
FROM (SELECT user_id FROM users WHERE MOD(user_id, @nguoi_dung_co_yeu_thich) = 0) u
         CROSS JOIN
     (SELECT product_id FROM products ORDER BY RAND()) p
         LEFT JOIN
     cart_items ci ON u.user_id = ci.user_id AND p.product_id = ci.product_id
WHERE ci.user_id IS NULL -- Đảm bảo sản phẩm chưa có trong giỏ hàng
LIMIT 1000;
-- Tạo nhiều hơn để đảm bảo đủ

-- Chèn vào bảng chính với số lượng chính xác từ biến (dùng WHERE thay vì LIMIT)
INSERT INTO wishlist_products (user_id, product_id)
SELECT user_id,
       product_id
FROM yeu_thich_tam
WHERE id <= @so_luong_yeu_thich;

-- 3. Tạo dữ liệu thông báo (NOTIFICATIONS)
-- Tạo bảng số để nhân bản thông báo
DROP TEMPORARY TABLE IF EXISTS cac_so;
CREATE TEMPORARY TABLE cac_so
(
    n INT
);
INSERT INTO cac_so
VALUES (1),
       (2),
       (3),
       (4),
       (5),
       (6),
       (7),
       (8),
       (9),
       (10);

-- Tạo bảng cấu hình thông báo cho từng người dùng
DROP TEMPORARY TABLE IF EXISTS cau_hinh_thong_bao;
CREATE TEMPORARY TABLE cau_hinh_thong_bao
(
    user_id      BIGINT PRIMARY KEY,
    so_thong_bao INT -- Số lượng thông báo mỗi người nhận được
);

-- Phân loại người dùng theo mức độ hoạt động
INSERT INTO cau_hinh_thong_bao (user_id, so_thong_bao)
SELECT user_id,
       CASE
           WHEN user_id < 50 THEN 1 + FLOOR(RAND() * 10) -- 1-10 thông báo cho người dùng tích cực
           WHEN user_id < 100 THEN 1 + FLOOR(RAND() * 5) -- 1-5 thông báo cho người dùng trung bình
           ELSE 1 + FLOOR(RAND() * 3) -- 1-3 thông báo cho người dùng ít hoạt động
           END as so_thong_bao
FROM users;

-- Insert notifications into the main table
INSERT INTO notifications (user_id, title, content)
SELECT cht.user_id,
       -- Generate different notification titles
       CASE MOD(cs.n + cht.user_id, 7)
           WHEN 0 THEN 'Order Confirmed'
           WHEN 1 THEN 'Special Offer'
           WHEN 2 THEN 'Prescription Ready'
           WHEN 3 THEN 'Discount Alert'
           WHEN 4 THEN 'Account Update'
           WHEN 5 THEN 'Delivery Status'
           ELSE 'Medication Refill Reminder'
           END AS title,

       -- Generate content matching the title
       CASE MOD(cs.n + cht.user_id, 7)
           WHEN 0 THEN CONCAT('Your order #', FLOOR(1000 + RAND() * 9000),
                              ' has been confirmed and will be delivered soon.')
           WHEN 1 THEN CONCAT('Enjoy a ', FLOOR(10 + RAND() * 30), '% discount on your next order with code: SPECIAL',
                              FLOOR(RAND() * 1000))
           WHEN 2 THEN 'Your prescription is ready for pickup or delivery. Please check your order status.'
           WHEN 3 THEN 'An item on your wishlist just went on sale! Check it out now.'
           WHEN 4 THEN 'Your account information has been successfully updated.'
           WHEN 5 THEN CONCAT('Order #', FLOOR(1000 + RAND() * 9000),
                              ' is on its way. Expected delivery: Today.')
           ELSE CONCAT('Time to restock your medication! Your ',
                       ELT(FLOOR(1 + RAND() * 5), 'cardiac', 'pain relief', 'antiallergic', 'vitamin', 'antibiotic'),
                       ' supply is running low.')
           END AS content
FROM cau_hinh_thong_bao cht
         JOIN cac_so cs ON cs.n <= cht.so_thong_bao;

-- Dọn dẹp các bảng tạm
DROP TEMPORARY TABLE IF EXISTS gio_hang_tam;
DROP TEMPORARY TABLE IF EXISTS yeu_thich_tam;
DROP TEMPORARY TABLE IF EXISTS cac_so;
DROP TEMPORARY TABLE IF EXISTS cau_hinh_thong_bao;

SET SQL_SAFE_UPDATES = 1;
SET FOREIGN_KEY_CHECKS = 1;
