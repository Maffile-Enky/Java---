-- ========================================
-- 外卖平台 MySQL 初始化脚本
-- 服务器: 47.99.34.251:3306
-- 执行方式: mysql -u root -p < init-all.sql
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS takeout_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS takeout_merchant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ========================================
-- 用户服务数据库
-- ========================================
USE takeout_user;

CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    openid VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试账户: root / 123 (password will be BCrypt hashed by DataInitializer on startup)
INSERT INTO t_user (username, password, phone, nickname, role, status) VALUES
('root', '123', '13800000000', '管理员', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE username=username;

-- 商家入驻申请表（在 takeout_user 库，因为 user-service 需要访问）
CREATE TABLE IF NOT EXISTS merchant_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '申请人用户ID',
    shop_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    shop_address VARCHAR(255) NOT NULL COMMENT '店铺地址',
    longitude DECIMAL(10, 6) COMMENT '经度',
    latitude DECIMAL(10, 6) COMMENT '纬度',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    description TEXT COMMENT '店铺描述',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝',
    admin_note VARCHAR(500) COMMENT '管理员审核备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家入驻申请表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS t_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    longitude DECIMAL(10, 6) COMMENT '经度',
    latitude DECIMAL(10, 6) COMMENT '纬度',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 订单服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS takeout_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE takeout_order;

CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_name VARCHAR(100),
    total_price DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) DEFAULT 0.00,
    total_quantity INT DEFAULT 0,
    delivery_address VARCHAR(255),
    delivery_phone VARCHAR(20),
    delivery_name VARCHAR(50),
    note VARCHAR(500),
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/DELIVERING/COMPLETED/CANCELLED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    dish_id BIGINT,
    dish_name VARCHAR(100) NOT NULL,
    dish_image VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 商家服务数据库
-- ========================================
USE takeout_merchant;

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL COMMENT '关联用户ID',
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    longitude DECIMAL(10, 6),
    latitude DECIMAL(10, 6),
    phone VARCHAR(20),
    description TEXT COMMENT '商家描述',
    image_url VARCHAR(500) COMMENT '商家图片',
    status TINYINT DEFAULT 1,
    sort_weight INT DEFAULT 0 COMMENT '排序权重，值越大越靠前',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_user_id (user_id),
    INDEX idx_sort_weight (sort_weight)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商家测试数据
INSERT INTO merchant (name, address, longitude, latitude, phone, status) VALUES
('湘菜馆', '长沙市开福区湘江中路', 112.98, 28.22, '13800138001', 1),
('粤菜馆', '广州市天河区珠江新城', 113.33, 23.13, '13800138002', 1),
('川菜馆', '成都市锦江区春熙路', 104.07, 30.66, '13800138003', 1);

-- 菜品测试数据
INSERT INTO dish (merchant_id, name, price, description, stock, status) VALUES
(1, '剁椒鱼头', 68.00, '湖南经典名菜，鲜辣可口', 50, 1),
(1, '小炒黄牛肉', 58.00, '香辣过瘾，下饭神器', 30, 1),
(1, '农家小炒肉', 38.00, '地道湖南味道', 40, 1),
(2, '白切鸡', 45.00, '皮爽肉嫩，原汁原味', 40, 1),
(2, '蒸蛋羹', 18.00, '嫩滑可口，老少皆宜', 60, 1),
(2, '烧鹅饭', 35.00, '广式经典，外焦里嫩', 25, 1),
(3, '麻婆豆腐', 28.00, '麻辣鲜香，经典川菜', 50, 1),
(3, '水煮肉片', 55.00, '川味十足，麻辣鲜香', 35, 1),
(3, '宫保鸡丁', 32.00, '酸甜微辣，鸡肉嫩滑', 45, 1);
