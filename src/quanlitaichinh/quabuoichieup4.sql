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
    `relationship` ENUM ('SELF', 'FATHER', 'MOTHER', 'HUSBAND', 'WIFE', 'BROTHER', 'SISTER', 'DAUGHTER', 'SON', 'GRAND_FATHER', 'GRAND_MOTHER', 'UNCLE', 'AUNT', 'CAUSIN', 'OTHER') DEFAULT 'SELF',
    `address`      VARCHAR(255),
    `gender`       ENUM ('MALE', 'FEMALE', 'OTHER')                                                                                                              DEFAULT 'OTHER',
    `birthdate`    DATE,
    `blood_type`   ENUM ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
    `patient_status` ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
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

CREATE TABLE `appointments` (
    `appointment_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` BIGINT,
    `patient_id` BIGINT,
    `service_id` BIGINT,
    `start_time` TIMESTAMP,
    `duration_minutes` INT,
    `appointment_status` ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'IN_PROGRESS', 'CANCELLED'),
    `scheduling_coordinator_id` BIGINT,
    FOREIGN KEY (`doctor_id`)
        REFERENCES `doctors` (`doctor_id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`patient_id`)
        REFERENCES `patients` (`patient_id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`service_id`)
        REFERENCES `services` (`service_id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (`scheduling_coordinator_id`)
        REFERENCES `scheduling_coordinators` (`scheduling_coordinator_id`)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `medical_records` (
                                 medical_record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 patient_id BIGINT NOT NULL,
                                 appointment_id BIGINT UNIQUE NOT NULL,
                                 admission_date DATE,
                                 discharge_date DATE,
                                 main_complaint VARCHAR(255),
                                 diagnosis TEXT,
                                 treatment_plan TEXT,
                                 outcome VARCHAR(255),
                                 FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
                                 FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

CREATE TABLE vital_signs (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             medical_record_id BIGINT NOT NULL,
                             pulse_rate INT,
                             bp_systolic INT,
                             bp_diastolic INT,
                             temperature DECIMAL(4,1),
                             respiratory_rate INT,
                             spo2 INT,
                             recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám hô hấp
CREATE TABLE respiratory_exams (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   medical_record_id BIGINT NOT NULL,
                                   breathing_pattern VARCHAR(50),
                                   fremitus VARCHAR(50),
                                   percussion_note VARCHAR(100),
                                   auscultation TEXT,
                                   recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám tim mạch
CREATE TABLE cardiac_exams (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               medical_record_id BIGINT NOT NULL,
                               heart_rate INT,
                               heart_sounds TEXT,
                               murmur TEXT,
                               jugular_venous_pressure VARCHAR(100),
                               edema VARCHAR(100),
                               recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám thần kinh
CREATE TABLE neurologic_exams (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  medical_record_id BIGINT NOT NULL,
                                  consciousness VARCHAR(100),
                                  cranial_nerves TEXT,
                                  motor_function TEXT,
                                  sensory_function TEXT,
                                  reflexes TEXT,
                                  recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám tiêu hóa
CREATE TABLE gastrointestinal_exams (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        medical_record_id BIGINT NOT NULL,
                                        abdominal_inspection TEXT,
                                        palpation TEXT,
                                        percussion TEXT,
                                        auscultation TEXT,
                                        recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám tiết niệu - sinh dục
CREATE TABLE genitourinary_exams (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     medical_record_id BIGINT NOT NULL,
                                     kidney_area TEXT,
                                     bladder TEXT,
                                     genital_inspection TEXT,
                                     recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám cơ xương khớp
CREATE TABLE musculoskeletal_exams (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       medical_record_id BIGINT NOT NULL,
                                       joint_exam TEXT,
                                       muscle_strength TEXT,
                                       deformity TEXT,
                                       recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Khám da liễu
CREATE TABLE dermatologic_exams (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    medical_record_id BIGINT NOT NULL,
                                    skin_appearance TEXT,
                                    rash TEXT,
                                    lesions TEXT,
                                    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

-- Ghi chú khác
CREATE TABLE clinical_notes (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                medical_record_id BIGINT NOT NULL,
                                note_text TEXT,
                                recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);


CREATE TABLE `medical_record_symptoms`
(
    `id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `medical_record_id` BIGINT,
    `symptom_name` VARCHAR(255),
    `onset_date` DATE,
    `duration` VARCHAR(100),
    `severity` VARCHAR(100),
    `description` TEXT,
    FOREIGN KEY (`medical_record_id`) REFERENCES `medical_records` (`medical_record_id`) ON DELETE CASCADE
);

CREATE TABLE `medical_profiles`
(
    `medical_profile_id` BIGINT UNIQUE PRIMARY KEY AUTO_INCREMENT,
    `patient_id`         BIGINT UNIQUE,
    `allergies`          JSON,
    `chronic_diseases`   JSON,
    FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE test_types (
    test_type_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_type_name VARCHAR(100) NOT NULL,
    product_id     BIGINT UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 3. Bảng test_items (Giữ nguyên Code 2)
CREATE TABLE test_items (
    test_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    unit         VARCHAR(20),
    ref_min      DECIMAL(10,2),
    ref_max      DECIMAL(10,2),
    test_type_id BIGINT,
    FOREIGN KEY (test_type_id) REFERENCES test_types(test_type_id)
);

-- 4. Bảng reference_ranges (Giữ nguyên Code 2)
CREATE TABLE reference_ranges (
    range_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_item_id        BIGINT NOT NULL,
    gender ENUM ('MALE', 'FEMALE'),
    age_min             INT NOT NULL,
    age_max             INT NOT NULL,
    min_value           DECIMAL(10,2),
    max_value           DECIMAL(10,2),
    suspected_condition TEXT,
    FOREIGN KEY (test_item_id) REFERENCES test_items(test_item_id)
);

-- 2. Bảng test_requests (Đổi tên từ orders)
CREATE TABLE test_requests (
    test_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Đổi order_id → test_request_id
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT NOT NULL,
    appointment_id  BIGINT NOT NULL,
    test_type_id    BIGINT NOT NULL,
    request_status  ENUM('pending','received','processing','completed','rejected') NOT NULL DEFAULT 'pending',  -- Đổi order_status → request_status
    request_time    DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Đổi order_time → request_time
    reason          TEXT,
    FOREIGN KEY (patient_id)     REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id)      REFERENCES users(user_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    FOREIGN KEY (test_type_id)   REFERENCES test_types(test_type_id)
);

-- 3. Cập nhật các bảng liên quan
CREATE TABLE samples (
    sample_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_request_id    BIGINT NOT NULL,  -- Đổi order_id → test_request_id
    barcode            VARCHAR(50) UNIQUE NOT NULL,
    sampler_id         BIGINT,
    sample_status      ENUM('collected','rejected','pending') NOT NULL DEFAULT 'pending',
    collection_time    DATETIME,
    recollection_reason TEXT,
    notes              TEXT,
    retest_time        DATETIME,
    FOREIGN KEY (test_request_id) REFERENCES test_requests(test_request_id),  -- Cập nhật FK
    FOREIGN KEY (sampler_id) REFERENCES users(user_id)
);

CREATE TABLE results (
    result_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sample_id          BIGINT NOT NULL,
    test_type_id       BIGINT NOT NULL,
    result_entry_status ENUM('pending','entered','verified','sent') DEFAULT 'pending',
    technician_id      BIGINT,
    approved_by        BIGINT,
    approved_time      DATETIME,
    notes              TEXT,
    FOREIGN KEY (sample_id)    REFERENCES samples(sample_id),
    FOREIGN KEY (test_type_id) REFERENCES test_types(test_type_id),  -- Vẫn giữ test_type_id
    FOREIGN KEY (technician_id) REFERENCES users(user_id),
    FOREIGN KEY (approved_by)  REFERENCES users(user_id)
);

CREATE TABLE result_details (
    detail_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_id           BIGINT NOT NULL,
    test_item_id        BIGINT NOT NULL,
    value               DECIMAL(10,2),
    flag                ENUM('normal','↑','↓','critical') DEFAULT 'normal',
    suspected_condition TEXT,
    FOREIGN KEY (result_id) REFERENCES results(result_id),
    FOREIGN KEY (test_item_id) REFERENCES test_items(test_item_id)
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


CREATE TABLE `leave_requests` (
    `leave_request_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `staff_id` BIGINT NOT NULL,
    `leave_type` ENUM (
        'ANNUAL_LEAVE', 
        'SICK_LEAVE', 
        'MATERNITY_LEAVE', 
        'PATERNITY_LEAVE', 
        'UNPAID_LEAVE', 
        'STUDY_LEAVE',
        'EMERGENCY_LEAVE'
    ) NOT NULL DEFAULT 'ANNUAL_LEAVE',
    `start_date` TIMESTAMP NOT NULL,
    `end_date` TIMESTAMP NOT NULL,
    `duration_hours` DECIMAL(5,2), -- Cho phép nghỉ nửa ngày
    `reason` TEXT,
    `status` ENUM (
        'DRAFT',
        'PENDING',
        'APPROVED',
        'REJECTED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'DRAFT',
    `approver_id` BIGINT, -- FK tới staffs
    `approved_at` TIMESTAMP,
    `rejection_reason` TEXT,
    `emergency_contact` VARCHAR(255),
    `substitute_staff_id` BIGINT, -- Người thay thế
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (`staff_id`) REFERENCES `staffs`(`staff_id`),
    FOREIGN KEY (`approver_id`) REFERENCES `staffs`(`staff_id`),
    FOREIGN KEY (`substitute_staff_id`) REFERENCES `staffs`(`staff_id`)
);
CREATE TABLE `leave_balances` (
    `balance_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `staff_id` BIGINT NOT NULL,
    `leave_type` ENUM (
        'ANNUAL_LEAVE', 
        'SICK_LEAVE', 
        'MATERNITY_LEAVE', 
        'PATERNITY_LEAVE',
        'STUDY_LEAVE'
    ) NOT NULL,
    `year` YEAR NOT NULL,
    `total_entitlement` DECIMAL(5,2) NOT NULL, -- Tổng số giờ/nghỉ được hưởng
    `used_balance` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `pending_balance` DECIMAL(5,2) NOT NULL DEFAULT 0.00, -- Số giờ đang chờ duyệt
    
    UNIQUE KEY `unique_balance` (`staff_id`, `leave_type`, `year`),
    FOREIGN KEY (`staff_id`) REFERENCES `staffs`(`staff_id`)
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
SET @appointment_count = 1000;
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
            'SELF', 'FATHER', 'MOTHER', 'HUSBAND', 'WIFE', 'BROTHER', 'SISTER', 'DAUGHTER', 'SON', 'GRAND_FATHER', 'GRAND_MOTHER', 'UNCLE', 'AUNT', 'CAUSIN', 'OTHER'
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
       -- banner_url (90% có banner)
       CASE
           WHEN RAND() < 0.9
               THEN CONCAT('https://api.dicebear.com/9.x/initials/svg?seed=', d.name)
           ELSE NULL
           END,
       -- slogan (80% có slogan)
       CASE
           WHEN RAND() < 0.8
               THEN ELT(
                   FLOOR(1 + RAND() * 5),
                   'Chăm sóc tận tâm - Dịch vụ chuyên nghiệp',
                   'Sức khỏe của bạn là ưu tiên của chúng tôi',
                   'Điều trị hiệu quả - An toàn tin cậy',
                   'Chất lượng là danh dự - Lòng trắc ẩn là trách nhiệm',
                   'Tin cậy - Chuyên nghiệp - Đổi mới'
                    )
           ELSE NULL
           END
FROM department_data d;

-- Tạo bảng tạm chứa tất cả các cặp (hospital, department)
CREATE TEMPORARY TABLE IF NOT EXISTS temp_hospital_departments AS
SELECT h.hospital_id, d.department_id,
       ROW_NUMBER() OVER () AS pair_row_num -- Đánh số thứ tự cho mỗi cặp
FROM hospitals h
CROSS JOIN departments d;

-- Tạo bảng tạm chứa tất cả user STAFF chưa được phân công
CREATE TEMPORARY TABLE IF NOT EXISTS temp_staff_users AS
SELECT user_id,
       ROW_NUMBER() OVER () AS user_row_num -- Đánh số thứ tự user
FROM users
WHERE user_role = 'STAFF'
  AND user_id NOT IN (SELECT user_id FROM staffs);
select count(*) from temp_staff_users;

-- Ghép cặp (hospital, department) với user duy nhất
CREATE TEMPORARY TABLE IF NOT EXISTS temp_doctor_assignments AS
SELECT hd.hospital_id, hd.department_id, u.user_id
FROM temp_hospital_departments hd
LEFT JOIN temp_staff_users u ON hd.pair_row_num = u.user_row_num -- Ghép 1-1
WHERE u.user_id IS NOT NULL;

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
       FLOOR(5 + RAND() * 3) -- Cấp cao (5-7) cho bác sĩ trưởng phòng
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
INSERT INTO `products` (`product_type`, `name`, `description`, `price`, `unit`, `product_status`, `stock_quantities`, `image_url`, `label`) VALUES
('TEST', 'Complete Blood Count (CBC)', 'Xét nghiệm đánh giá sức khỏe tổng quát và phát hiện các rối loạn khác nhau.', 50.00, 'Test', 'ACTIVE', 100, 'http://example.com/images/cbc.jpg', 'STANDARD'),
('TEST', 'Basic Metabolic Panel (BMP)', 'Nhóm xét nghiệm đo các chất hóa học khác nhau trong máu.', 60.00, 'Test', 'ACTIVE', 80, 'http://example.com/images/bmp.jpg', 'STANDARD'),
('TEST', 'Lipid Profile', 'Xét nghiệm đánh giá nguy cơ mắc bệnh tim mạch.', 70.00, 'Test', 'ACTIVE', 90, 'http://example.com/images/lipid.jpg', 'STANDARD'),
('TEST', 'Liver Function Test (LFT)', 'Xét nghiệm kiểm tra sức khỏe gan.', 55.00, 'Test', 'ACTIVE', 70, 'http://example.com/images/lft.jpg', 'STANDARD'),
('TEST', 'Thyroid Stimulating Hormone (TSH)', 'Xét nghiệm đánh giá chức năng tuyến giáp.', 65.00, 'Test', 'ACTIVE', 85, 'http://example.com/images/tsh.jpg', 'STANDARD'),
('TEST', 'Hemoglobin A1c', 'Xét nghiệm đo lượng đường trong máu trung bình trong 2-3 tháng.', 45.00, 'Test', 'ACTIVE', 60, 'http://example.com/images/a1c.jpg', 'STANDARD'),
('TEST', 'Urinalysis', 'Xét nghiệm nước tiểu để phát hiện các bệnh nhiễm trùng hoặc rối loạn thận.', 30.00, 'Test', 'ACTIVE', 120, 'http://example.com/images/urinalysis.jpg', 'STANDARD'),
('TEST', 'Electrolyte Panel', 'Xét nghiệm đo các chất điện giải trong máu.', 40.00, 'Test', 'ACTIVE', 75, 'http://example.com/images/electrolyte.jpg', 'STANDARD'),
('TEST', 'Renal Function Test', 'Xét nghiệm đánh giá chức năng thận.', 50.00, 'Test', 'ACTIVE', 65, 'http://example.com/images/renal.jpg', 'STANDARD'),
('TEST', 'Prothrombin Time (PT)', 'Xét nghiệm đánh giá khả năng đông máu.', 35.00, 'Test', 'ACTIVE', 55, 'http://example.com/images/pt.jpg', 'STANDARD'),
('TEST', 'Activated Partial Thromboplastin Time (APTT)', 'Xét nghiệm đánh giá thời gian đông máu.', 40.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/aptt.jpg', 'STANDARD'),
('TEST', 'Fasting Blood Sugar (FBS)', 'Xét nghiệm đo lượng đường trong máu sau khi nhịn ăn.', 25.00, 'Test', 'ACTIVE', 100, 'http://example.com/images/fbs.jpg', 'STANDARD'),
('TEST', 'Oral Glucose Tolerance Test (OGTT)', 'Xét nghiệm đánh giá khả năng chuyển hóa glucose.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/ogtt.jpg', 'STANDARD'),
('TEST', 'C-Reactive Protein (CRP)', 'Xét nghiệm đo mức độ viêm trong cơ thể.', 45.00, 'Test', 'ACTIVE', 60, 'http://example.com/images/crp.jpg', 'STANDARD'),
('TEST', 'Erythrocyte Sedimentation Rate (ESR)', 'Xét nghiệm đo tốc độ lắng của hồng cầu.', 30.00, 'Test', 'ACTIVE', 70, 'http://example.com/images/esr.jpg', 'STANDARD'),
('TEST', 'Serum Iron', 'Xét nghiệm đo lượng sắt trong máu.', 35.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/iron.jpg', 'STANDARD'),
('TEST', 'Ferritin', 'Xét nghiệm đo lượng ferritin trong máu.', 40.00, 'Test', 'ACTIVE', 45, 'http://example.com/images/ferritin.jpg', 'STANDARD'),
('TEST', 'Vitamin B12', 'Xét nghiệm đo lượng vitamin B12 trong máu.', 50.00, 'Test', 'ACTIVE', 55, 'http://example.com/images/b12.jpg', 'STANDARD'),
('TEST', 'Folate', 'Xét nghiệm đo lượng folate trong máu.', 45.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/folate.jpg', 'STANDARD'),
('TEST', 'Calcium', 'Xét nghiệm đo lượng canxi trong máu.', 30.00, 'Test', 'ACTIVE', 60, 'http://example.com/images/calcium.jpg', 'STANDARD'),
('TEST', 'Magnesium', 'Xét nghiệm đo lượng magiê trong máu.', 35.00, 'Test', 'ACTIVE', 55, 'http://example.com/images/magnesium.jpg', 'STANDARD'),
('TEST', 'Phosphate', 'Xét nghiệm đo lượng phosphate trong máu.', 30.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/phosphate.jpg', 'STANDARD'),
('TEST', 'Uric Acid', 'Xét nghiệm đo lượng axit uric trong máu.', 25.00, 'Test', 'ACTIVE', 65, 'http://example.com/images/uric.jpg', 'STANDARD'),
('TEST', 'Bilirubin', 'Xét nghiệm đo lượng bilirubin trong máu.', 30.00, 'Test', 'ACTIVE', 60, 'http://example.com/images/bilirubin.jpg', 'STANDARD'),
('TEST', 'Alkaline Phosphatase (ALP)', 'Xét nghiệm đo lượng ALP trong máu.', 35.00, 'Test', 'ACTIVE', 55, 'http://example.com/images/alp.jpg', 'STANDARD'),
('TEST', 'Aspartate Aminotransferase (AST)', 'Xét nghiệm đo lượng AST trong máu.', 30.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/ast.jpg', 'STANDARD'),
('TEST', 'Alanine Aminotransferase (ALT)', 'Xét nghiệm đo lượng ALT trong máu.', 30.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/alt.jpg', 'STANDARD'),
('TEST', 'Gamma-Glutamyl Transferase (GGT)', 'Xét nghiệm đo lượng GGT trong máu.', 35.00, 'Test', 'ACTIVE', 45, 'http://example.com/images/ggt.jpg', 'STANDARD'),
('TEST', 'Lactate Dehydrogenase (LDH)', 'Xét nghiệm đo lượng LDH trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/ldh.jpg', 'STANDARD'),
('TEST', 'Creatine Kinase (CK)', 'Xét nghiệm đo lượng CK trong máu.', 45.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/ck.jpg', 'STANDARD'),
('TEST', 'Troponin', 'Xét nghiệm đo lượng troponin trong máu.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/troponin.jpg', 'STANDARD'),
('TEST', 'B-Type Natriuretic Peptide (BNP)', 'Xét nghiệm đo lượng BNP trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/bnp.jpg', 'STANDARD'),
('TEST', 'D-Dimer', 'Xét nghiệm đo lượng D-Dimer trong máu.', 60.00, 'Test', 'ACTIVE', 20, 'http://example.com/images/ddimer.jpg', 'STANDARD'),
('TEST', 'Fibrinogen', 'Xét nghiệm đo lượng fibrinogen trong máu.', 45.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/fibrinogen.jpg', 'STANDARD'),
('TEST', 'Antithrombin III', 'Xét nghiệm đo lượng antithrombin III trong máu.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/atiii.jpg', 'STANDARD'),
('TEST', 'Protein C', 'Xét nghiệm đo lượng protein C trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/proteinc.jpg', 'STANDARD'),
('TEST', 'Protein S', 'Xét nghiệm đo lượng protein S trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/proteins.jpg', 'STANDARD'),
('TEST', 'Factor V Leiden', 'Xét nghiệm phát hiện đột biến Factor V Leiden.', 60.00, 'Test', 'ACTIVE', 20, 'http://example.com/images/factorv.jpg', 'STANDARD'),
('TEST', 'Homocysteine', 'Xét nghiệm đo lượng homocysteine trong máu.', 45.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/homocysteine.jpg', 'STANDARD'),
('TEST', 'Lipoprotein (a)', 'Xét nghiệm đo lượng lipoprotein (a) trong máu.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/lipa.jpg', 'STANDARD'),
('TEST', 'Apolipoprotein B', 'Xét nghiệm đo lượng apolipoprotein B trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/apob.jpg', 'STANDARD'),
('TEST', 'Apolipoprotein A1', 'Xét nghiệm đo lượng apolipoprotein A1 trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/apoa1.jpg', 'STANDARD'),
('TEST', 'Lipoprotein-Associated Phospholipase A2 (Lp-PLA2)', 'Xét nghiệm đo lượng Lp-PLA2 trong máu.', 60.00, 'Test', 'ACTIVE', 20, 'http://example.com/images/lppla2.jpg', 'STANDARD'),
('TEST', 'High-Sensitivity C-Reactive Protein (hs-CRP)', 'Xét nghiệm đo lượng hs-CRP trong máu.', 45.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/hscrp.jpg', 'STANDARD'),
('TEST', 'Myeloperoxidase (MPO)', 'Xét nghiệm đo lượng MPO trong máu.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/mpo.jpg', 'STANDARD'),
('TEST', 'Oxidized LDL', 'Xét nghiệm đo lượng LDL oxy hóa trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/oxldl.jpg', 'STANDARD'),
('TEST', 'F2-Isoprostanes', 'Xét nghiệm đo lượng F2-isoprostanes trong máu.', 60.00, 'Test', 'ACTIVE', 20, 'http://example.com/images/f2iso.jpg', 'STANDARD'),
('TEST', 'Plasminogen Activator Inhibitor-1 (PAI-1)', 'Xét nghiệm đo lượng PAI-1 trong máu.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/pai1.jpg', 'STANDARD'),
('TEST', 'Tissue Plasminogen Activator (tPA)', 'Xét nghiệm đo lượng tPA trong máu.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/tpa.jpg', 'STANDARD'),
('TEST', 'Von Willebrand Factor (vWF)', 'Xét nghiệm đo lượng vWF trong máu.', 45.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/vwf.jpg', 'STANDARD'),
('TEST', 'Factor VIII', 'Xét nghiệm đo lượng factor VIII trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factorviii.jpg', 'STANDARD'),
('TEST', 'Factor IX', 'Xét nghiệm đo lượng factor IX trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factorix.jpg', 'STANDARD'),
('TEST', 'Factor X', 'Xét nghiệm đo lượng factor X trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factorx.jpg', 'STANDARD'),
('TEST', 'Factor XI', 'Xét nghiệm đo lượng factor XI trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factorxi.jpg', 'STANDARD'),
('TEST', 'Factor XII', 'Xét nghiệm đo lượng factor XII trong máu.', 40.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factorxii.jpg', 'STANDARD'),
('TEST', 'Antiphospholipid Antibodies', 'Xét nghiệm phát hiện kháng thể antiphospholipid.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/antiphospholipid.jpg', 'STANDARD'),
('TEST', 'Lupus Anticoagulant', 'Xét nghiệm phát hiện lupus anticoagulant.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/lupus.jpg', 'STANDARD'),
('TEST', 'Anti-Cardiolipin Antibodies', 'Xét nghiệm phát hiện kháng thể anti-cardiolipin.', 50.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/anticardiolipin.jpg', 'STANDARD'),
('TEST', 'Anti-Beta2 Glycoprotein I Antibodies', 'Xét nghiệm phát hiện kháng thể anti-beta2 glycoprotein I.', 55.00, 'Test', 'ACTIVE', 25, 'http://example.com/images/antibeta2.jpg', 'STANDARD'),
('TEST', 'Rheumatoid Factor (RF)', 'Xét nghiệm phát hiện yếu tố dạng thấp.', 40.00, 'Test', 'ACTIVE', 50, 'http://example.com/images/rf.jpg', 'STANDARD'),
('TEST', 'Anti-Nuclear Antibody (ANA)', 'Xét nghiệm phát hiện kháng thể kháng nhân.', 45.00, 'Test', 'ACTIVE', 45, 'http://example.com/images/ana.jpg', 'STANDARD'),
('TEST', 'Anti-Double Stranded DNA (dsDNA)', 'Xét nghiệm phát hiện kháng thể anti-dsDNA.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/dsdna.jpg', 'STANDARD'),
('TEST', 'Anti-Smith Antibody', 'Xét nghiệm phát hiện kháng thể anti-Smith.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/smith.jpg', 'STANDARD'),
('TEST', 'Anti-Ro/SSA Antibody', 'Xét nghiệm phát hiện kháng thể anti-Ro/SSA.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/ro.jpg', 'STANDARD'),
('TEST', 'Anti-La/SSB Antibody', 'Xét nghiệm phát hiện kháng thể anti-La/SSB.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/la.jpg', 'STANDARD'),
('TEST', 'Anti-Centromere Antibody', 'Xét nghiệm phát hiện kháng thể anti-centromere.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/centromere.jpg', 'STANDARD'),
('TEST', 'Anti-Scl-70 Antibody', 'Xét nghiệm phát hiện kháng thể anti-Scl-70.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/scl70.jpg', 'STANDARD'),
('TEST', 'Anti-Jo-1 Antibody', 'Xét nghiệm phát hiện kháng thể anti-Jo-1.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/jo1.jpg', 'STANDARD'),
('TEST', 'Anti-RNP Antibody', 'Xét nghiệm phát hiện kháng thể anti-RNP.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/rnp.jpg', 'STANDARD'),
('TEST', 'Anti-Histone Antibody', 'Xét nghiệm phát hiện kháng thể anti-histone.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/histone.jpg', 'STANDARD'),
('TEST', 'Anti-CCP Antibody', 'Xét nghiệm phát hiện kháng thể anti-CCP.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/ccp.jpg', 'STANDARD'),
('TEST', 'Anti-MPO Antibody', 'Xét nghiệm phát hiện kháng thể anti-MPO.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/mpoab.jpg', 'STANDARD'),
('TEST', 'Anti-PR3 Antibody', 'Xét nghiệm phát hiện kháng thể anti-PR3.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/pr3.jpg', 'STANDARD'),
('TEST', 'Anti-GBM Antibody', 'Xét nghiệm phát hiện kháng thể anti-GBM.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/gbm.jpg', 'STANDARD'),
('TEST', 'Anti-Thyroglobulin Antibody', 'Xét nghiệm phát hiện kháng thể anti-thyroglobulin.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/thyroglobulin.jpg', 'STANDARD'),
('TEST', 'Anti-Thyroid Peroxidase Antibody (TPO)', 'Xét nghiệm phát hiện kháng thể anti-TPO.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/tpo.jpg', 'STANDARD'),
('TEST', 'Anti-TSH Receptor Antibody', 'Xét nghiệm phát hiện kháng thể anti-TSH receptor.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/tshreceptor.jpg', 'STANDARD'),
('TEST', 'Anti-Islet Cell Antibody', 'Xét nghiệm phát hiện kháng thể anti-islet cell.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/islet.jpg', 'STANDARD'),
('TEST', 'Anti-Insulin Antibody', 'Xét nghiệm phát hiện kháng thể anti-insulin.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/insulinab.jpg', 'STANDARD'),
('TEST', 'Anti-GAD Antibody', 'Xét nghiệm phát hiện kháng thể anti-GAD.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/gad.jpg', 'STANDARD'),
('TEST', 'Anti-IA2 Antibody', 'Xét nghiệm phát hiện kháng thể anti-IA2.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/ia2.jpg', 'STANDARD'),
('TEST', 'Anti-ZnT8 Antibody', 'Xét nghiệm phát hiện kháng thể anti-ZnT8.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/znt8.jpg', 'STANDARD'),
('TEST', 'Anti-Parietal Cell Antibody', 'Xét nghiệm phát hiện kháng thể anti-parietal cell.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/parietal.jpg', 'STANDARD'),
('TEST', 'Anti-Intrinsic Factor Antibody', 'Xét nghiệm phát hiện kháng thể anti-intrinsic factor.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/intrinsic.jpg', 'STANDARD'),
('TEST', 'Anti-Smooth Muscle Antibody', 'Xét nghiệm phát hiện kháng thể anti-smooth muscle.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/smoothmuscle.jpg', 'STANDARD'),
('TEST', 'Anti-Mitochondrial Antibody', 'Xét nghiệm phát hiện kháng thể anti-mitochondrial.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/mitochondrial.jpg', 'STANDARD'),
('TEST', 'Anti-Liver Kidney Microsomal Antibody', 'Xét nghiệm phát hiện kháng thể anti-LKM.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/lkm.jpg', 'STANDARD'),
('TEST', 'Anti-Actin Antibody', 'Xét nghiệm phát hiện kháng thể anti-actin.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/actin.jpg', 'STANDARD'),
('TEST', 'Anti-Soluble Liver Antigen Antibody', 'Xét nghiệm phát hiện kháng thể anti-SLA.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/sla.jpg', 'STANDARD'),
('TEST', 'Anti-F-Actin Antibody', 'Xét nghiệm phát hiện kháng thể anti-F-actin.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/factin.jpg', 'STANDARD'),
('TEST', 'Anti-Glutamic Acid Decarboxylase Antibody', 'Xét nghiệm phát hiện kháng thể anti-GAD.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/gadab.jpg', 'STANDARD'),
('TEST', 'Anti-Tissue Transglutaminase Antibody', 'Xét nghiệm phát hiện kháng thể anti-tTG.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/ttg.jpg', 'STANDARD'),
('TEST', 'Anti-Endomysial Antibody', 'Xét nghiệm phát hiện kháng thể anti-endomysial.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/endomysial.jpg', 'STANDARD'),
('TEST', 'Anti-Gliadin Antibody', 'Xét nghiệm phát hiện kháng thể anti-gliadin.', 50.00, 'Test', 'ACTIVE', 40, 'http://example.com/images/gliadin.jpg', 'STANDARD'),
('TEST', 'Anti-Reticulin Antibody', 'Xét nghiệm phát hiện kháng thể anti-reticulin.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/reticulin.jpg', 'STANDARD'),
('TEST', 'Anti-Saccharomyces cerevisiae Antibody', 'Xét nghiệm phát hiện kháng thể anti-Saccharomyces cerevisiae.', 60.00, 'Test', 'ACTIVE', 30, 'http://example.com/images/saccharomyces.jpg', 'STANDARD'),
('TEST', 'Anti-Neutrophil Cytoplasmic Antibody (ANCA)', 'Xét nghiệm phát hiện kháng thể ANCA.', 55.00, 'Test', 'ACTIVE', 35, 'http://example.com/images/anca.jpg', 'STANDARD'),
('TEST', 'Vitamin D Test', 'Xét nghiệm đo mức vitamin D trong máu.', 75.00, 'Test', 'ACTIVE', 95, 'http://example.com/images/vitd.jpg', 'STANDARD');

-- Tạo dữ liệu cho bảng `test_types`
INSERT INTO `test_types` (`test_type_name`, `product_id`) VALUES
('Complete Blood Count (CBC)', 1),
('Basic Metabolic Panel (BMP)', 2),
('Lipid Profile', 3),
('Liver Function Test (LFT)', 4),
('Thyroid Stimulating Hormone (TSH)', 5),
('Hemoglobin A1c', 6),
('Urinalysis', 7),
('Electrolyte Panel', 8),
('Renal Function Test', 9),
('Prothrombin Time (PT)', 10),
('Activated Partial Thromboplastin Time (APTT)', 11),
('Fasting Blood Sugar (FBS)', 12),
('Oral Glucose Tolerance Test (OGTT)', 13),
('C-Reactive Protein (CRP)', 14),
('Erythrocyte Sedimentation Rate (ESR)', 15),
('Serum Iron', 16),
('Ferritin', 17),
('Vitamin B12', 18),
('Folate', 19),
('Calcium', 20),
('Magnesium', 21),
('Phosphate', 22),
('Uric Acid', 23),
('Bilirubin', 24),
('Alkaline Phosphatase (ALP)', 25),
('Aspartate Aminotransferase (AST)', 26),
('Alanine Aminotransferase (ALT)', 27),
('Gamma-Glutamyl Transferase (GGT)', 28),
('Lactate Dehydrogenase (LDH)', 29),
('Creatine Kinase (CK)', 30),
('Troponin', 31),
('B-Type Natriuretic Peptide (BNP)', 32),
('D-Dimer', 33),
('Fibrinogen', 34),
('Antithrombin III', 35),
('Protein C', 36),
('Protein S', 37),
('Factor V Leiden', 38),
('Homocysteine', 39),
('Lipoprotein (a)', 40),
('Apolipoprotein B', 41),
('Apolipoprotein A1', 42),
('Lipoprotein-Associated Phospholipase A2 (Lp-PLA2)', 43),
('High-Sensitivity C-Reactive Protein (hs-CRP)', 44),
('Myeloperoxidase (MPO)', 45),
('Oxidized LDL', 46),
('F2-Isoprostanes', 47),
('Plasminogen Activator Inhibitor-1 (PAI-1)', 48),
('Tissue Plasminogen Activator (tPA)', 49),
('Von Willebrand Factor (vWF)', 50),
('Factor VIII', 51),
('Factor IX', 52),
('Factor X', 53),
('Factor XI', 54),
('Factor XII', 55),
('Antiphospholipid Antibodies', 56),
('Lupus Anticoagulant', 57),
('Anti-Cardiolipin Antibodies', 58),
('Anti-Beta2 Glycoprotein I Antibodies', 59),
('Rheumatoid Factor (RF)', 60),
('Anti-Nuclear Antibody (ANA)', 61),
('Anti-Double Stranded DNA (dsDNA)', 62),
('Anti-Smith Antibody', 63),
('Anti-Ro/SSA Antibody', 64),
('Anti-La/SSB Antibody', 65),
('Anti-Centromere Antibody', 66),
('Anti-Scl-70 Antibody', 67),
('Anti-Jo-1 Antibody', 68),
('Anti-RNP Antibody', 69),
('Anti-Histone Antibody', 70),
('Anti-CCP Antibody', 71),
('Anti-MPO Antibody', 72),
('Anti-PR3 Antibody', 73),
('Anti-GBM Antibody', 74),
('Anti-Thyroglobulin Antibody', 75),
('Anti-Thyroid Peroxidase Antibody (TPO)', 76),
('Anti-TSH Receptor Antibody', 77),
('Anti-Islet Cell Antibody', 78),
('Anti-Insulin Antibody', 79),
('Anti-GAD Antibody', 80),
('Anti-IA2 Antibody', 81),
('Anti-ZnT8 Antibody', 82),
('Anti-Parietal Cell Antibody', 83),
('Anti-Intrinsic Factor Antibody', 84),
('Anti-Smooth Muscle Antibody', 85),
('Anti-Mitochondrial Antibody', 86),
('Anti-Liver Kidney Microsomal Antibody', 87),
('Anti-Actin Antibody', 88),
('Anti-Soluble Liver Antigen Antibody', 89),
('Anti-F-Actin Antibody', 90),
('Anti-Glutamic Acid Decarboxylase Antibody', 91),
('Anti-Tissue Transglutaminase Antibody', 92),
('Anti-Endomysial Antibody', 93),
('Anti-Gliadin Antibody', 94),
('Anti-Reticulin Antibody', 95),
('Anti-Saccharomyces cerevisiae Antibody', 96),
('Anti-Neutrophil Cytoplasmic Antibody (ANCA)', 97),
('Vitamin D Test', 100);

-- Tạo dữ liệu cho bảng `test_items`
-- Test items cho Complete Blood Count (CBC) - test_type_id = 1
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('Hemoglobin', 'g/dL', 12.0, 16.0, 1),
('Hematocrit', '%', 36.0, 48.0, 1),
('Red Blood Cell Count', 'million cells/mcL', 4.0, 5.5, 1),
('White Blood Cell Count', 'thousand cells/mcL', 4.5, 11.0, 1),
('Platelet Count', 'thousand cells/mcL', 150.0, 450.0, 1);

-- Test items cho Basic Metabolic Panel (BMP) - test_type_id = 2
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('Glucose', 'mg/dL', 70.0, 100.0, 2),
('Calcium', 'mg/dL', 8.5, 10.2, 2),
('Sodium', 'mEq/L', 135.0, 145.0, 2),
('Potassium', 'mEq/L', 3.5, 5.0, 2);

-- Test items cho Lipid Profile - test_type_id = 3
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('Total Cholesterol', 'mg/dL', 0.0, 200.0, 3),
('HDL Cholesterol', 'mg/dL', 40.0, 60.0, 3),
('LDL Cholesterol', 'mg/dL', 0.0, 100.0, 3),
('Triglycerides', 'mg/dL', 0.0, 150.0, 3);

-- Test items cho Liver Function Test (LFT) - test_type_id = 4
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('ALT', 'U/L', 7.0, 56.0, 4),
('AST', 'U/L', 10.0, 40.0, 4),
('ALP', 'U/L', 44.0, 147.0, 4),
('Bilirubin', 'mg/dL', 0.1, 1.0, 4);

-- Test items cho Thyroid Stimulating Hormone (TSH) - test_type_id = 5
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('TSH', 'mIU/L', 0.4, 4.0, 5);

-- Test items cho các test_type khác (từ 6 đến 100) được tạo đơn giản hơn
INSERT INTO `test_items` (`name`, `unit`, `ref_min`, `ref_max`, `test_type_id`) VALUES
('Hemoglobin A1c', '%', 4.0, 5.6, 6),
('Urine pH', '', 4.5, 8.0, 7),
('Sodium', 'mEq/L', 135.0, 145.0, 8),
('Creatinine', 'mg/dL', 0.6, 1.2, 9),
('PT', 'seconds', 11.0, 13.5, 10),
('APTT', 'seconds', 25.0, 35.0, 11),
('Glucose', 'mg/dL', 70.0, 100.0, 12),
('Glucose (2h)', 'mg/dL', 70.0, 140.0, 13),
('CRP', 'mg/L', 0.0, 10.0, 14),
('ESR', 'mm/h', 0.0, 20.0, 15),
('Iron', 'µg/dL', 60.0, 170.0, 16),
('Ferritin', 'ng/mL', 20.0, 250.0, 17),
('Vitamin B12', 'pg/mL', 200.0, 900.0, 18),
('Folate', 'ng/mL', 3.0, 17.0, 19),
('Calcium', 'mg/dL', 8.5, 10.2, 20),
('Magnesium', 'mg/dL', 1.7, 2.2, 21),
('Phosphate', 'mg/dL', 2.5, 4.5, 22),
('Uric Acid', 'mg/dL', 3.4, 7.0, 23),
('Bilirubin', 'mg/dL', 0.1, 1.0, 24),
('ALP', 'U/L', 44.0, 147.0, 25),
('AST', 'U/L', 10.0, 40.0, 26),
('ALT', 'U/L', 7.0, 56.0, 27),
('GGT', 'U/L', 9.0, 48.0, 28),
('LDH', 'U/L', 140.0, 280.0, 29),
('CK', 'U/L', 38.0, 174.0, 30),
('Troponin', 'ng/mL', 0.0, 0.04, 31),
('BNP', 'pg/mL', 0.0, 100.0, 32),
('D-Dimer', 'µg/mL', 0.0, 0.5, 33),
('Fibrinogen', 'mg/dL', 200.0, 400.0, 34),
('Antithrombin III', '%', 80.0, 120.0, 35),
('Protein C', '%', 70.0, 140.0, 36),
('Protein S', '%', 60.0, 130.0, 37),
('Factor V Leiden', '', 0.0, 0.0, 38),
('Homocysteine', 'µmol/L', 5.0, 15.0, 39),
('Lipoprotein (a)', 'mg/dL', 0.0, 30.0, 40),
('Apolipoprotein B', 'mg/dL', 50.0, 110.0, 41),
('Apolipoprotein A1', 'mg/dL', 110.0, 205.0, 42),
('Lp-PLA2', 'ng/mL', 0.0, 200.0, 43),
('hs-CRP', 'mg/L', 0.0, 3.0, 44),
('MPO', 'pmol/L', 0.0, 70.0, 45),
('Oxidized LDL', 'U/L', 0.0, 60.0, 46),
('F2-Isoprostanes', 'pg/mL', 0.0, 50.0, 47),
('PAI-1', 'ng/mL', 4.0, 43.0, 48),
('tPA', 'ng/mL', 0.0, 10.0, 49),
('vWF', '%', 50.0, 150.0, 50),
('Factor VIII', '%', 50.0, 150.0, 51),
('Factor IX', '%', 60.0, 140.0, 52),
('Factor X', '%', 70.0, 130.0, 53),
('Factor XI', '%', 60.0, 140.0, 54),
('Factor XII', '%', 50.0, 150.0, 55),
('Antiphospholipid Antibodies', 'GPL', 0.0, 15.0, 56),
('Lupus Anticoagulant', '', 0.0, 1.2, 57),
('Anti-Cardiolipin Antibodies', 'GPL', 0.0, 20.0, 58),
('Anti-Beta2 Glycoprotein I Antibodies', 'U/mL', 0.0, 20.0, 59),
('RF', 'IU/mL', 0.0, 14.0, 60),
('ANA', '', 0.0, 0.0, 61),
('dsDNA', 'IU/mL', 0.0, 30.0, 62),
('Anti-Smith Antibody', 'U/mL', 0.0, 25.0, 63),
('Anti-Ro/SSA Antibody', 'U/mL', 0.0, 25.0, 64),
('Anti-La/SSB Antibody', 'U/mL', 0.0, 25.0, 65),
('Anti-Centromere Antibody', 'U/mL', 0.0, 25.0, 66),
('Anti-Scl-70 Antibody', 'U/mL', 0.0, 25.0, 67),
('Anti-Jo-1 Antibody', 'U/mL', 0.0, 25.0, 68),
('Anti-RNP Antibody', 'U/mL', 0.0, 25.0, 69),
('Anti-Histone Antibody', 'U/mL', 0.0, 25.0, 70),
('Anti-CCP Antibody', 'U/mL', 0.0, 20.0, 71),
('Anti-MPO Antibody', 'U/mL', 0.0, 20.0, 72),
('Anti-PR3 Antibody', 'U/mL', 0.0, 20.0, 73),
('Anti-GBM Antibody', 'U/mL', 0.0, 20.0, 74),
('Anti-Thyroglobulin Antibody', 'IU/mL', 0.0, 40.0, 75),
('Anti-TPO Antibody', 'IU/mL', 0.0, 35.0, 76),
('Anti-TSH Receptor Antibody', 'IU/L', 0.0, 1.8, 77),
('Anti-Islet Cell Antibody', '', 0.0, 0.0, 78),
('Anti-Insulin Antibody', 'µU/mL', 0.0, 25.0, 79),
('Anti-GAD Antibody', 'U/mL', 0.0, 30.0, 80),
('Anti-IA2 Antibody', 'U/mL', 0.0, 30.0, 81),
('Anti-ZnT8 Antibody', 'U/mL', 0.0, 30.0, 82),
('Anti-Parietal Cell Antibody', '', 0.0, 0.0, 83),
('Anti-Intrinsic Factor Antibody', '', 0.0, 0.0, 84),
('Anti-Smooth Muscle Antibody', '', 0.0, 0.0, 85),
('Anti-Mitochondrial Antibody', '', 0.0, 0.0, 86),
('Anti-LKM Antibody', '', 0.0, 0.0, 87),
('Anti-Actin Antibody', '', 0.0, 0.0, 88),
('Anti-SLA Antibody', '', 0.0, 0.0, 89),
('Anti-F-Actin Antibody', '', 0.0, 0.0, 90),
('Anti-GAD Antibody', 'U/mL', 0.0, 30.0, 91),
('Anti-tTG Antibody', 'U/mL', 0.0, 20.0, 92),
('Anti-Endomysial Antibody', '', 0.0, 0.0, 93),
('Anti-Gliadin Antibody', 'U/mL', 0.0, 20.0, 94),
('Anti-Reticulin Antibody', '', 0.0, 0.0, 95),
('Anti-Saccharomyces cerevisiae Antibody', 'U/mL', 0.0, 20.0, 96),
('ANCA', '', 0.0, 0.0, 97),
('Vitamin D', 'ng/mL', 20.0, 50.0, 100);

-- Tạo dữ liệu cho bảng `reference_ranges`
-- Reference ranges cho Hemoglobin (test_item_id = 1)
INSERT INTO `reference_ranges` (`test_item_id`, `age_min`, `age_max`, `min_value`, `max_value`, `suspected_condition`) VALUES
(1, 0, 18, 11.0, 15.0, 'Thiếu máu nếu dưới min_value'),
(1, 19, 60, 12.0, 16.0, 'Thiếu máu nếu dưới min_value'),
(1, 61, 120, 11.5, 15.5, 'Thiếu máu nếu dưới min_value');

-- Reference ranges cho Glucose (test_item_id = 6)
INSERT INTO `reference_ranges` (`test_item_id`, `age_min`, `age_max`, `min_value`, `max_value`, `suspected_condition`) VALUES
(6, 0, 18, 60.0, 100.0, 'Hạ đường huyết nếu dưới min_value, Tăng đường huyết nếu trên max_value'),
(6, 19, 60, 70.0, 100.0, 'Hạ đường huyết nếu dưới min_value, Tăng đường huyết nếu trên max_value'),
(6, 61, 120, 70.0, 110.0, 'Hạ đường huyết nếu dưới min_value, Tăng đường huyết nếu trên max_value');

-- Reference ranges cho Total Cholesterol (test_item_id = 10)
INSERT INTO `reference_ranges` (`test_item_id`, `age_min`, `age_max`, `min_value`, `max_value`, `suspected_condition`) VALUES
(10, 0, 18, 0.0, 170.0, 'Nguy cơ tim mạch nếu trên max_value'),
(10, 19, 60, 0.0, 200.0, 'Nguy cơ tim mạch nếu trên max_value'),
(10, 61, 120, 0.0, 200.0, 'Nguy cơ tim mạch nếu trên max_value');

-- Reference ranges cho các test_item khác (từ 2 đến 100) được tạo đơn giản hơn
INSERT INTO `reference_ranges` (`test_item_id`, `age_min`, `age_max`, `min_value`, `max_value`, `suspected_condition`) VALUES
(2, 0, 120, 36.0, 48.0, 'Thiếu máu nếu dưới min_value'),
(3, 0, 120, 4.0, 5.5, 'Thiếu máu nếu dưới min_value'),
(4, 0, 120, 4.5, 11.0, 'Nhiễm trùng nếu trên max_value'),
(5, 0, 120, 150.0, 450.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(7, 0, 120, 8.5, 10.2, 'Rối loạn canxi nếu ngoài khoảng'),
(8, 0, 120, 135.0, 145.0, 'Rối loạn điện giải nếu ngoài khoảng'),
(9, 0, 120, 3.5, 5.0, 'Rối loạn kali nếu ngoài khoảng'),
(11, 0, 120, 40.0, 60.0, 'Nguy cơ tim mạch nếu dưới min_value'),
(12, 0, 120, 0.0, 100.0, 'Nguy cơ tim mạch nếu trên max_value'),
(13, 0, 120, 0.0, 150.0, 'Nguy cơ tim mạch nếu trên max_value'),
(14, 0, 120, 7.0, 56.0, 'Tổn thương gan nếu trên max_value'),
(15, 0, 120, 10.0, 40.0, 'Tổn thương gan nếu trên max_value'),
(16, 0, 120, 44.0, 147.0, 'Tổn thương gan nếu trên max_value'),
(17, 0, 120, 0.1, 1.0, 'Tổn thương gan nếu trên max_value'),
(18, 0, 120, 0.4, 4.0, 'Rối loạn tuyến giáp nếu ngoài khoảng'),
(19, 0, 120, 4.0, 5.6, 'Tiểu đường nếu trên max_value'),
(20, 0, 120, 4.5, 8.0, 'Rối loạn thận nếu ngoài khoảng'),
(21, 0, 120, 135.0, 145.0, 'Rối loạn điện giải nếu ngoài khoảng'),
(22, 0, 120, 0.6, 1.2, 'Suy thận nếu trên max_value'),
(23, 0, 120, 11.0, 13.5, 'Rối loạn đông máu nếu ngoài khoảng'),
(24, 0, 120, 25.0, 35.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(25, 0, 120, 70.0, 100.0, 'Tiểu đường nếu trên max_value'),
(26, 0, 120, 70.0, 140.0, 'Tiểu đường nếu trên max_value'),
(27, 0, 120, 0.0, 10.0, 'Viêm nếu trên max_value'),
(28, 0, 120, 0.0, 20.0, 'Viêm nếu trên max_value'),
(29, 0, 120, 60.0, 170.0, 'Thiếu sắt nếu dưới min_value'),
(30, 0, 120, 20.0, 250.0, 'Thiếu sắt nếu dưới min_value'),
(31, 0, 120, 200.0, 900.0, 'Thiếu B12 nếu dưới min_value'),
(32, 0, 120, 3.0, 17.0, 'Thiếu folate nếu dưới min_value'),
(33, 0, 120, 8.5, 10.2, 'Rối loạn canxi nếu ngoài khoảng'),
(34, 0, 120, 1.7, 2.2, 'Rối loạn magiê nếu ngoài khoảng'),
(35, 0, 120, 2.5, 4.5, 'Rối loạn phosphate nếu ngoài khoảng'),
(36, 0, 120, 3.4, 7.0, 'Gút nếu trên max_value'),
(37, 0, 120, 0.1, 1.0, 'Tổn thương gan nếu trên max_value'),
(38, 0, 120, 44.0, 147.0, 'Tổn thương gan nếu trên max_value'),
(39, 0, 120, 10.0, 40.0, 'Tổn thương gan nếu trên max_value'),
(40, 0, 120, 7.0, 56.0, 'Tổn thương gan nếu trên max_value'),
(41, 0, 120, 9.0, 48.0, 'Tổn thương gan nếu trên max_value'),
(42, 0, 120, 140.0, 280.0, 'Tổn thương cơ nếu trên max_value'),
(43, 0, 120, 38.0, 174.0, 'Tổn thương cơ nếu trên max_value'),
(44, 0, 120, 0.0, 0.04, 'Nhồi máu cơ tim nếu trên max_value'),
(45, 0, 120, 0.0, 100.0, 'Suy tim nếu trên max_value'),
(46, 0, 120, 0.0, 0.5, 'Huyết khối nếu trên max_value'),
(47, 0, 120, 200.0, 400.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(48, 0, 120, 80.0, 120.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(49, 0, 120, 70.0, 140.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(50, 0, 120, 60.0, 130.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(51, 0, 120, 0.0, 0.0, 'Đột biến gen nếu khác 0'),
(52, 0, 120, 5.0, 15.0, 'Nguy cơ tim mạch nếu trên max_value'),
(53, 0, 120, 0.0, 30.0, 'Nguy cơ tim mạch nếu trên max_value'),
(54, 0, 120, 50.0, 110.0, 'Nguy cơ tim mạch nếu trên max_value'),
(55, 0, 120, 110.0, 205.0, 'Nguy cơ tim mạch nếu dưới min_value'),
(56, 0, 120, 0.0, 200.0, 'Nguy cơ tim mạch nếu trên max_value'),
(57, 0, 120, 0.0, 3.0, 'Viêm nếu trên max_value'),
(58, 0, 120, 0.0, 70.0, 'Nguy cơ tim mạch nếu trên max_value'),
(59, 0, 120, 0.0, 60.0, 'Nguy cơ tim mạch nếu trên max_value'),
(60, 0, 120, 0.0, 50.0, 'Nguy cơ tim mạch nếu trên max_value'),
(61, 0, 120, 4.0, 43.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(62, 0, 120, 0.0, 10.0, 'Rối loạn đông máu nếu trên max_value'),
(63, 0, 120, 50.0, 150.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(64, 0, 120, 50.0, 150.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(65, 0, 120, 60.0, 140.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(66, 0, 120, 70.0, 130.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(67, 0, 120, 60.0, 140.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(68, 0, 120, 50.0, 150.0, 'Rối loạn đông máu nếu ngoài khoảng'),
(69, 0, 120, 0.0, 15.0, 'Bệnh tự miễn nếu trên max_value'),
(70, 0, 120, 0.0, 1.2, 'Bệnh tự miễn nếu trên max_value'),
(71, 0, 120, 0.0, 20.0, 'Bệnh tự miễn nếu trên max_value'),
(72, 0, 120, 0.0, 20.0, 'Bệnh tự miễn nếu trên max_value'),
(73, 0, 120, 0.0, 14.0, 'Viêm khớp dạng thấp nếu trên max_value'),
(74, 0, 120, 0.0, 0.0, 'Bệnh tự miễn nếu khác 0'),
(75, 0, 120, 0.0, 30.0, 'Bệnh tự miễn nếu trên max_value'),
(76, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(77, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(78, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(79, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(80, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(81, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(82, 0, 120, 0.0, 25.0, 'Bệnh tự miễn nếu trên max_value'),
(83, 0, 120, 0.0, 20.0, 'Viêm khớp dạng thấp nếu trên max_value'),
(84, 0, 120, 0.0, 20.0, 'Viêm mạch nếu trên max_value'),
(85, 0, 120, 0.0, 20.0, 'Viêm mạch nếu trên max_value'),
(86, 0, 120, 0.0, 20.0, 'Bệnh thận nếu trên max_value'),
(87, 0, 120, 0.0, 40.0, 'Bệnh tuyến giáp nếu trên max_value'),
(88, 0, 120, 0.0, 35.0, 'Bệnh tuyến giáp nếu trên max_value'),
(89, 0, 120, 0.0, 1.8, 'Bệnh tuyến giáp nếu trên max_value'),
(90, 0, 120, 0.0, 0.0, 'Tiểu đường type 1 nếu khác 0'),
(91, 0, 120, 0.0, 25.0, 'Tiểu đường type 1 nếu trên max_value'),
(92, 0, 120, 0.0, 30.0, 'Tiểu đường type 1 nếu trên max_value'),
(93, 0, 120, 0.0, 30.0, 'Tiểu đường type 1 nếu trên max_value'),
(94, 0, 120, 0.0, 30.0, 'Tiểu đường type 1 nếu trên max_value'),
(95, 0, 120, 0.0, 0.0, 'Thiếu máu ác tính nếu khác 0'),
(96, 0, 120, 0.0, 0.0, 'Thiếu máu ác tính nếu khác 0'),
(97, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(98, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(99, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(100, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(101, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(102, 0, 120, 0.0, 0.0, 'Bệnh gan tự miễn nếu khác 0'),
(103, 0, 120, 0.0, 30.0, 'Tiểu đường type 1 nếu trên max_value'),
(104, 0, 120, 0.0, 20.0, 'Bệnh celiac nếu trên max_value'),
(105, 0, 120, 0.0, 0.0, 'Bệnh celiac nếu khác 0'),
(106, 0, 120, 0.0, 20.0, 'Bệnh celiac nếu trên max_value'),
(107, 0, 120, 0.0, 0.0, 'Bệnh celiac nếu khác 0'),
(108, 0, 120, 0.0, 20.0, 'Bệnh viêm ruột nếu trên max_value'),
(109, 0, 120, 0.0, 0.0, 'Viêm mạch nếu khác 0'),
(110, 0, 120, 20.0, 50.0, 'Thiếu vitamin D nếu dưới min_value');

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
    pd.patient_id,
    pd.appointment_id,
    pd.admission_date,
    pd.discharge_date,
    pd.main_complaint,

    -- diagnosis dựa vào main_complaint
    CASE pd.main_complaint
        WHEN 'Severe persistent headache'
            THEN 'Migraine headache: common type, episodic, moderate severity. Group II according to international classification.'
        WHEN 'Sore throat with high fever'
            THEN 'Acute tonsillitis due to Streptococcus, with persistent fever >38.5°C, purulent exudate on tonsillar surface.'
        WHEN 'Upper abdominal pain'
            THEN 'Acute gastritis: gastric mucosa congestion, mild gastroesophageal reflux present.'
        WHEN 'Persistent cough with difficulty breathing'
            THEN 'Acute bronchitis: increased sputum production, airway edema, no signs of pneumonia.'
        WHEN 'Knee joint pain during movement'
            THEN 'Primary knee osteoarthritis, grade II cartilage damage, small osteophyte formation.'
        WHEN 'Dizziness and nausea'
            THEN 'Peripheral vestibular disorder: benign paroxysmal positional vertigo (BPPV), right semicircular canal.'
        WHEN 'Prolonged fatigue of unknown cause'
            THEN 'Chronic fatigue syndrome, no organic cause identified, prolonged psychological stress factors present.'
        WHEN 'Skin rash with itching'
            THEN 'Allergic contact dermatitis, type IV reaction, with erythema, itching, scaling in contact areas.'
        WHEN 'Left chest pain radiating to shoulder'
            THEN 'Unstable angina, suspected myocardial ischemia, further cardiac function assessment needed.'
        WHEN 'Chronic sleep disorder'
            THEN 'Primary sleep disorder: initial insomnia, difficulty maintaining sleep, early waking. Moderate severity.'
        WHEN 'Unexplained weight loss'
            THEN 'Physical asthenia, mild nutritional deficiency, suspected vitamin D and iron deficiency.'
        WHEN 'Chronic lower back pain'
            THEN 'Lumbar disc herniation L4-L5, mild nerve root compression, no motor function impairment.'
        WHEN 'Diarrhea and abdominal pain'
            THEN 'Irritable bowel syndrome, diarrhea-predominant, related to psychological stress factors.'
        WHEN 'Shortness of breath during exertion'
            THEN 'Chronic obstructive pulmonary disease (COPD) stage II, mild respiratory function impairment, FEV1/FVC < 70%, FEV1 60% predicted.'
        ELSE
            'Serous otitis media, intact tympanic membrane, middle ear fluid effusion, conductive hearing loss of 30dB.'
    END AS diagnosis,

    -- treatment_plan tương ứng
    CASE pd.main_complaint
        WHEN 'Severe persistent headache'
            THEN 'Sumatriptan 50mg for acute attacks. Propranolol 40mg daily for prevention. Avoid triggers. Follow-up in 4 weeks.'
        WHEN 'Sore throat with high fever'
            THEN 'Amoxicillin 500mg three times daily for 7 days. Paracetamol 500mg for fever >38.5°C. Salt water gargles. Rest and hydration.'
        WHEN 'Upper abdominal pain'
            THEN 'Omeprazole 20mg once daily before breakfast for 14 days. Light diet, avoid spicy foods, caffeine, alcohol. Follow-up in 2 weeks.'
        WHEN 'Persistent cough with difficulty breathing'
            THEN 'Ambroxol 30mg three times daily. Salbutamol inhaler as needed. Antibiotics if bacterial infection suspected. Increased fluid intake, rest.'
        WHEN 'Knee joint pain during movement'
            THEN 'Glucosamine sulfate 1500mg daily for 3 months. Physical therapy twice weekly for 1 month. Weight reduction, gentle exercise. Follow-up in 1 month.'
        WHEN 'Dizziness and nausea'
            THEN 'Epley maneuver exercises three times daily. Betahistine 16mg three times daily for 14 days. Avoid sudden position changes. Follow-up in 2 weeks.'
        WHEN 'Prolonged fatigue of unknown cause'
            THEN 'Work-rest schedule adjustment. Multivitamin and mineral supplements. Psychological counseling. Light exercise. Tests to exclude organic causes.'
        WHEN 'Skin rash with itching'
            THEN 'Methylprednisolone 4mg with tapering dose over 7 days. Loratadine 10mg each morning. Mometasone 0.1% cream twice daily. Avoid allergen contact. Follow-up in 1 week.'
        WHEN 'Left chest pain radiating to shoulder'
            THEN 'Refer to Cardiology. Aspirin 81mg daily. Sublingual nitroglycerin as needed. Stress ECG and echocardiogram. Home blood pressure monitoring.'
        WHEN 'Chronic sleep disorder'
            THEN 'Sleep hygiene. Zolpidem 5mg before bedtime (for 5 days). Relaxation techniques counseling. Follow-up in 2 weeks.'
        WHEN 'Unexplained weight loss'
            THEN 'Multivitamin và mineral supplements. Iron 60mg daily. Vitamin D3 1000 IU daily. Nutrition counseling. Blood tests in 1 month.'
        WHEN 'Chronic lower back pain'
            THEN 'Meloxicam 7.5mg once daily after meals for 10 days. Physical therapy. Avoid heavy lifting và prolonged sitting. Follow-up in 2 weeks with MRI results.'
        WHEN 'Diarrhea and abdominal pain'
            THEN 'Loperamide 2mg as needed for diarrhea. Mebeverine 135mg three times daily 30 minutes trước meals. Diet rich in soluble fiber. Stress reduction. Follow-up in 1 month.'
        WHEN 'Shortness of breath during exertion'
            THEN 'Tiotropium 18mcg inhaler once daily. Budesonide inhaler as needed. Breathing exercises. Smoking cessation. Influenza và pneumococcal vaccines.'
        ELSE
            'Cefuroxime 250mg twice daily for 5 days. Fluticasone nasal spray twice daily. Saline solution for sinus irrigation. Follow-up in 1 week.'
    END AS treatment_plan,

    -- outcome ngẫu nhiên
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN 'Condition worsened, urgent intervention required'
        WHEN 1 THEN 'No significant improvement, treatment plan adjustment needed'
        WHEN 2 THEN 'Partially improved, continued monitoring required'
        ELSE   'Condition resolved, good response to treatment'
    END AS outcome

FROM (
    -- bước 2: tính discharge_date và main_complaint
    SELECT
        ca.patient_id,
        ca.appointment_id,
        ca.appt_date           AS admission_date,
        CASE FLOOR(RAND() * 10)
            WHEN 0 THEN DATE_ADD(ca.appt_date, INTERVAL 1 DAY)
            WHEN 1 THEN DATE_ADD(ca.appt_date, INTERVAL 2 DAY)
            WHEN 2 THEN DATE_ADD(ca.appt_date, INTERVAL 3 DAY)
            WHEN 3 THEN DATE_ADD(ca.appt_date, INTERVAL 4 DAY)
            ELSE ca.appt_date
        END                    AS discharge_date,
        ELT(
            FLOOR(1 + RAND() * 15),
            'Severe persistent headache',
            'Sore throat with high fever',
            'Upper abdominal pain',
            'Persistent cough with difficulty breathing',
            'Knee joint pain during movement',
            'Dizziness và nausea',
            'Prolonged fatigue of unknown cause',
            'Skin rash with itching',
            'Left chest pain radiating to shoulder',
            'Chronic sleep disorder',
            'Unexplained weight loss',
            'Chronic lower back pain',
            'Diarrhea và abdominal pain',
            'Shortness of breath during exertion',
            'Tinnitus and hearing loss'
        )                      AS main_complaint
    FROM (
        -- bước 1: lọc completed appointments
        SELECT
            appointment_id,
            patient_id,
            DATE(start_time) AS appt_date
        FROM appointments
        WHERE appointment_status = 'COMPLETED'
    ) AS ca
) AS pd;

-- 1. Thêm dữ liệu cho vital_signs
INSERT INTO vital_signs (
    medical_record_id,
    pulse_rate,
    bp_systolic,
    bp_diastolic,
    temperature,
    respiratory_rate,
    spo2,
    recorded_at
)
SELECT
    mr.medical_record_id,
    FLOOR(60 + RAND() * 60)            AS pulse_rate,         -- 60–120 bpm
    FLOOR(100 + RAND() * 40)           AS bp_systolic,        -- 100–140 mmHg
    FLOOR(60 + RAND() * 30)            AS bp_diastolic,       -- 60–90 mmHg
    ROUND(36.5 + RAND() * 2.5, 1)      AS temperature,        -- 36.5–39.0 °C
    FLOOR(12 + RAND() * 20)            AS respiratory_rate,   -- 12–32 breaths/min
    FLOOR(90 + RAND() * 10)            AS spo2,               -- 90–99%
    -- recorded_at: admission_date + random offset days up to discharge_date
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(
        RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)
      ) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.3;

-- 2. Thêm dữ liệu cho respiratory_exams
INSERT INTO respiratory_exams (
    medical_record_id,
    breathing_pattern,
    fremitus,
    percussion_note,
    auscultation,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Tachypneic', 'Bradypneic', 'Kussmaul'),
    ELT(FLOOR(1 + RAND() * 3), 'Normal', 'Increased', 'Decreased'),
    ELT(FLOOR(1 + RAND() * 4), 'Resonant', 'Dull', 'Hyperresonant', 'Tympanic'),
    ELT(FLOOR(1 + RAND() * 5), 'Clear', 'Crackles', 'Wheezes', 'Rhonchi', 'Stridor'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.5;  -- 50% bệnh án có khám hô hấp


-- 3. Thêm dữ liệu cho cardiac_exams
INSERT INTO cardiac_exams (
    medical_record_id,
    heart_rate,
    heart_sounds,
    murmur,
    jugular_venous_pressure,
    edema,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    FLOOR(60 + RAND() * 80),  -- 60–140 bpm
    ELT(FLOOR(1 + RAND() * 3), 'Normal', 'S3 gallop', 'S4 gallop'),
    ELT(FLOOR(1 + RAND() * 4), 'None', 'Systolic', 'Diastolic', 'Continuous'),
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Elevated', 'Not assessed', 'Decreased'),
    ELT(FLOOR(1 + RAND() * 4), 'None', 'Mild', 'Moderate', 'Severe'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.4;  -- 60% bệnh án có khám tim


-- 4. Thêm dữ liệu cho neurologic_exams
INSERT INTO neurologic_exams (
    medical_record_id,
    consciousness,
    cranial_nerves,
    motor_function,
    sensory_function,
    reflexes,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 4), 'Alert', 'Confused', 'Lethargic', 'Stuporous'),
    ELT(FLOOR(1 + RAND() * 3), 'Intact', 'Impaired', 'Not assessed'),
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Weakness', 'Paralysis', 'Spasticity'),
    ELT(FLOOR(1 + RAND() * 3), 'Intact', 'Decreased', 'Absent'),
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Hyperreflexia', 'Hyporeflexia', 'Areflexia'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.6;  -- 40% bệnh án có khám thần kinh


-- 5. Thêm dữ liệu cho gastrointestinal_exams
INSERT INTO gastrointestinal_exams (
    medical_record_id,
    abdominal_inspection,
    palpation,
    percussion,
    auscultation,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Distended', 'Scars', 'Hernia'),
    ELT(FLOOR(1 + RAND() * 4), 'Soft', 'Tender', 'Guarding', 'Rigid'),
    ELT(FLOOR(1 + RAND() * 3), 'Tympanic', 'Dull', 'Hyperresonant'),
    ELT(FLOOR(1 + RAND() * 3), 'Normal', 'Hypoactive', 'Hyperactive'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.5;  -- 50% bệnh án có khám tiêu hóa


-- 6. Thêm dữ liệu cho genitourinary_exams
INSERT INTO genitourinary_exams (
    medical_record_id,
    kidney_area,
    bladder,
    genital_inspection,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 3), 'Non-tender', 'Tender', 'Mass'),
    ELT(FLOOR(1 + RAND() * 3), 'Not palpable', 'Distended', 'Tender'),
    ELT(FLOOR(1 + RAND() * 3), 'Normal', 'Rash', 'Discharge'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.7;  -- 30% bệnh án có khám tiết niệu


-- 7. Thêm dữ liệu cho musculoskeletal_exams
INSERT INTO musculoskeletal_exams (
    medical_record_id,
    joint_exam,
    muscle_strength,
    deformity,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Swelling', 'Redness', 'Deformity'),
    ELT(FLOOR(1 + RAND() * 5), '5/5', '4/5', '3/5', '2/5', '1/5'),
    ELT(FLOOR(1 + RAND() * 4), 'None', 'Scoliosis', 'Kyphosis', 'Lordosis'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.6;  -- 40% bệnh án có khám cơ xương


-- 8. Thêm dữ liệu cho dermatologic_exams
INSERT INTO dermatologic_exams (
    medical_record_id,
    skin_appearance,
    rash,
    lesions,
    recorded_at
)
SELECT 
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 4), 'Normal', 'Pale', 'Jaundiced', 'Cyanotic'),
    ELT(FLOOR(1 + RAND() * 4), 'None', 'Maculopapular', 'Vesicular', 'Pustular'),
    ELT(FLOOR(1 + RAND() * 4), 'None', 'Ulcer', 'Nodule', 'Plaque'),
    DATE_ADD(
      mr.admission_date,
      INTERVAL FLOOR(RAND() * (DATEDIFF(mr.discharge_date, mr.admission_date) + 1)) DAY
    ) AS recorded_at
FROM medical_records mr
WHERE RAND() > 0.7;  -- 30% bệnh án có khám da liễu


-- 9. Thêm dữ liệu cho clinical_notes (nhiều ghi chú cho mỗi bệnh án)
INSERT INTO clinical_notes (
    medical_record_id,
    note_text,
    recorded_at
)
SELECT 
    t.medical_record_id,
    t.note_text,
    t.recorded_at
FROM (
    -- lồng 2 cấp: tạo note_count rồi sinh note_num
    SELECT
      nr.medical_record_id,
      ELT(
        FLOOR(1 + RAND() * 10),
        'Patient reported improvement in symptoms',
        'No adverse effects from medication',
        'Vital signs stable',
        'Plan: Continue current management',
        'Patient education provided',
        'Follow-up scheduled',
        'Laboratory results reviewed',
        'Dietary recommendations given',
        'Pain well controlled with current meds',
        'Physical therapy initiated'
      ) AS note_text,
      DATE_ADD(
        nr.admission_date,
        INTERVAL FLOOR(RAND() * (DATEDIFF(nr.discharge_date, nr.admission_date) + 1)) DAY
      ) AS recorded_at
    FROM (
      SELECT
        mr.medical_record_id,
        mr.admission_date,
        mr.discharge_date,
        FLOOR(1 + RAND() * 3) AS note_count
      FROM medical_records mr
    ) AS nr
    JOIN (
      SELECT 1 AS note_num UNION ALL SELECT 2 UNION ALL SELECT 3
    ) AS nums ON nums.note_num <= nr.note_count
) AS t;


-- 10. Thêm dữ liệu cho medical_record_symptoms
INSERT INTO medical_record_symptoms (
    medical_record_id,
    symptom_name,
    onset_date,
    duration,
    severity,
    description
)
SELECT
    mr.medical_record_id,
    ELT(FLOOR(1 + RAND() * 20),
        'Headache', 'Fever', 'Cough', 'Shortness of breath', 'Chest pain',
        'Abdominal pain', 'Nausea', 'Vomiting', 'Diarrhea', 'Constipation',
        'Dizziness', 'Fatigue', 'Muscle pain', 'Joint pain', 'Rash',
        'Sore throat', 'Runny nose', 'Sneezing', 'Chills', 'Sweating'
    ) AS symptom_name,
    DATE_SUB(mr.admission_date, INTERVAL FLOOR(1 + RAND() * 30) DAY) AS onset_date,
    CONCAT(FLOOR(1 + RAND() * 30), ' days') AS duration,
    ELT(FLOOR(1 + RAND() * 5), 'Mild', 'Moderate', 'Severe', 'Very severe', 'Worst possible') AS severity,
    ELT(FLOOR(1 + RAND() * 5),
        'Worsens with activity',
        'Relieved by rest',
        'Aggravated by certain foods',
        'Occurs mostly at night',
        'No specific pattern'
    ) AS description
FROM medical_records mr
WHERE RAND() > 0.2;  

CREATE TEMPORARY TABLE temp_appointments AS
SELECT a.appointment_id,
       a.patient_id,
       a.doctor_id,
       a.start_time,
       1 + FLOOR(RAND() * 3) AS num_tests
FROM appointments a
WHERE a.appointment_status != 'CANCELLED'
  AND RAND() < 0.7;

-- Insert into test_requests
INSERT INTO test_requests (patient_id, doctor_id, appointment_id, test_type_id, request_status, request_time, reason)
SELECT
    ta.patient_id,
    ta.doctor_id,
    ta.appointment_id,
    (SELECT test_type_id FROM test_types ORDER BY RAND() LIMIT 1),
    ELT(FLOOR(1 + RAND() * 5), 'pending', 'received', 'processing', 'completed', 'rejected'),
    ta.start_time + INTERVAL FLOOR(RAND() * 7) DAY,
    ELT(FLOOR(1 + RAND() * 5), 'Routine checkup', 'Follow-up test', 'Diagnostic purpose', 'Patient complaint', 'Doctor\'s recommendation')
FROM temp_appointments ta
JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3) nums
ON nums.n <= ta.num_tests;

-- Drop temporary table
DROP TEMPORARY TABLE temp_appointments;

-- Insert into samples
INSERT INTO samples (barcode, test_request_id, sampler_id, sample_status, collection_time)
SELECT
    CONCAT('smp-', LPAD(FLOOR(RAND() * 99999999), 8, '0'), '-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s')),
    tr.test_request_id,
    (SELECT user_id FROM users WHERE user_role = 'STAFF' ORDER BY RAND() LIMIT 1),
    CASE
        WHEN RAND() < 0.8 THEN 'collected'
        WHEN RAND() < 0.9 THEN 'pending'
        ELSE 'rejected'
    END,
    tr.request_time + INTERVAL FLOOR(RAND() * 3) DAY
FROM test_requests tr;

-- Insert into results
INSERT INTO results (sample_id, test_type_id, result_entry_status, technician_id, approved_by, approved_time, notes)
SELECT
    s.sample_id,
    tr.test_type_id,
    ELT(FLOOR(1 + RAND() * 4), 'pending', 'entered', 'verified', 'sent'),
    (SELECT user_id FROM users WHERE user_role = 'STAFF' ORDER BY RAND() LIMIT 1),
    (SELECT user_id FROM users WHERE user_role = 'STAFF' ORDER BY RAND() LIMIT 1),
    s.collection_time + INTERVAL FLOOR(1 + RAND() * 5) DAY,
    IF(RAND() < 0.2, 'Sample processed with standard protocol', NULL)
FROM samples s
JOIN test_requests tr ON s.test_request_id = tr.test_request_id
WHERE s.sample_status = 'collected';

-- Insert into result_details
INSERT INTO result_details (result_id, test_item_id, value, flag)
SELECT
    sub.result_id,
    sub.test_item_id,
    sub.val,
    CASE
        WHEN sub.ref_min IS NULL OR sub.ref_max IS NULL THEN 'normal'
        WHEN sub.val < sub.ref_min THEN '↓'
        WHEN sub.val > sub.ref_max THEN '↑'
        ELSE 'normal'
    END AS flag
FROM (
    SELECT
        r.result_id,
        ti.test_item_id,
        CASE
            WHEN RAND() < 0.8 THEN ti.ref_min + (ti.ref_max - ti.ref_min) * RAND()
            WHEN RAND() < 0.9 THEN ti.ref_min - (ti.ref_min * 0.2) * RAND()
            ELSE ti.ref_max + (ti.ref_max * 0.2) * RAND()
        END AS val,
        ti.ref_min,
        ti.ref_max
    FROM results r
    JOIN test_types tt ON r.test_type_id = tt.test_type_id
    JOIN test_items ti ON ti.test_type_id = tt.test_type_id
) AS sub;

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