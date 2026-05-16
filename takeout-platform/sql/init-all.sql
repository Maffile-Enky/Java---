-- ========================================
-- 外卖平台 MySQL 初始化脚本
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
-- 支付服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS takeout_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE takeout_payment;

CREATE TABLE IF NOT EXISTS t_payment_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(30) NOT NULL UNIQUE COMMENT '支付流水号',
    order_no VARCHAR(30) NOT NULL COMMENT '业务订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_channel VARCHAR(20) NOT NULL COMMENT '支付渠道: ALIPAY/WECHAT',
    pay_type VARCHAR(20) DEFAULT 'NATIVE' COMMENT '支付类型: NATIVE/JSAPI/H5',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: CREATED/PENDING/SUCCESS/FAILED/REFUNDED/CLOSED',
    trade_no VARCHAR(64) COMMENT '第三方支付交易号',
    remark VARCHAR(500) COMMENT '商品描述/备注',
    pay_time DATETIME COMMENT '支付成功时间',
    expire_time DATETIME COMMENT '支付过期时间',
    callback_data TEXT COMMENT '回调原始数据',
    refund_reason VARCHAR(500) COMMENT '退款原因',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_no (payment_no),
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';

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

-- ========================================
-- 通知服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS takeout_notification DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE takeout_notification;

CREATE TABLE IF NOT EXISTS t_notification_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_no VARCHAR(30) NOT NULL UNIQUE COMMENT '通知流水号',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    order_no VARCHAR(30) COMMENT '关联订单号',
    type VARCHAR(30) NOT NULL COMMENT '通知类型: PAYMENT_SUCCESS/ORDER_CANCEL/ORDER_STATUS',
    channel VARCHAR(20) NOT NULL COMMENT '通知渠道: WEBSOCKET/SMS/APP',
    title VARCHAR(100) COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    extra_data TEXT COMMENT '扩展数据(JSON)',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/SENDING/SUCCESS/FAILED/RETRY',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    max_retry INT DEFAULT 3 COMMENT '最大重试次数',
    last_send_time DATETIME COMMENT '最后发送时间',
    fail_reason VARCHAR(500) COMMENT '失败原因',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_notification_no (notification_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- ========================================
-- 骑手配送服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS takeout_delivery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE takeout_delivery;

CREATE TABLE IF NOT EXISTS t_rider (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '骑手姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    id_card_no VARCHAR(20) COMMENT '身份证号',
    vehicle_type VARCHAR(20) DEFAULT 'ELECTRIC_BIKE' COMMENT '车辆类型: BIKE/ELECTRIC_BIKE/MOTORCYCLE',
    status VARCHAR(20) DEFAULT 'OFFLINE' COMMENT '状态: OFFLINE/ONLINE/BUSY',
    rating DECIMAL(3,1) DEFAULT 5.0 COMMENT '评分',
    total_orders INT DEFAULT 0 COMMENT '完成订单数',
    longitude DECIMAL(10,6) COMMENT '当前经度',
    latitude DECIMAL(10,6) COMMENT '当前纬度',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手信息表';

CREATE TABLE IF NOT EXISTS t_delivery_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(30) NOT NULL UNIQUE COMMENT '配送任务号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(30) NOT NULL COMMENT '订单号',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    merchant_name VARCHAR(100) COMMENT '商家名称',
    merchant_address VARCHAR(255) COMMENT '商家地址',
    merchant_longitude DECIMAL(10,6) COMMENT '商家经度',
    merchant_latitude DECIMAL(10,6) COMMENT '商家纬度',
    delivery_address VARCHAR(255) NOT NULL COMMENT '配送地址',
    delivery_longitude DECIMAL(10,6) COMMENT '配送经度',
    delivery_latitude DECIMAL(10,6) COMMENT '配送纬度',
    delivery_phone VARCHAR(20) COMMENT '收货人电话',
    delivery_name VARCHAR(50) COMMENT '收货人姓名',
    rider_id BIGINT COMMENT '骑手ID',
    rider_name VARCHAR(50) COMMENT '骑手姓名',
    rider_phone VARCHAR(20) COMMENT '骑手电话',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/ASSIGNED/PICKED_UP/DELIVERING/COMPLETED/CANCELLED',
    estimated_distance DECIMAL(10,2) COMMENT '预计距离(km)',
    estimated_time INT COMMENT '预计时间(分钟)',
    actual_distance DECIMAL(10,2) COMMENT '实际距离(km)',
    actual_time INT COMMENT '实际时间(分钟)',
    fee DECIMAL(10,2) COMMENT '配送费',
    note VARCHAR(500) COMMENT '备注',
    assigned_at DATETIME COMMENT '派单时间',
    picked_up_at DATETIME COMMENT '取餐时间',
    delivered_at DATETIME COMMENT '送达时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_rider_id (rider_id),
    INDEX idx_status (status),
    INDEX idx_task_no (task_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送任务表';

CREATE TABLE IF NOT EXISTS t_rider_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rider_id BIGINT NOT NULL COMMENT '骑手ID',
    longitude DECIMAL(10,6) NOT NULL COMMENT '经度',
    latitude DECIMAL(10,6) NOT NULL COMMENT '纬度',
    speed DECIMAL(5,2) COMMENT '速度(km/h)',
    heading INT COMMENT '方向(0-360度)',
    task_id BIGINT COMMENT '当前配送任务ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rider_id (rider_id),
    INDEX idx_task_id (task_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手位置轨迹表';

-- ========================================
-- 搜索服务数据库
-- ========================================
CREATE DATABASE IF NOT EXISTS takeout_search DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE takeout_search;

CREATE TABLE IF NOT EXISTS search_merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL UNIQUE COMMENT '源商家ID',
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    description TEXT,
    image_url VARCHAR(500),
    longitude DECIMAL(10,6),
    latitude DECIMAL(10,6),
    rating DECIMAL(3,1) DEFAULT 5.0,
    monthly_sales INT DEFAULT 0 COMMENT '月销量',
    avg_delivery_time INT DEFAULT 30 COMMENT '平均配送时间(分钟)',
    categories VARCHAR(255) COMMENT '分类(逗号分隔)',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    FULLTEXT INDEX ft_name (name) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索商家索引表';

CREATE TABLE IF NOT EXISTS search_dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dish_id BIGINT NOT NULL COMMENT '源菜品ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    merchant_name VARCHAR(100) COMMENT '商家名称',
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    category VARCHAR(50) COMMENT '菜品分类',
    image_url VARCHAR(500),
    sales INT DEFAULT 0 COMMENT '销量',
    rating DECIMAL(3,1) DEFAULT 5.0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_status (status),
    FULLTEXT INDEX ft_name (name) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索菜品索引表';

-- 搜索服务测试数据：从商家服务同步
INSERT INTO search_merchant (merchant_id, name, address, longitude, latitude, rating, monthly_sales, avg_delivery_time, categories, status) VALUES
(1, '湘菜馆', '长沙市开福区湘江中路', 112.98, 28.22, 4.8, 356, 28, '湘菜,中餐', 1),
(2, '粤菜馆', '广州市天河区珠江新城', 113.33, 23.13, 4.6, 289, 32, '粤菜,中餐', 1),
(3, '川菜馆', '成都市锦江区春熙路', 104.07, 30.66, 4.9, 412, 25, '川菜,中餐', 1);

INSERT INTO search_dish (dish_id, merchant_id, merchant_name, name, price, description, category, sales, rating, status) VALUES
(1, 1, '湘菜馆', '剁椒鱼头', 68.00, '湖南经典名菜，鲜辣可口', '湘菜', 128, 4.9, 1),
(2, 1, '湘菜馆', '小炒黄牛肉', 58.00, '香辣过瘾，下饭神器', '湘菜', 96, 4.7, 1),
(3, 1, '湘菜馆', '农家小炒肉', 38.00, '地道湖南味道', '湘菜', 156, 4.8, 1),
(4, 2, '粤菜馆', '白切鸡', 45.00, '皮爽肉嫩，原汁原味', '粤菜', 88, 4.6, 1),
(5, 2, '粤菜馆', '蒸蛋羹', 18.00, '嫩滑可口，老少皆宜', '粤菜', 203, 4.5, 1),
(6, 2, '粤菜馆', '烧鹅饭', 35.00, '广式经典，外焦里嫩', '粤菜', 134, 4.7, 1),
(7, 3, '川菜馆', '麻婆豆腐', 28.00, '麻辣鲜香，经典川菜', '川菜', 178, 4.8, 1),
(8, 3, '川菜馆', '水煮肉片', 55.00, '川味十足，麻辣鲜香', '川菜', 145, 4.9, 1),
(9, 3, '川菜馆', '宫保鸡丁', 32.00, '酸甜微辣，鸡肉嫩滑', '川菜', 167, 4.7, 1);
