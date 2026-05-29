-- Create the search database
CREATE DATABASE IF NOT EXISTS `takeout_search` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `takeout_search`;

-- Merchant search index table
CREATE TABLE IF NOT EXISTS `search_merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT 'Source merchant ID from merchant-service',
    `name` VARCHAR(128) NOT NULL COMMENT 'Merchant name',
    `address` VARCHAR(256) DEFAULT NULL COMMENT 'Merchant address',
    `description` VARCHAR(512) DEFAULT NULL COMMENT 'Merchant description',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT 'Merchant image URL',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT 'Longitude',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT 'Latitude',
    `rating` DECIMAL(2,1) DEFAULT 0.0 COMMENT 'Average rating',
    `monthly_sales` INT DEFAULT 0 COMMENT 'Monthly sales count',
    `avg_delivery_time` INT DEFAULT 30 COMMENT 'Average delivery time in minutes',
    `categories` VARCHAR(256) DEFAULT NULL COMMENT 'Comma-separated categories',
    `status` INT DEFAULT 1 COMMENT '1=active, 0=inactive',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_id` (`merchant_id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_rating` (`rating`),
    KEY `idx_monthly_sales` (`monthly_sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Search merchant index table';

-- Dish search index table
CREATE TABLE IF NOT EXISTS `search_dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dish_id` BIGINT NOT NULL COMMENT 'Source dish ID from merchant-service',
    `merchant_id` BIGINT NOT NULL COMMENT 'Merchant ID',
    `merchant_name` VARCHAR(128) DEFAULT NULL COMMENT 'Merchant name (denormalized)',
    `name` VARCHAR(128) NOT NULL COMMENT 'Dish name',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Dish price',
    `description` VARCHAR(512) DEFAULT NULL COMMENT 'Dish description',
    `category` VARCHAR(64) DEFAULT NULL COMMENT 'Dish category',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT 'Dish image URL',
    `sales` INT DEFAULT 0 COMMENT 'Sales count',
    `rating` DECIMAL(2,1) DEFAULT 0.0 COMMENT 'Average rating',
    `status` INT DEFAULT 1 COMMENT '1=active, 0=inactive',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dish_id` (`dish_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_name` (`name`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Search dish index table';
