CREATE DATABASE IF NOT EXISTS hunsnii_delguur CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hunsnii_delguur;

-- User table
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL -- 'ADMIN' or 'EMPLOYEE'
);

-- Supplier table
CREATE TABLE IF NOT EXISTS supplier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255)
);

-- Product table
CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DOUBLE NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    expiration_date DATE,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL
);

-- Stock In table
CREATE TABLE IF NOT EXISTS stock_in (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Stock Out table
CREATE TABLE IF NOT EXISTS stock_out (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Seed default users
INSERT INTO user (username, password, role) 
VALUES ('admin', 'admin', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user (username, password, role) 
VALUES ('staff', '1234', 'EMPLOYEE')
ON DUPLICATE KEY UPDATE username=username;

-- Seed some suppliers
INSERT INTO supplier (name, phone, address) VALUES
('Сүү ХК', '70111111', 'Улаанбаатар, Баянзүрх дүүрэг'),
('Талх Чихэр ХК', '70222222', 'Улаанбаатар, Сонгинохайрхан дүүрэг'),
('АПУ ХК', '70333333', 'Улаанбаатар, Хан-Уул дүүрэг');

-- Last verified: 2026-07-09

-- Last verified: 2026-07-09

-- Last verified: 2026-07-09
