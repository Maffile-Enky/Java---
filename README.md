# 开发规范

## 前言
### 编写代码前请先拉取dev分支最新代码
### 仓库与分支规范：
约定分支规则：
- main（主分支，仅存可运行成品）  
- dev（开发分支，所有人代码由负责人合并到这里）  
- feature/xxx（个人功能分支，如feature/user-auth/feature/video-upload，所有人代码仅提交到个人分支）  
- 严禁直接往 main/dev 推代码，所有 PR 合并均由项目负责人单独完成，严禁私自合并。  

### 提交信息规范：
约定格式：【类型: 描述】如：
- feat: 实现用户注册接口
- fix: 修复登录Token过期问题
- docs: 补充接口文档
- 每条提交只做一个功能。

### 文件目录规范：
严格按照文档推荐的包结构统一，所有人本地项目目录一致，避免合并代码时路径冲突

# 一、总体架构规范

## 1. 架构选型

* 架构模式：**微服务 + 分层**
* 技术栈：

  * Spring Boot 3.x（Jakarta）
  * Spring Cloud Alibaba（Nacos 2.4）
  * Gateway：Spring Cloud Gateway
  * ORM：MyBatis-Plus 
  * 缓存：Redis
  * MQ：RabbitMQ
  * ES

 Nacos核心能力：

* 服务注册/发现
* 配置中心（动态刷新） ([Nacos 官网][1])

---

## 2. 标准分层结构（按业务模块）

```
com.xxx.project
├── api            # DTO/VO/Feign接口
├── controller     # 接口层
├── service        # 业务层
├── domain         # 领域模型（可选）
├── repository     # 持久层
├── config         # 配置类
├── common         # 工具/统一返回/异常
```

### 强制约束

* Controller  不允许写业务逻辑
* Service  只处理业务
* Repository  只操作数据库
* DTO / Entity 严格分离

---

# 二、Nacos 2.4 集群规范（重点）

## 1. 集群部署模型（生产必须）

### 推荐拓扑

```
Nginx（VIP）
   ↓
Nacos Cluster (3节点)
   ↓
MySQL（主从）
```

## 2. 核心机制

* Nacos是**去中心化集群（无主节点）** ([springcloud.io][2])
* 数据存储：

  * MySQL：权威数据源
  * 本地磁盘：缓存副本 ([springcloud.io][2])
* 配置同步：

  * 节点间 HTTP 通知同步


---

## 3. 配置规范

### application.yml（统一写法）

```yaml
spring:
  application:
    name: user-service

  config:
    import:
      - nacos:common.yaml
      - nacos:user-service.yaml

  cloud:
    nacos:
      server-addr: nacos1:8848,nacos2:8848,nacos3:8848
      username: nacos
      password: nacos
```

 Spring Boot 3 使用：

```
spring.config.import
```

替代旧 bootstrap.yml ([阿里云][3])

---

## 4. Nacos配置设计规范

| 类型        | 规范                |
| --------- | ----------------- |
| DataId    | `${服务名}-dev.yaml`     |
| Group     | DEV / TEST / PROD |
| Namespace | 环境隔离              |
| 公共配置      | common-dev.yaml       |

### 示例

```
common-dev.yaml
user-service-dev.yaml
order-service-dev.yaml
```

---

## 5. 服务注册规范

```yaml
spring:
  cloud:
    nacos:
      discovery:
        namespace: dev
        group: DEFAULT_GROUP
```

---

# 三、Spring Boot 3 开发规范

## 1. Controller规范

```java
@RestController
@RequestMapping("/user")
public class UserController {
}
```

### 要求：

* 返回统一结构
* 不直接返回 Entity
* 必须加参数校验

---

## 2. 统一返回结构

```java
public class Result<T> {
    private int code;
    private String msg;
    private T data;
}
```

---

## 3. 全局异常处理（必须）

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

---

## 4. 参数校验

```java
@NotNull
@Size(min = 1, max = 50)
```

---

## 5. 日志规范

* 使用 `Slf4j`
* 必须打印：

  * traceId
  * 请求参数
  * 异常堆栈

---

# 四、微服务通信规范

## 1. 调用方式

* 推荐：

  * OpenFeign（同步）
  * MQ（异步）

## 2. 超时与重试

* 必须配置：

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 3000
        readTimeout: 5000
```

---

## 3. 服务调用原则

*  禁止循环调用
*  禁止跨多级调用（A→B→C→D）
*  推荐扁平调用

---

# 五、数据库规范

## 1. 基本规则

* 一服务一数据库（强制）
* 禁止跨库 join

## 2. 字段规范

* 主键：`id`（雪花算法）
* 时间字段：

  * create_time
  * update_time

---

# 六、缓存规范（Redis）

## Key命名

```
业务:模块:ID
user:info:1001
```

## 设计原则

* 先查缓存 → 再查DB
* 设置过期时间（防止雪崩）

---

# 七、安全规范

## 必须项

* JWT / OAuth2
* 接口签名（可选）
* 防重放（时间戳）

---

# 八、网关规范

## Spring Cloud Gateway

统一处理：

* 鉴权
* 日志
* 限流（Redis）
* 跨域

---

# 九、部署规范


---

## docker-compose示例（核心）

```yaml
services:
  nacos1:
    image: nacos/nacos-server:v2.4
    environment:
      - MODE=cluster
```

---

# 十、监控与运维规范

## 必须组件

* Spring Boot Actuator
* Prometheus + Grafana
* SkyWalking（链路追踪）

---

# 十一、代码质量规范

## 必须执行

* 单元测试
* 接口文档（Knife4J）
* 静态扫描

---

# 十二、核心设计原则总结（重点）

1. 配置必须外部化（Nacos）
2. 服务必须无状态
3. 数据必须隔离
4. 接口必须幂等
5. 调用必须可降级

---




[1]: https://nacos.io/en/docs/next/v2/ecology/use-nacos-with-spring?utm_source=chatgpt.com "Nacos with Spring Projects | Nacos"
[2]: https://www.springcloud.io/post/2022-04/nacos-principle-and-source-code/?utm_source=chatgpt.com "Nacos Configuration Center Cluster Principle and Source Code Analysis - Spring Cloud"
[3]: https://www.alibabacloud.com/blog/best-practices-for-dynamic-configuration-with-spring-cloud-nacos-and-kms_601998?utm_source=chatgpt.com "Best Practices for Dynamic Configuration with Spring Cloud, Nacos, and KMS - Alibaba Cloud Community"
