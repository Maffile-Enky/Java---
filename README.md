# Takeout Platform - 外卖平台

> 基于 Spring Cloud 微服务架构的外卖点餐系统，支持用户点餐、商家管理、订单处理、在线支付、骑手配送等完整业务流程。

---

> 项目预览见 http://121.41.76.29
## 项目概览

| 维度 | 说明 |
|------|------|
| **架构** | Spring Cloud 微服务 + Vue 3 前后端分离 |
| **服务数** | 8 个微服务 + 1 个 API 网关 |
| **部署** | Docker Compose 容器化，支持多服务器部署 |
| **监控** | Prometheus + Grafana 可视化监控 |

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.6 | 应用框架 |
| Spring Cloud | 2025.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0 | 阿里巴巴微服务组件 |
| Nacos | 2.3.x | 服务注册与配置中心 |
| Spring Cloud Gateway | - | API 网关 |
| Sentinel | - | 流量控制与熔断降级 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.x | 缓存与会话管理 |
| RabbitMQ | 3.12 | 消息队列 |
| JWT | - | 用户认证 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.4 | 渐进式 JavaScript 框架 |
| Vite | 5.0 | 构建工具 |
| Vue Router | 4.2 | 路由管理 |
| Pinia | 2.1 | 状态管理 |
| Axios | 1.6 | HTTP 客户端 |

### 运维

| 技术 | 说明 |
|------|------|
| Docker | 容器化部署 |
| Docker Compose | 服务编排 |
| Prometheus | 监控数据采集 |
| Grafana | 监控可视化面板 |

## 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户端 / 商家端                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Nginx (反向代理 / 静态资源)                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              Spring Cloud Gateway (API 网关 / 鉴权)              │
└────────────────────────────┬────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  用户服务     │   │  商家服务     │   │  订单服务     │
│  8081        │   │  8083        │   │  8084        │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       └──────────────────┼──────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  支付服务     │  │  配送服务     │  │  通知服务     │
│  8085        │  │  8087        │  │  8086        │
└──────────────┘  └──────────────┘  └──────────────┘
        │                 │                 │
        └─────────────────┼─────────────────┘
                          ▼
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│    MySQL     │  │    Redis     │  │   RabbitMQ   │
│   3306       │  │   6379       │  │   5672       │
└──────────────┘  └──────────────┘  └──────────────┘
```

## 项目结构

```
Java---/
├── takeout-platform/                    # 后端微服务
│   ├── common/                          # 公共模块
│   │   ├── common-core/                 # 核心工具类
│   │   ├── common-web/                  # Web 公共配置
│   │   ├── common-redis/                # Redis 配置
│   │   ├── common-security/             # 安全认证
│   │   ├── common-feign/                # Feign 客户端
│   │   └── common-mq/                   # 消息队列
│   ├── gateway/                         # API 网关 (9999)
│   ├── user-service/                    # 用户服务 (8081)
│   ├── merchant-service/                # 商家服务 (8083)
│   ├── order-service/                   # 订单服务 (8084)
│   ├── payment-service/                 # 支付服务 (8085)
│   ├── notification-service/            # 通知服务 (8086)
│   ├── delivery-service/                # 配送服务 (8087)
│   ├── search-service/                  # 搜索服务 (8088)
│   └── sql/                             # 数据库脚本
│
├── takeout-frontend-v2/                 # 前端项目
│   ├── src/
│   │   ├── views/                       # 页面组件
│   │   ├── components/                  # 公共组件
│   │   ├── router/                      # 路由配置
│   │   ├── stores/                      # 状态管理
│   │   ├── api/                         # API 接口
│   │   └── utils/                       # 工具函数
│   └── package.json
│
├── config/                              # 配置文件
│   ├── .env.example                     # 环境变量模板
│   └── server.env.example               # 服务器配置模板
│
├── monitoring/                          # 监控配置
│   ├── prometheus.yml
│   ├── grafana-datasource.yml
│   └── grafana-dashboard.json
│
├── docker-compose.yml                   # 本地开发 Docker 配置
├── docker-compose.server.yml.example    # 服务器部署配置模板
├── deploy.py.example                    # 部署脚本模板
└── SECURITY.md                          # 安全配置指南
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.x
- RabbitMQ 3.12

### 本地开发

#### 1. 克隆项目

```bash
git clone <your-repo-url>
cd Java---
```

#### 2. 启动基础设施

```bash
# 启动 Nacos、MySQL、Redis、RabbitMQ
docker-compose up -d nacos mysql redis rabbitmq
```

#### 3. 初始化数据库

```bash
# 等待 MySQL 启动完成后，执行初始化脚本
mysql -h localhost -u root -p < takeout-platform/sql/init.sql
```

#### 4. 启动后端服务

```bash
# 编译项目
cd takeout-platform
mvn clean install -DskipTests

# 启动网关
mvn spring-boot:run -pl gateway

# 启动用户服务（新终端）
mvn spring-boot:run -pl user-service

# 启动其他服务...
mvn spring-boot:run -pl merchant-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl delivery-service
mvn spring-boot:run -pl notification-service
mvn spring-boot:run -pl search-service
```

#### 5. 启动前端

```bash
cd takeout-frontend-v2
npm install
npm run dev
```

前端访问地址: http://localhost:3000

#### 6. 访问服务

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| API 网关 | http://localhost:9999 |
| Nacos 控制台 | http://localhost:8848/nacos |
| RabbitMQ 管理 | http://localhost:15672 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3001 |

### 使用 Docker Compose 一键启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f gateway
```

## 服务器部署

### 1. 准备配置文件

```bash
# 复制配置模板
cp config/.env.example config/.env
cp config/server.env.example config/server.env
cp docker-compose.server.yml.example docker-compose.server.yml
cp docker-compose.infra.yml.example docker-compose.infra.yml
cp docker-compose.services.yml.example docker-compose.services.yml
cp deploy.py.example deploy.py
cp takeout-frontend-v2/.env.production.example takeout-frontend-v2/.env.production
```

### 2. 编辑配置文件

编辑以下文件，填入真实的服务器 IP 和密码：

```bash
# 编辑环境变量
vi config/.env

# 编辑服务器配置
vi config/server.env

# 编辑 Docker Compose 配置
vi docker-compose.server.yml
```

### 3. 部署基础设施

```bash
# 部署 Nacos、MySQL、Redis、RabbitMQ
docker-compose -f docker-compose.infra.yml up -d
```

### 4. 部署后端服务

```bash
# 构建项目
cd takeout-platform
mvn clean package -DskipTests

# 部署服务
docker-compose -f docker-compose.services.yml up -d
```

### 5. 部署前端

```bash
cd takeout-frontend-v2

# 配置生产环境 API 地址
vi .env.production

# 构建前端
npm run build

# 部署到服务器
python deploy.py
```

### 6. 使用部署脚本

```bash
# 配置环境变量
export DEPLOY_FRONTEND_SERVER_IP=<your-frontend-server-ip>
export DEPLOY_BACKEND_SERVER_IP=<your-backend-server-ip>
export DEPLOY_SSH_PASSWORD=<your-ssh-password>

# 一键部署
python deploy.py
> 部署脚本未维护，请自行完善
```


## 配置说明

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_IP` | 服务器 IP 地址 | - |
| `NACOS_SERVER_ADDR` | Nacos 服务地址 | `localhost:8848` |
| `NACOS_USERNAME` | Nacos 用户名 | `nacos` |
| `NACOS_PASSWORD` | Nacos 密码 | - |
| `MYSQL_HOST` | MySQL 主机 | `localhost` |
| `MYSQL_PORT` | MySQL 端口 | `3306` |
| `MYSQL_PASSWORD` | MySQL 密码 | - |
| `REDIS_HOST` | Redis 主机 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | - |
| `RABBITMQ_HOST` | RabbitMQ 主机 | `localhost` |
| `RABBITMQ_PORT` | RabbitMQ 端口 | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ 用户名 | `takeout` |
| `RABBITMQ_PASSWORD` | RabbitMQ 密码 | - |

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
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
| rabbitmq-management | 15672 | RabbitMQ 管理界面 |
| nacos | 8848 | 注册中心 |
| prometheus | 9090 | 监控 |
| grafana | 3001 | 监控面板 |




### 开发

1. 本地开发使用 `application-local.yml` 配置文件
2. 生产环境必须设置所有必需的环境变量
3. 提交代码前确保所有测试通过
4. 遵循代码规范，使用统一的代码风格

### 部署

1. 部署前确保所有配置文件已正确设置
2. 首次部署需要初始化数据库
3. 监控服务健康状态，及时处理异常
4. 定期备份数据库和重要数据

### 性能

1. 生产环境建议配置适当 JVM 参数
2. 合理配置数据库连接池大小
3. 使用 Redis 缓存热点数据
4. 监控服务响应时间和错误率

### 技术
详细技术架构见 [PROJECT_OVERVIEW.md](./PROJECT_OVERVIEW.md)

## 常见问题

### Q: 无法连接到 Nacos

检查 `NACOS_SERVER_ADDR` 环境变量是否正确设置，确保 Nacos 服务已启动。

### Q: 数据库连接失败

检查 `MYSQL_HOST`、`MYSQL_PORT`、`MYSQL_PASSWORD` 环境变量，确保 MySQL 服务可访问。

### Q: 前端无法访问 API

检查 `VITE_API_BASE_URL` 或 `VITE_DEV_SERVER_IP` 环境变量，确保 API 网关已启动。

### Q: 服务启动失败

查看服务日志：
```bash
docker-compose logs -f service-name
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request



## 致谢

感谢所有为本项目做出贡献的开发者！
