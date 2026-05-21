# 安全配置指南

## 概述

为了保护敏感信息（服务器IP、密码等）不被提交到GitHub，项目使用了环境变量和模板文件的方式。

## 快速开始

### 1. 本地开发

本地开发不需要额外配置，默认使用 `localhost`。

### 2. 服务器部署

#### 步骤1：复制模板文件

```bash
# 复制环境变量模板
cp config/.env.example config/.env
cp config/server.env.example config/server.env

# 复制 Docker Compose 模板
cp docker-compose.server.yml.example docker-compose.server.yml
cp docker-compose.infra.yml.example docker-compose.infra.yml
cp docker-compose.services.yml.example docker-compose.services.yml

# 复制部署脚本模板
cp deploy.py.example deploy.py

# 复制前端环境变量模板
cp takeout-frontend-v2/.env.production.example takeout-frontend-v2/.env.production
```

#### 步骤2：编辑配置文件

编辑以下文件，填入真实的服务器IP和密码：

- `config/.env` - 服务器基础配置
- `config/server.env` - 服务器环境变量
- `docker-compose.server.yml` - 服务器部署配置
- `docker-compose.infra.yml` - 基础设施层配置
- `docker-compose.services.yml` - 业务服务层配置
- `deploy.py` - 部署脚本
- `takeout-frontend-v2/.env.production` - 前端生产环境配置

## 环境变量说明

### 服务器配置

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `SERVER_IP` | 服务器IP地址 | `47.99.34.251` |
| `NACOS_SERVER_ADDR` | Nacos服务地址 | `47.99.34.251:8848` |
| `NACOS_USERNAME` | Nacos用户名 | `nacos` |
| `NACOS_PASSWORD` | Nacos密码 | `nacos` |

### 数据库配置

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `MYSQL_HOST` | MySQL主机地址 | `47.99.34.251` |
| `MYSQL_PORT` | MySQL端口 | `3306` |
| `MYSQL_PASSWORD` | MySQL密码 | `your-mysql-password` |
| `MYSQL_ROOT_PASSWORD` | MySQL root密码 | `your-root-password` |

### Redis配置

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `REDIS_HOST` | Redis主机地址 | `47.99.34.251` |
| `REDIS_PORT` | Redis端口 | `6379` |
| `REDIS_PASSWORD` | Redis密码 | `your-redis-password` |

### RabbitMQ配置

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `RABBITMQ_HOST` | RabbitMQ主机地址 | `47.99.34.251` |
| `RABBITMQ_PORT` | RabbitMQ端口 | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ用户名 | `takeout` |
| `RABBITMQ_PASSWORD` | RabbitMQ密码 | `your-rabbitmq-password` |

### 前端配置

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `VITE_API_BASE_URL` | API基础URL | `http://47.99.34.251:9999` |
| `VITE_DEV_SERVER_IP` | 开发服务器IP | `47.99.34.251` |

## 文件说明

### 模板文件（可提交到GitHub）

- `config/.env.example` - 环境变量模板
- `config/server.env.example` - 服务器环境变量模板
- `docker-compose.server.yml.example` - 服务器部署配置模板
- `docker-compose.infra.yml.example` - 基础设施层配置模板
- `docker-compose.services.yml.example` - 业务服务层配置模板
- `deploy.py.example` - 部署脚本模板
- `takeout-frontend-v2/.env.production.example` - 前端生产环境配置模板

### 配置文件（不提交到GitHub）

- `config/.env` - 环境变量配置
- `config/server.env` - 服务器环境变量
- `docker-compose.server.yml` - 服务器部署配置
- `docker-compose.infra.yml` - 基础设施层配置
- `docker-compose.services.yml` - 业务服务层配置
- `deploy.py` - 部署脚本
- `takeout-frontend-v2/.env.production` - 前端生产环境配置

## 注意事项

1. **永远不要**将包含真实密码的文件提交到GitHub
2. **永远不要**在代码中硬编码服务器IP或密码
3. **始终使用**环境变量来配置敏感信息
4. **定期更换**密码和密钥
5. **使用强密码**，避免使用简单的密码如 `123456`

## 部署命令

### 本地开发

```bash
# 启动基础设施
docker-compose up -d

# 启动后端服务
cd takeout-platform
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl user-service
# ... 其他服务

# 启动前端
cd takeout-frontend-v2
npm run dev
```

### 服务器部署

```bash
# 使用环境变量部署
export SERVER_IP=your-server-ip
export MYSQL_PASSWORD=your-mysql-password
export REDIS_PASSWORD=your-redis-password
export RABBITMQ_PASSWORD=your-rabbitmq-password

# 启动基础设施
docker-compose -f docker-compose.infra.yml up -d

# 启动业务服务
docker-compose -f docker-compose.services.yml up -d

# 或者使用统一的部署脚本
python deploy.py
```

## 故障排除

### 问题：无法连接到Nacos

检查 `NACOS_SERVER_ADDR` 环境变量是否正确设置。

### 问题：无法连接到数据库

检查 `MYSQL_HOST`、`MYSQL_PORT`、`MYSQL_PASSWORD` 环境变量是否正确设置。

### 问题：前端无法访问API

检查 `VITE_API_BASE_URL` 环境变量是否正确设置。

## 安全建议

1. 使用密钥管理服务（如AWS Secrets Manager、HashiCorp Vault）来管理敏感信息
2. 定期轮换密码和密钥
3. 限制服务器访问权限
4. 启用防火墙，只开放必要的端口
5. 定期更新依赖包，修复安全漏洞
