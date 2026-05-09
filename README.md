
---

## 运行方法

### 环境要求

- **JDK 21**
- **Maven 3.9+**
- **Node.js 18+** (前端)
- **Docker & Docker Compose** (容器化部署)

### 本地运行

#### 1. 启动基础设施

确保以下服务已启动：
- Nacos (默认端口: 8848)
- MySQL (默认端口: 3306)
- Redis (默认端口: 6379)

#### 2. 构建并启动后端服务

```bash
# 进入后端项目目录
cd takeout-platform

# 构建所有服务（跳过测试）
mvn clean package -DskipTests -pl common/common-core,common/common-web,common/common-security,common/common-redis,common/common-mq,common/common-feign,gateway,user-service,merchant-service,order-service -am

# 启动各服务（按顺序）
java -jar gateway/target/gateway-1.0.0-SNAPSHOT.jar
java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar
java -jar merchant-service/target/merchant-service-1.0.0-SNAPSHOT.jar
java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar
```

#### 3. 启动前端服务

```bash
# 进入前端项目目录
cd takeout-frontend

# 安装依赖
npm install

# 开发模式运行（热更新）
npm run dev

# 或构建生产版本
npm run build
```

#### 4. 访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 (开发) | http://localhost:5173 |
| 前端页面 (生产) | http://localhost:80 |
| API网关 | http://localhost:9999 |
| Nacos控制台 | http://localhost:8848/nacos |

---

### 服务器部署

#### 方式一：Docker Compose 一键部署

```bash
# 启动所有服务（包含基础设施）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [服务名]

# 停止所有服务
docker-compose down
```

#### 方式二：手动构建Docker镜像

```bash
# 1. 构建后端jar包
cd takeout-platform
mvn clean package -DskipTests -pl common/common-core,common/common-web,common/common-security,common/common-redis,common/common-mq,common/common-feign,gateway,user-service,merchant-service,order-service -am

# 2. 构建前端
cd ../takeout-frontend
npm install && npm run build

# 3. 构建Docker镜像
cd ..
docker build -t takeout-gateway:v1.0 ./takeout-platform/gateway
docker build -t takeout-user:v1.0 ./takeout-platform/user-service
docker build -t takeout-merchant:v1.0 ./takeout-platform/merchant-service
docker build -t takeout-order:v1.0 ./takeout-platform/order-service
docker build -t takeout-frontend:v1.0 ./takeout-frontend

# 4. 打latest标签
docker tag takeout-gateway:v1.0 takeout-gateway:latest
docker tag takeout-user:v1.0 takeout-user:latest
docker tag takeout-merchant:v1.0 takeout-merchant:latest
docker tag takeout-order:v1.0 takeout-order:latest
docker tag takeout-frontend:v1.0 takeout-frontend:latest
```

#### 离线部署（导出/导入镜像）

```bash
# 导出所有镜像到一个文件
docker save -o takeout-images-v1.0.tar \
  takeout-gateway:v1.0 takeout-gateway:latest \
  takeout-user:v1.0 takeout-user:latest \
  takeout-merchant:v1.0 takeout-merchant:latest \
  takeout-order:v1.0 takeout-order:latest \
  takeout-frontend:v1.0 takeout-frontend:latest

# 在目标服务器导入镜像
docker load -i takeout-images-v1.0.tar
```

#### 服务器访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://服务器IP:80 |
| API网关 | http://服务器IP:9999 |
| Nacos控制台 | http://服务器IP:8848/nacos |

---

## 常见问题

### Q1: Maven构建失败：JAVA_HOME未正确设置

```bash
# Windows
set JAVA_HOME=C:\path\to\jdk-21

# Linux/Mac
export JAVA_HOME=/path/to/jdk-21
```

### Q2: 前端nginx报错：host not found in upstream "gateway"

这是正常现象。前端需要与gateway服务在同一Docker网络中才能解析服务名。使用 `docker-compose up` 启动即可解决。

### Q3: 服务无法注册到Nacos

1. 检查Nacos是否正常运行
2. 检查服务配置中的Nacos地址是否正确
3. 检查网络是否通畅

### Q4: 数据库连接失败

1. 确认MySQL服务已启动
2. 检查数据库账号密码是否正确（默认: root/123456）
3. 确认数据库已初始化（执行sql目录下的脚本）

