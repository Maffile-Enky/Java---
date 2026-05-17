FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache supervisor

WORKDIR /app

# 复制所有7个后端服务的JAR
COPY jars/user-service-1.0.0-SNAPSHOT.jar /app/services/user-service.jar
COPY jars/merchant-service-1.0.0-SNAPSHOT.jar /app/services/merchant-service.jar
COPY jars/order-service-1.0.0-SNAPSHOT.jar /app/services/order-service.jar
COPY jars/payment-service-1.0.0-SNAPSHOT.jar /app/services/payment-service.jar
COPY jars/delivery-service-1.0.0-SNAPSHOT.jar /app/services/delivery-service.jar
COPY jars/search-service-1.0.0-SNAPSHOT.jar /app/services/search-service.jar
COPY jars/notification-service-1.0.0-SNAPSHOT.jar /app/services/notification-service.jar

# 复制 supervisord 配置
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# 暴露所有服务端口
EXPOSE 8081 8083 8084 8085 8086 8087 8088

# 环境变量 - 数据库配置
ENV MYSQL_HOST=host.docker.internal
ENV MYSQL_PORT=3306
ENV MYSQL_PASSWORD=TakeoutApp2026!

# 环境变量 - Redis配置
ENV REDIS_HOST=host.docker.internal
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=123456

# 环境变量 - RabbitMQ配置
ENV RABBITMQ_HOST=host.docker.internal
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=takeout
ENV RABBITMQ_PASSWORD=123456

# 环境变量 - Nacos配置
ENV NACOS_SERVER_ADDR=47.99.34.251:8848
ENV NACOS_USERNAME=nacos
ENV NACOS_PASSWORD=nacos

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
