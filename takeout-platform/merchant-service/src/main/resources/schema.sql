-- 创建商家表
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    longitude DECIMAL(10, 6),
    latitude DECIMAL(10, 6),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version INT DEFAULT 0,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据（商家）
INSERT INTO merchant (name, address, longitude, latitude, phone, status) VALUES 
('湘菜馆', '长沙市开福区湘江中路', 112.98, 28.22, '13800138001', 1),
('粤菜馆', '广州市天河区珠江新城', 113.33, 23.13, '13800138002', 1),
('川菜馆', '成都市锦江区春熙路', 104.07, 30.66, '13800138003', 1);

-- 插入测试数据（菜品）
INSERT INTO dish (merchant_id, name, price, description, stock, status) VALUES 
(1, '剁椒鱼头', 68.00, '湖南经典名菜', 50, 1),
(1, '小炒黄牛肉', 58.00, '香辣过瘾', 30, 1),
(2, '白切鸡', 45.00, '皮爽肉嫩', 40, 1),
(2, '蒸蛋羹', 18.00, '嫩滑可口', 60, 1),
(3, '麻婆豆腐', 28.00, '麻辣鲜香', 50, 1),
(3, '水煮肉片', 55.00, '川味十足', 35, 1);
