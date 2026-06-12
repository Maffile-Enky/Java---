# Takeout Platform - 外卖点餐平台技术文档

> 基于 Spring Cloud 微服务架构的全栈外卖点餐平台，涵盖用户点餐、商家管理、订单处理、支付结算、骑手配送、消息通知、智能搜索等完整业务闭环。

---

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 技术栈](#2-技术栈)
  - [2.1 后端技术栈](#21-后端技术栈)
  - [2.2 前端技术栈](#22-前端技术栈)
  - [2.3 运维与监控](#23-运维与监控)
- [3. 系统架构](#3-系统架构)
  - [3.1 整体架构图](#31-整体架构图)
  - [3.2 微服务划分](#32-微服务划分)
  - [3.3 请求处理流程](#33-请求处理流程)
- [4. 功能模块](#4-功能模块)
  - [4.1 用户端功能](#41-用户端功能)
  - [4.2 商家端功能](#42-商家端功能)
  - [4.3 骑手端功能](#43-骑手端功能)
  - [4.4 管理后台功能](#44-管理后台功能)
- [5. 核心业务逻辑](#5-核心业务逻辑)
  - [5.1 订单生命周期](#51-订单生命周期)
  - [5.2 支付流程](#52-支付流程)
  - [5.3 配送调度算法](#53-配送调度算法)
  - [5.4 消息队列事件驱动](#54-消息队列事件驱动)
- [6. 数据库设计](#6-数据库设计)
  - [6.1 核心表结构](#61-核心表结构)
  - [6.2 数据流转](#62-数据流转)
- [7. 安全设计](#7-安全设计)
  - [7.1 认证鉴权](#71-认证鉴权)
  - [7.2 网关安全](#72-网关安全)
- [8. 前端设计](#8-前端设计)
  - [8.1 组件体系](#81-组件体系)
  - [8.2 路由与权限](#82-路由与权限)
- [9. 部署架构](#9-部署架构)
  - [9.1 容器化部署](#91-容器化部署)
  - [9.2 监控体系](#92-监控体系)
- [10. 项目特色与创新](#10-项目特色与创新)

---

## 1. 项目概述

### 1.1 项目定位

本项目是一个功能完整的外卖点餐平台，模拟美团外卖的核心业务场景，采用**微服务架构**设计，支持多角色（用户、商家、骑手、管理员）协同工作，实现了从用户浏览、下单、支付、配送到售后的完整业务链路。

### 1.2 核心亮点

- **8 个独立微服务**：用户、商家、订单、支付、配送、通知、搜索、网关
- **7 个公共模块**：统一的安全、缓存、消息队列、Web 配置等基础设施
- **4 端 UI**：用户端、商家端、骑手端、管理后台，共计 29 个页面
- **145 个 Java 源文件**：完整的后端业务实现
- **14 个实体类**：覆盖所有核心业务表
- **事件驱动架构**：基于 RabbitMQ 的异步消息通信
- **容器化部署**：Docker Compose + Kubernetes 双轨部署方案

---

## 2. 技术栈

### 2.1 后端技术栈

| 技术栈 | 版本 | 用途 | 说明 |
|--------|------|------|------|
| **Spring Boot** | 3.0.13 | 应用框架 | 基于 Spring 6.x，支持 JDK 21 |
| **Spring Cloud** | 2022.0.0 | 微服务框架 | 服务治理、负载均衡、熔断降级 |
| **Spring Cloud Alibaba** | 2022.0.0.0 | 阿里微服务组件 | Nacos、Sentinel 集成 |
| **Nacos** | 2.3.1 | 注册中心 & 配置中心 | 服务注册发现、动态配置管理 |
| **Spring Cloud Gateway** | — | API 网关 | 路由转发、鉴权过滤、限流 |
| **Sentinel** | — | 流量控制 | 熔断降级、系统保护 |
| **MyBatis-Plus** | 3.5.5 | ORM 框架 | 代码生成、分页插件、逻辑删除 |
| **MySQL** | 8.0 | 关系型数据库 | 核心业务数据存储 |
| **Redis** | 7.x | 缓存 & 会话管理 | 热点数据缓存、分布式 Session |
| **RabbitMQ** | 3.12 | 消息队列 | 异步事件驱动、服务解耦 |
| **JWT** | — | 认证方案 | 无状态令牌、跨服务鉴权 |
| **Lombok** | — | 代码简化 | 减少样板代码 |

### 2.2 前端技术栈

| 技术栈 | 版本 | 用途 | 说明 |
|--------|------|------|------|
| **Vue.js** | 3.4 | 渐进式框架 | Composition API、响应式系统 |
| **Vite** | 5.0 | 构建工具 | 极速 HMR、ES Module 原生支持 |
| **Vue Router** | 4.2 | 路由管理 | 嵌套路由、路由守卫、懒加载 |
| **Pinia** | 2.1 | 状态管理 | 轻量级、TypeScript 友好 |
| **Axios** | 1.6 | HTTP 客户端 | 请求拦截、响应拦截、错误处理 |
| **Glassmorphism UI** | 自研 | 设计体系 | 毛玻璃风格组件库 |

### 2.3 运维与监控

| 技术栈 | 用途 | 说明 |
|--------|------|------|
| **Docker** | 容器化 | 统一运行环境 |
| **Docker Compose** | 服务编排 | 多容器编排管理 |
| **Kubernetes** | 容器编排 | 生产级部署方案 |
| **Prometheus** | 监控采集 | 指标数据收集、告警规则 |
| **Grafana** | 监控可视化 | 仪表盘展示、实时监控 |
| **Nginx** | 反向代理 | 静态资源托管、负载均衡 |

---

## 3. 系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              客户端层（Client Layer）                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   用户端     │  │   商家端     │  │   骑手端     │  │   管理后台   │            │
│  │  Vue 3 SPA  │  │  Vue 3 SPA  │  │  Vue 3 SPA  │  │  Vue 3 SPA  │            │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘            │
└─────────┼────────────────┼────────────────┼────────────────┼────────────────────┘
          │                │                │                │
          └────────────────┴────────┬───────┴────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────────────────────────┐
│                              网关层（Gateway Layer）                             │
│                          Nginx (反向代理/静态资源)                               │
│                                    │                                            │
│                          Spring Cloud Gateway (:9999)                           │
│                          ├── AuthFilter (JWT 鉴权)                              │
│                          ├── RateLimitFilter (限流)                             │
│                          ├── RequestLoggingFilter (日志)                        │
│                          ├── SecurityHeadersFilter (安全头)                     │
│                          └── TraceIdFilter (链路追踪)                           │
└───────────────────────────────────┼─────────────────────────────────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────────────────────────┐
│                              服务层（Service Layer）                             │
│                                    │                                            │
│  ┌──────────────┐  ┌──────────────┐│┌──────────────┐  ┌──────────────┐          │
│  │  用户服务     │  │  商家服务     │││  订单服务     │  │  支付服务     │          │
│  │  :8081       │  │  :8083       │││  :8084       │  │  :8085       │          │
│  │  用户/地址/   │  │  商家/菜品/   │││  订单管理/   │  │  支付/退款/   │          │
│  │  商家申请     │  │  购物车       │││  状态流转     │  │  回调处理     │          │
│  └──────┬───────┘  └──────┬───────┘│└──────┬───────┘  └──────┬───────┘          │
│         │                 │         │       │                 │                  │
│  ┌──────┴───────┐  ┌──────┴───────┐│┌──────┴───────┐  ┌──────┴───────┐          │
│  │  通知服务     │  │  配送服务     │││  搜索服务     │  │  网关服务     │          │
│  │  :8086       │  │  :8087       │││  :8088       │  │  :9999       │          │
│  │  站内信/      │  │  智能调度/   │││  商家搜索/    │  │  路由/鉴权/   │          │
│  │  短信推送     │  │  骑手管理     │││  菜品搜索     │  │  限流         │          │
│  └──────────────┘  └──────────────┘│└──────────────┘  └──────────────┘          │
└────────────────────────────────────┼────────────────────────────────────────────┘
                                     │
┌────────────────────────────────────┼────────────────────────────────────────────┐
│                              基础设施层（Infrastructure）                         │
│                                     │                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │    Nacos      │  │    MySQL     │  │    Redis     │  │   RabbitMQ   │          │
│  │  注册/配置    │  │   3306       │  │   6379       │  │   5672       │          │
│  │  :8848       │  │  业务数据     │  │  缓存/会话   │  │  异步消息     │          │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                                  │
│  ┌──────────────┐  ┌──────────────┐                                              │
│  │  Prometheus  │  │   Grafana    │                                              │
│  │  :9090       │  │   :3000      │                                              │
│  │  指标采集     │  │  可视化面板   │                                              │
│  └──────────────┘  └──────────────┘                                              │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 微服务划分

| 服务名称 | 端口 | 职责 | 核心实体 |
|----------|------|------|----------|
| **gateway** | 9999 | API 路由、JWT 鉴权、限流、安全头注入 | — |
| **user-service** | 8081 | 用户注册登录、地址管理、商家入驻申请 | User, Address, MerchantApplication |
| **merchant-service** | 8083 | 商家信息管理、菜品 CRUD、购物车 | Merchant, Dish, Cart |
| **order-service** | 8084 | 订单创建、状态流转、订单查询 | Order, OrderItem |
| **payment-service** | 8085 | 支付创建、回调处理、退款 | PaymentOrder |
| **notification-service** | 8086 | 站内消息、短信通知、消息推送 | NotificationRecord |
| **delivery-service** | 8087 | 配送任务、骑手调度、位置追踪 | DeliveryTask, Rider, RiderLocation |
| **search-service** | 8088 | 商家搜索、菜品搜索、搜索同步 | SearchMerchant, SearchDish |

### 3.3 请求处理流程

```
用户请求
    │
    ▼
Nginx (静态资源 + 反向代理)
    │
    ▼
Spring Cloud Gateway
    ├── 1. TraceIdFilter → 注入链路追踪 ID
    ├── 2. SecurityHeadersFilter → 添加安全响应头
    ├── 3. RequestLoggingFilter → 记录请求日志
    ├── 4. AuthFilter → JWT 鉴权，提取用户信息写入 Header
    └── 5. RateLimitFilter → Sentinel 限流
    │
    ▼
目标微服务
    ├── 从 Header 读取 X-User-Id / X-Username / X-User-Role
    ├── 执行业务逻辑
    └── 返回统一响应格式
```

---

## 4. 功能模块

### 4.1 用户端功能

| 功能模块 | 核心功能 | 技术实现 |
|----------|----------|----------|
| **注册登录** | 手机号注册、密码登录、角色升级 | JWT + Redis Session |
| **浏览商家** | 商家列表、筛选排序、距离计算 | 搜索服务 + 滚动加载动画 |
| **商家详情** | 菜品展示、购物车、收藏 | 响应式布局 + 本地状态管理 |
| **下单支付** | 地址选择、备注填写、在线支付 | 支付服务 + 异步回调 |
| **订单管理** | 订单列表、状态追踪、取消订单 | 订单状态机 |
| **配送追踪** | 实时位置、预计送达时间 | 骑手位置服务 |
| **消息通知** | 订单状态变更、支付成功通知 | RabbitMQ + 通知服务 |
| **个人中心** | 个人信息、收货地址、角色升级 | 用户服务 |

### 4.2 商家端功能

| 功能模块 | 核心功能 | 技术实现 |
|----------|----------|----------|
| **商家入驻** | 商家申请、审核流程 | MerchantApplication 实体 |
| **仪表盘** | 今日数据、订单统计、收入概览 | 聚合查询 |
| **菜品管理** | 菜品 CRUD、分类管理、上下架 | DishService + 图片上传 |
| **订单处理** | 接单/拒单、状态更新、订单查询 | 订单状态机 + WebSocket |
| **商家设置** | 店铺信息、营业时间、配送范围 | 商家服务 |

### 4.3 骑手端功能

| 功能模块 | 核心功能 | 技术实现 |
|----------|----------|----------|
| **骑手注册** | 实名认证、在线状态管理 | 骑手服务 |
| **接单配送** | 任务接收、取餐确认、配送状态更新 | 配送任务服务 |
| **位置上报** | 实时位置更新、轨迹记录 | RiderLocation 实体 |
| **配送统计** | 配送单数、收入统计 | 聚合查询 |

### 4.4 管理后台功能

| 功能模块 | 核心功能 | 技术实现 |
|----------|----------|----------|
| **数据概览** | 平台数据统计、趋势分析 | DashboardView |
| **用户管理** | 用户列表、角色管理、禁用/启用 | UserAdminController |
| **商家管理** | 商家审核、信息管理、上下架 | MerchantManageView |
| **申请审批** | 商家入驻申请审核 | ApplicationManageView |

---

## 5. 核心业务逻辑

### 5.1 订单生命周期

```
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│ PENDING │───▶│  PAID   │───▶│CONFIRMED│───▶│DELIVERING│───▶│COMPLETED│
│  待支付  │    │  已支付  │    │  已确认  │    │  配送中  │    │  已完成  │
└────┬────┘    └────┬────┘    └─────────┘    └─────────┘    └─────────┘
     │              │
     │              ▼
     │         ┌─────────┐
     └────────▶│CANCELLED│
               │  已取消  │
               └─────────┘
```

**状态说明：**
- `PENDING`：订单创建成功，等待用户支付
- `PAID`：支付成功，等待商家确认
- `CONFIRMED`：商家已接单，等待骑手取餐
- `DELIVERING`：骑手已取餐，正在配送
- `COMPLETED`：用户确认收货，订单完成
- `CANCELLED`：订单被取消（超时未支付、用户取消、商家拒单）

### 5.2 支付流程

```
用户下单
    │
    ▼
OrderService.createOrder()
    │ 创建订单记录 (status=PENDING)
    │
    ▼
PaymentService.createPayment()
    │ 创建支付流水 (status=CREATED)
    │ 生成支付链接/二维码
    │
    ▼
用户完成支付
    │
    ▼
支付平台回调 /payment/callback
    │
    ▼
PaymentService.handleCallback()
    ├── 验证签名
    ├── 更新支付流水 (status=SUCCESS)
    │
    ├──▶ RabbitMQ: payment.success
    │       │
    │       ├──▶ OrderService (消费)
    │       │       └── 更新订单状态 (PENDING → PAID)
    │       │
    │       ├──▶ NotificationService (消费)
    │       │       └── 发送支付成功通知
    │       │
    │       └──▶ DeliveryService (消费)
    │               └── 创建配送任务
    │
    └── 返回支付平台 "SUCCESS"
```

**支付服务核心特性：**
- 支持支付宝（ALIPAY）和微信（WECHAT）双通道
- 支持扫码支付（NATIVE）、公众号支付（JSAPI）、H5 支付
- 支付流水表记录完整生命周期，支持对账
- 乐观锁（`@Version`）防止并发更新冲突
- 逻辑删除（`@TableLogic`）保护数据完整性
- 支付过期自动关闭机制
- 死信队列（DLQ）处理支付异常消息

### 5.3 配送调度算法

配送服务采用 **Haversine 公式** 计算地理距离，实现智能骑手调度：

```java
// 核心算法：Haversine 公式计算两点间距离
public BigDecimal calculateDistance(BigDecimal lon1, BigDecimal lat1,
                                     BigDecimal lon2, BigDecimal lat2) {
    double lat1Rad = Math.toRadians(lat1.doubleValue());
    double lat2Rad = Math.toRadians(lat2.doubleValue());
    double deltaLat = Math.toRadians(lat2 - lat1);
    double deltaLon = Math.toRadians(lon2 - lon1);

    double a = Math.sin(deltaLat/2)² + cos(lat1) × cos(lat2) × sin(deltaLon/2)²;
    double c = 2 × atan2(√a, √(1-a));

    return EARTH_RADIUS_KM × c;  // 地球半径 6371km
}
```

**调度策略：**
1. 查询所有在线且非忙碌的骑手
2. 遍历计算骑手位置与商家的距离
3. 筛选 10km 搜索半径内的骑手
4. 选择距离最近的骑手进行派单
5. 预估配送时间 = 取餐时间(5min) + 骑行时间(distance/15km/h)

**配送任务状态流转：**
```
PENDING → ASSIGNED → PICKED_UP → DELIVERING → COMPLETED
   │
   └──▶ CANCELLED
```

### 5.4 消息队列事件驱动

系统通过 RabbitMQ 实现服务间异步通信，核心事件流：

```
┌─────────────────────────────────────────────────────────────────────┐
│                         RabbitMQ 消息拓扑                            │
│                                                                     │
│  ┌──────────────────┐                                               │
│  │  payment.exchange │ (Topic Exchange)                              │
│  │  payment.success  │                                               │
│  └────────┬─────────┘                                               │
│           │                                                         │
│           ├──▶ payment.success.order.queue                          │
│           │       └── OrderService: 更新订单状态                     │
│           │                                                         │
│           ├──▶ payment.success.notify.queue                         │
│           │       └── NotificationService: 发送支付成功通知          │
│           │                                                         │
│           └──▶ payment.dlq.queue (死信队列)                         │
│                   └── 异常消息人工处理                               │
│                                                                     │
│  ┌──────────────────┐                                               │
│  │  order.exchange   │ (Topic Exchange)                              │
│  │  order.cancel     │                                               │
│  └────────┬─────────┘                                               │
│           │                                                         │
│           └──▶ order.cancel.notify.queue                            │
│                   └── NotificationService: 发送订单取消通知          │
│                                                                     │
│  ┌──────────────────┐                                               │
│  │  通知重试机制     │                                               │
│  │  notification.retry.queue → notification.dlx.exchange            │
│  │       └── 失败重试 → 超时进入死信队列                             │
│  └──────────────────┘                                               │
└─────────────────────────────────────────────────────────────────────┘
```

**消息可靠性保障：**
- **死信队列（DLQ）**：支付和通知消息均有死信兜底
- **重试机制**：通知失败自动重试，超时进入死信
- **幂等消费**：消费端通过业务 ID 去重，防止重复处理
- **统一常量**：`MQConstants` 类统一管理所有交换机、队列、路由键名称

---

## 6. 数据库设计

### 6.1 核心表结构

#### 用户相关

```sql
-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER',  -- USER/MERCHANT/RIDER/ADMIN
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 收货地址表
CREATE TABLE t_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    longitude DECIMAL(10,7),
    latitude DECIMAL(10,7),
    is_default TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商家入驻申请表
CREATE TABLE t_merchant_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    merchant_name VARCHAR(100),
    address VARCHAR(255),
    phone VARCHAR(20),
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING/APPROVED/REJECTED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 商家相关

```sql
-- 商家表
CREATE TABLE merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    longitude DECIMAL(10,7),
    latitude DECIMAL(10,7),
    phone VARCHAR(20),
    description TEXT,
    image_url VARCHAR(255),
    cover_image VARCHAR(255),
    rating DECIMAL(2,1) DEFAULT 5.0,
    monthly_sales INT DEFAULT 0,
    status INT DEFAULT 1,
    sort_weight INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 菜品表
CREATE TABLE dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    category VARCHAR(50),
    status INT DEFAULT 1,  -- 1-上架 0-下架
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 订单相关

```sql
-- 订单主表
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_name VARCHAR(100),
    total_price DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) DEFAULT 0,
    total_quantity INT DEFAULT 0,
    delivery_address VARCHAR(255),
    delivery_phone VARCHAR(20),
    delivery_name VARCHAR(50),
    note TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单明细表
CREATE TABLE t_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    dish_name VARCHAR(100),
    dish_price DECIMAL(10,2),
    quantity INT DEFAULT 1,
    subtotal DECIMAL(10,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 支付相关

```sql
-- 支付流水表
CREATE TABLE t_payment_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no VARCHAR(32) UNIQUE NOT NULL,
    order_no VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_channel VARCHAR(20),  -- ALIPAY/WECHAT
    pay_type VARCHAR(20),     -- NATIVE/JSAPI/H5
    status VARCHAR(20) DEFAULT 'CREATED',
    trade_no VARCHAR(64),
    pay_time DATETIME,
    expire_time DATETIME,
    callback_data TEXT,
    refund_reason VARCHAR(255),
    remark VARCHAR(255),
    version INT DEFAULT 0,  -- 乐观锁
    deleted INT DEFAULT 0,  -- 逻辑删除
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 配送相关

```sql
-- 骑手表
CREATE TABLE t_rider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'OFFLINE',  -- ONLINE/OFFLINE/BUSY
    longitude DECIMAL(10,7),
    latitude DECIMAL(10,7),
    total_orders INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 配送任务表
CREATE TABLE t_delivery_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(32) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(32) NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_name VARCHAR(100),
    merchant_address VARCHAR(255),
    merchant_longitude DECIMAL(10,7),
    merchant_latitude DECIMAL(10,7),
    delivery_address VARCHAR(255),
    delivery_longitude DECIMAL(10,7),
    delivery_latitude DECIMAL(10,7),
    delivery_phone VARCHAR(20),
    delivery_name VARCHAR(50),
    rider_id BIGINT,
    rider_name VARCHAR(50),
    rider_phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING',
    estimated_distance DECIMAL(10,2),
    estimated_time INT,  -- 预估配送时间（分钟）
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 搜索相关

```sql
-- 搜索商家表（冗余表，用于搜索优化）
CREATE TABLE search_merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100),
    address VARCHAR(255),
    description TEXT,
    image_url VARCHAR(255),
    longitude DECIMAL(10,7),
    latitude DECIMAL(10,7),
    rating DECIMAL(2,1),
    monthly_sales INT,
    avg_delivery_time INT,
    categories VARCHAR(255),
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 搜索菜品表
CREATE TABLE search_dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dish_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100),
    price DECIMAL(10,2),
    description TEXT,
    image_url VARCHAR(255),
    category VARCHAR(50),
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 6.2 数据流转

```
商家创建/更新信息
    │
    ▼
MerchantService
    │ 写入 merchant 表
    │
    └──▶ syncMerchantToSearch()
            │ 写入 search_merchant 表
            └── 保持搜索数据与业务数据同步
```

**设计说明：**
- 搜索表（search_merchant / search_dish）是业务表的冗余副本
- 采用**同步写入**策略，商家/菜品变更时立即同步到搜索表
- 搜索服务独立部署，查询不影响主业务服务性能
- 支持后续扩展为 Elasticsearch 全文搜索

---

## 7. 安全设计

### 7.1 认证鉴权

#### JWT 认证流程

```
用户登录
    │
    ▼
AuthController.login()
    │ 验证用户名密码
    │ 生成 JWT Token
    │   ├── userId
    │   ├── username
    │   ├── role
    │   └── 过期时间
    │
    ▼
返回 Token 给前端
    │
    ▼
前端请求携带 Header: Authorization: Bearer <token>
    │
    ▼
Gateway AuthFilter
    ├── 1. 检查是否在白名单（登录、注册、公开接口）
    ├── 2. 提取 Authorization Header
    ├── 3. 验证 Token 签名和过期时间
    ├── 4. 解析 userId / username / role
    └── 5. 写入请求 Header: X-User-Id / X-Username / X-User-Role
    │
    ▼
下游服务从 Header 读取用户信息
```

**安全特性：**
- **无状态认证**：JWT 自包含用户信息，无需 Session 存储
- **网关统一鉴权**：所有请求经过 Gateway AuthFilter 统一校验
- **白名单机制**：登录、注册、公开查询接口免鉴权
- **角色控制**：支持 USER / MERCHANT / RIDER / ADMIN 四种角色
- **密码加密**：使用 BCrypt 算法加密存储

#### 角色升级机制

```
普通用户 (USER)
    │
    ├── PUT /user/upgrade-role { "role": "MERCHANT" }
    │       └── 更新数据库角色，签发新 JWT
    │
    └── PUT /user/upgrade-role { "role": "RIDER" }
            └── 更新数据库角色，签发新 JWT

注意：拒绝升级为 ADMIN（需管理员手动操作）
```

### 7.2 网关安全

```java
// AuthFilter - JWT 鉴权过滤器
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    // 白名单：无需鉴权的接口
    private static final List<String> WHITE_LIST = List.of(
        "/auth/login",      // 登录
        "/auth/register",   // 注册
        "/auth/sms/send",   // 发送验证码
        "/merchant/health", // 健康检查
        "/merchant/list",   // 商家列表（公开）
        "/dish/list",       // 菜品列表（公开）
        "/payment/callback" // 支付回调（验签而非JWT）
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 放行 OPTIONS 预检请求
        // 2. 白名单路径直接放行
        // 3. 提取并验证 JWT Token
        // 4. 解析用户信息写入 Header
        // 5. 鉴权失败返回 401
    }
}
```

**其他安全过滤器：**
- **RateLimitFilter**：基于 Sentinel 的接口限流，防止恶意请求
- **SecurityHeadersFilter**：注入安全响应头（X-Content-Type-Options, X-Frame-Options 等）
- **RequestLoggingFilter**：记录请求日志，便于审计和问题排查
- **TraceIdFilter**：注入链路追踪 ID，支持分布式调用链追踪

---

## 8. 前端设计

### 8.1 组件体系

#### Glassmorphism 设计系统

项目自研了一套 **Glassmorphism（毛玻璃）** 风格的 UI 组件库：

```
components/ui/
├── GlassButton.vue    # 毛玻璃按钮组件
├── GlassCard.vue      # 毛玻璃卡片容器
├── GlassInput.vue     # 毛玻璃输入框
├── GlassModal.vue     # 毛玻璃弹窗
├── GlassNav.vue       # 毛玻璃导航栏
├── GlassTag.vue       # 毛玻璃标签
├── LoadingBar.vue     # 加载进度条
└── Toast.vue          # 消息提示
```

**设计特点：**
- 半透明背景 + 模糊效果（backdrop-filter: blur）
- 渐变背景色（鼠尾草绿渐变 × 暖金 × 薄紫微缀）
- 微妙的边框和阴影
- 流畅的过渡动画

#### 公共业务组件

```
components/common/
├── DishCard.vue         # 菜品卡片
├── MerchantCard.vue     # 商家卡片
├── OrderStatusBadge.vue # 订单状态徽章
├── PriceDisplay.vue     # 价格展示
├── RatingStars.vue      # 评分星级
├── SkeletonLoader.vue   # 骨架屏加载
├── EmptyState.vue       # 空状态展示
└── LoadingSpinner.vue   # 加载动画
```

### 8.2 路由与权限

#### 路由结构

```
/
├── /                          # 首页
├── /login                     # 登录
├── /register                  # 注册
├── /user/                     # 用户端
│   ├── restaurants            # 商家列表
│   ├── restaurants/:id        # 商家详情
│   ├── cart                   # 购物车
│   ├── orders                 # 订单列表
│   ├── orders/:id             # 订单详情
│   ├── payment/:id            # 支付页面
│   ├── delivery/:id           # 配送追踪
│   ├── notifications          # 消息通知
│   ├── addresses              # 收货地址
│   └── profile                # 个人中心
├── /merchant/                 # 商家端
│   ├── dashboard              # 仪表盘
│   ├── dishes                 # 菜品管理
│   ├── orders                 # 订单管理
│   └── settings               # 商家设置
├── /rider/                    # 骑手端
│   ├── tasks                  # 配送任务
│   └── stats                  # 配送统计
└── /admin/                    # 管理后台
    ├── dashboard              # 数据概览
    ├── users                  # 用户管理
    ├── merchants              # 商家管理
    └── applications           # 申请审批
```

#### 路由守卫

```javascript
// 路由守卫：根据角色控制访问权限
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    // 需要登录的页面
    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
        return next('/login')
    }

    // 角色权限检查
    if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
        return next('/')  // 无权限，跳转首页
    }

    next()
})
```

---

## 9. 部署架构

### 9.1 容器化部署

#### Docker Compose 部署方案

```yaml
# docker-compose.yml - 本地开发环境
version: '3.8'
services:
  # 基础设施
  nacos:
    image: nacos/nacos-server:v2.3.1
    ports: ["8848:8848"]

  mysql:
    image: mysql:8.0
    ports: ["3306:3306"]

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  rabbitmq:
    image: rabbitmq:3.12-management
    ports: ["5672:5672", "15672:15672"]

  # 后端服务
  gateway:
    build: ./takeout-platform/gateway
    ports: ["9999:9999"]

  user-service:
    build: ./takeout-platform/user-service
    ports: ["8081:8081"]

  # ... 其他服务

  # 前端
  frontend:
    build: ./takeout-frontend-v2
    ports: ["3000:80"]

  # 监控
  prometheus:
    image: prom/prometheus
    ports: ["9090:9090"]

  grafana:
    image: grafana/grafana
    ports: ["3001:3000"]
```

#### Kubernetes 部署方案

```yaml
# k8s/gateway.yaml - 网关服务部署
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gateway
  namespace: takeout
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gateway
  template:
    spec:
      containers:
        - name: gateway
          image: takeout/gateway:latest
          ports:
            - containerPort: 9999
          env:
            - name: NACOS_SERVER_ADDR
              value: "nacos:8848"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 9999
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 9999
```

### 9.2 监控体系

#### Prometheus 指标采集

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'takeout-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['gateway:9999']

  - job_name: 'takeout-services'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets:
          - 'user-service:8081'
          - 'merchant-service:8083'
          - 'order-service:8084'
          - 'payment-service:8085'
          - 'notification-service:8086'
          - 'delivery-service:8087'
          - 'search-service:8088'
```

#### Grafana 监控面板

监控指标包括：
- **服务健康状态**：各微服务存活状态
- **请求 QPS**：每秒请求数量统计
- **响应时间**：P50/P90/P99 响应时间分布
- **错误率**：4xx/5xx 错误比例
- **JVM 指标**：堆内存、GC 频率、线程数
- **数据库连接池**：活跃连接数、等待队列
- **RabbitMQ 队列**：消息堆积量、消费速率

---

## 10. 项目特色与创新

### 10.1 微服务架构设计

**特色：**
- 8 个独立微服务，职责清晰，边界明确
- 7 个公共模块，统一基础设施，避免代码重复
- 服务间通过 Feign 调用和 RabbitMQ 消息通信，松耦合设计

**创新点：**
- 统一的 `common-security` 模块，网关鉴权 + 下游服务透明获取用户信息
- 统一的 `common-mq` 模块，消息常量和事件 DTO 集中管理
- `common-redis` 模块封装分布式 Session、缓存服务、验证码服务

### 10.2 智能配送调度

**特色：**
- 基于 Haversine 公式的地理距离计算
- 10km 搜索半径内的最近骑手匹配
- 预估配送时间算法（取餐时间 + 距离/速度）

**创新点：**
- 骑手实时位置追踪（RiderLocation 实体）
- 配送任务状态机完整流转
- 支付成功自动触发配送任务创建

### 10.3 事件驱动架构

**特色：**
- 支付成功事件异步通知订单服务和通知服务
- 订单取消事件异步通知通知服务
- 通知服务内置重试机制和死信队列

**创新点：**
- 统一的 MQ 常量管理（`MQConstants`）
- 死信队列兜底，防止消息丢失
- 支付事件 DTO 设计，携带完整上下文信息

### 10.4 网关安全体系

**特色：**
- 5 层过滤器链：链路追踪 → 安全日志 → 请求日志 → JWT 鉴权 → 限流
- 白名单机制，公开接口免鉴权
- 用户信息通过 Header 透传，下游服务无需重复解析 Token

**创新点：**
- `TraceIdFilter` 注入链路追踪 ID，支持分布式调用链追踪
- `SecurityHeadersFilter` 注入安全响应头，防御 XSS/Clickjacking
- `RateLimitFilter` 集成 Sentinel，支持动态限流规则

### 10.5 Glassmorphism UI 设计

**特色：**
- 自研毛玻璃风格组件库（GlassButton / GlassCard / GlassInput / GlassModal 等）
- 渐变色彩体系：鼠尾草绿 × 暖金 × 薄紫微缀
- 骨架屏加载（SkeletonLoader）提升用户体验
- 滚动动画（useScrollReveal）增加页面动感

**创新点：**
- `useScrollReveal` 自定义 Hook，支持异步加载内容后的重新观察
- `GlassModal` 支持 `v-model:visible` 双向绑定
- 响应式设计，适配桌面端和移动端

### 10.6 搜索服务独立化

**特色：**
- 搜索服务独立部署，与业务服务解耦
- 冗余搜索表（search_merchant / search_dish）优化查询性能
- 商家/菜品变更时同步写入搜索表

**创新点：**
- 采用同步写入策略，保证搜索数据实时性
- 独立的搜索服务端口（8088），可独立扩缩容
- 预留 Elasticsearch 扩展接口

### 10.7 完整的支付体系

**特色：**
- 支持支付宝和微信双支付通道
- 支持扫码支付、公众号支付、H5 支付多种方式
- 支付流水表记录完整生命周期，支持对账

**创新点：**
- 乐观锁（`@Version`）防止并发更新冲突
- 逻辑删除（`@TableLogic`）保护数据完整性
- 支付过期自动关闭机制
- 死信队列处理支付异常消息
- 回调数据原始记录，便于问题排查

### 10.8 容器化与监控

**特色：**
- Docker Compose 一键启动本地开发环境
- Kubernetes 生产级部署方案
- Prometheus + Grafana 完整监控体系

**创新点：**
- 分层 Docker Compose：infra.yml / services.yml / server.yml
- 自动化部署脚本（deploy.py）
- Grafana 仪表盘预配置，开箱即用
- Actuator + Micrometer 暴露应用指标

---

## 附录

### A. 服务端口一览

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend | 3000 | 前端应用 |
| gateway | 9999 | API 网关 |
| user-service | 8081 | 用户服务 |
| merchant-service | 8083 | 商家服务 |
| order-service | 8084 | 订单服务 |
| payment-service | 8085 | 支付服务 |
| notification-service | 8086 | 通知服务 |
| delivery-service | 8087 | 配送服务 |
| search-service | 8088 | 搜索服务 |
| mysql | 3306 | 数据库 |
| redis | 6379 | 缓存 |
| rabbitmq | 5672 | 消息队列 |
| rabbitmq-management | 15672 | MQ 管理界面 |
| nacos | 8848 | 注册中心 |
| prometheus | 9090 | 监控 |
| grafana | 3001 | 监控面板 |

### B. 环境变量配置

```bash
# 服务器配置
SERVER_IP=your-server-ip
NACOS_SERVER_ADDR=localhost:8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=your-nacos-password

# 数据库配置
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_PASSWORD=your-mysql-password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# RabbitMQ 配置
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=takeout
RABBITMQ_PASSWORD=your-rabbitmq-password

# 前端配置
VITE_API_BASE_URL=http://your-server-ip:9999
```

### C. 技术栈版本汇总

| 技术栈 | 版本 |
|--------|------|
| JDK | 21 |
| Spring Boot | 3.0.13 |
| Spring Cloud | 2022.0.0 |
| Spring Cloud Alibaba | 2022.0.0.0 |
| Nacos | 2.3.1 |
| MyBatis-Plus | 3.5.5 |
| MySQL | 8.0 |
| Redis | 7.x |
| RabbitMQ | 3.12 |
| Vue.js | 3.4 |
| Vite | 5.0 |
| Vue Router | 4.2 |
| Pinia | 2.1 |
| Node.js | 18+ |
| Maven | 3.8+ |

---

> 文档版本：v2.0
> 最后更新：2026-06-09
> 维护者：Maffile-Enky
