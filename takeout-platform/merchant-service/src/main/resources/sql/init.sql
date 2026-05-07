-- 商家表
CREATE TABLE `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商家名称',
    `address` VARCHAR(255) NOT NULL COMMENT '商家地址',
    `longitude` DECIMAL(10, 6) COMMENT '经度',
    `latitude` DECIMAL(10, 6) COMMENT '纬度',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-营业中 0-休息中',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 菜品表
CREATE TABLE `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `description` VARCHAR(500) COMMENT '描述',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-上架 0-下架',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 插入测试数据
INSERT INTO `merchant` (`name`, `address`, `longitude`, `latitude`, `phone`, `status`) VALUES
('湘菜馆', '长沙市岳麓区麓山南路', 112.938883, 28.228209, '0731-88888888', 1),
('川味坊', '成都市武侯区科华北路', 104.051563, 30.642865, '028-66666666', 1);

INSERT INTO `dish` (`merchant_id`, `name`, `price`, `description`, `stock`, `status`) VALUES
(1, '剁椒鱼头', 68.00, '正宗湖南剁椒鱼头', 100, 1),
(1, '辣椒炒肉', 38.00, '农家土猪肉配本地辣椒', 200, 1),
(1, '臭豆腐', 20.00, '长沙特色臭豆腐', 150, 1),
(2, '麻婆豆腐', 28.00, '经典川菜', 300, 1),
(2, '宫保鸡丁', 42.00, '香辣可口', 180, 1);