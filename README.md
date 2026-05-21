

**开发流程**：所有人先拉取dev分支最新代码，在个人feature/xxx分支开发，完成自测+单元测试后提交PR，由架构负责人统一Review并合并到dev。严格执行包结构、统一返回、异常处理、日志规范等。

**5人后端团队详细分工**（均衡难度，成员1把控全局架构与集成，成员3负责最复杂订单模块，成员5负责实时调度模块）：

成员1（架构负责人/技术领头，建议由经验最丰富者担任，全局把控）：  
负责整体项目架构设计、Nacos集群与公共配置、网关服务、用户服务、代码审查、集成测试、监控部署以及跨服务一致性框架。  
具体部分包括：  
- 网关服务全部内容：Spring Cloud Gateway路由配置、过滤器链（全局鉴权、日志记录、Sentinel限流、跨域处理、请求头透传）、统一入口安全策略。  
- 用户服务全部内容：用户注册/登录（手机号、微信授权、JWT生成）、用户中心（个人信息、地址管理）、RBAC权限体系（角色定义：用户/商家/骑手/管理员，权限拦截）、OAuth2支持、分布式Session与Redis缓存框架。  
- Nacos相关：3节点集群部署配置、公共配置common-dev.yaml（统一日志、数据库连接池、Redis/Sentinel等）、各服务专属配置（如user-service-dev.yaml）、动态刷新机制。  
- 全局基础框架：统一Result<T>返回结构、全局异常处理GlobalExceptionHandler、traceId日志链路（MDC + SkyWalking）、接口签名与防重放机制、Docker Compose多服务编排脚本、Kubernetes基础部署yaml、Prometheus + Grafana监控面板搭建、每周代码Review与集成联调把控。  
- 跨服务公共工作：Seata全局事务协调器配置、Sentinel规则统一管理、SkyWalking探针接入。

成员2：  
负责商家服务和菜品/购物车服务全部或主要部分。  
具体部分包括：  
- 商家服务：商家入驻流程（信息提交、审核状态）、店铺管理（基本信息、营业时间、配送范围设置、地理位置）、商家后台登录与权限、店铺统计基础。  
- 菜品管理：菜品分类CRUD、多规格SKU管理（口味、价格、库存组合）、菜品上下架、图片上传与OSS集成、菜品搜索基础逻辑。  
- 购物车服务：Redis实现购物车（添加、删除、修改数量、合并本地购物车）、购物车预览计算（总价、优惠）、库存预检查（Redis分布式锁）。  
- 商家端订单处理：接单、出餐确认、拒单逻辑（通过MQ与订单服务交互）。  
- 对应工作：merchant-service的Nacos配置、数据库表设计（商家表、菜品表、规格表、库存表）、MyBatis-Plus Mapper与Service实现、接口幂等处理、单元测试覆盖、与订单服务的Feign/MQ调用定义。

成员3（承担最复杂、最困难模块）：  
负责订单服务全部内容（整个项目技术难度最高的核心模块）。  
具体部分包括：  
- 订单全流程：订单创建与预览（购物车转订单、地址选择、优惠计算）、订单状态机全生命周期管理（下单 → 待支付 → 已支付 → 接单 → 出餐 → 配送中 → 已完成/取消/退款，使用Spring Statemachine实现状态流转与事件监听）。  
- 分布式事务处理：Seata Saga/AT模式实现下单扣库存、扣优惠、创建订单等跨服务一致性（与商家服务、用户服务、支付服务配合）。  
- 订单管理：订单查询（用户端/商家端/管理员）、复杂条件搜索与ES同步、订单分库分表策略（按用户ID/商家ID/时间维度）、订单项明细管理。  
- 事件驱动：订单事件发布（Kafka/RabbitMQ，支付成功、状态变更等事件）、事件消费处理。  
- 高并发优化：防重复下单（分布式锁 + 幂等Token）、限流保护、库存最终一致性保障。  
- 对应工作：order-service完整包结构实现、Nacos配置、数据库设计（订单主表、订单项表、状态历史表）、状态机配置类、Seata事务注解使用、与支付/配送服务的通信接口定义、性能测试重点覆盖（下单高峰场景）。

成员4：  
负责支付服务和通知服务全部内容。  
具体部分包括：  
- 支付服务：支付宝/微信沙箱支付集成（统一下单、回调验签、退款接口、对账单生成）、支付流水记录、支付状态机与订单状态同步（通过MQ通知订单服务）、支付异常处理与重试。  
- 通知服务：实时消息推送体系（WebSocket订单状态变更推送、短信/APP推送模板）、异步事件消费（支付成功后触发通知、订单取消通知等）、消息可靠性（RabbitMQ重试 + 死信队列）、通知日志与审计。  
- 对应工作：payment-service与notification-service的Nacos配置、数据库表（支付流水表、通知记录表）、Feign/MQ调用定义、回调安全校验、与订单服务的分布式事务配合、单元测试（支付回调模拟）。

成员5（承担最具技术亮点模块）：  
负责骑手服务、配送/调度服务以及实时位置功能全部内容（项目中最前沿的实时与算法部分）。  
具体部分包括：  
- 骑手服务：骑手注册认证、个人信息管理、忙碌状态切换、骑手端登录与权限。  
- 配送调度：配送任务创建（从订单服务接收）、智能调度算法实现（距离优先 + 负载均衡 + Redis ZSet抢单/派单 + 简单贪心算法）、派单/抢单逻辑、ETA预计送达时间计算。  
- 实时位置：骑手位置上报（WebSocket/MQTT实时接口）、轨迹跟踪记录、地理围栏判断、与高德/腾讯地图API集成（距离、路线规划）。  
- 骑手端功能：骑手订单列表、接单确认、导航集成、配送完成上报（触发订单状态变更MQ）。  
- 对应工作：delivery-service完整实现、Nacos配置、数据库表（骑手表、配送任务表、位置轨迹表）、实时通信配置（WebSocket Handler + MQTT Broker集成）、调度算法核心类、Redis ZSet使用、与订单服务的MQ事件消费、实时大屏数据基础支持（可选与监控配合）、性能测试（骑手抢单高峰）。

**跨成员协作要求**（所有人共同参与）：  
- 数据库整体设计（雪花ID主键、create_time/update_time字段、索引优化、ER关系）。  
- 统一接口规范与Knife4J文档编写。  
- 单元测试、集成测试（Testcontainers模拟Redis/MQ）。  
- 性能压测（JMeter重点覆盖订单创建、骑手抢单）。  
- 日志与traceId统一、最终监控面板调优（Grafana展示订单量、骑手分布、事务成功率等）。  
- 所有服务必须实现服务无状态、配置外部化、接口幂等、可降级。
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

---

## 服务器IP配置

项目采用集中式配置管理，所有服务器IP地址统一存放在 `config/` 目录下。

### 配置文件说明

```
config/
├── server.env          # 真实配置（不会上传到GitHub）
└── server.env.example  # 配置模板（上传到GitHub，供其他人使用）
```

### 修改服务器IP

#### 方式一：直接修改配置文件

编辑 `config/server.env` 文件：

```bash
# 服务器基础IP
SERVER_IP=your-server-ip

# Nacos配置
NACOS_SERVER_ADDR=your-server-ip:8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos

# MySQL配置
MYSQL_HOST=your-server-ip
MYSQL_PORT=3306
MYSQL_PASSWORD=your-mysql-password

# Redis配置
REDIS_HOST=your-server-ip
REDIS_PORT=6379
```

#### 方式二：新环境部署

```bash
# 1. 复制配置模板
cp config/server.env.example config/server.env

# 2. 编辑配置文件，填入真实IP
vim config/server.env
```

##### 配置生效方式

| 运行方式 | 配置来源 |
|---------|---------|
| 本地开发（直连服务器） | `config/server.env` 中的IP |
| Docker Compose | 自动读取 `config/server.env` |
| 环境变量 | 系统环境变量优先级最高 |

##### 注意事项

- `config/server.env` 已被 `.gitignore` 排除，不会上传到GitHub
- 首次部署时需要手动创建 `config/server.env` 文件
- Docker Compose 会优先使用 `config/server.env`，无配置时使用容器服务名（适用于本地开发）


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
