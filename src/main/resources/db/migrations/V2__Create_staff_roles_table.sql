-- Create staff_roles table
CREATE TABLE IF NOT EXISTS `staff_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_staff_role_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default staff roles
INSERT INTO `staff_roles` (`name`, `description`) VALUES
('DOCTOR', 'Medical doctor'),
('TECHNICIAN', 'Medical technician'),
('NURSE', 'Nursing staff'),
('SCHEDULING_COORDINATOR', 'Scheduling coordinator'),
('PHARMACIST', 'Pharmacy staff'),
('INVENTORY_MANAGER', 'Inventory management'),
('LAB_RECEIVER', 'Laboratory receiver');
