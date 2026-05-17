FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache nginx supervisor

WORKDIR /app

# 复制网关JAR
COPY gateway/gateway-1.0.0-SNAPSHOT.jar /app/gateway.jar

# 复制前端构建产物
COPY frontend /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/http.d/default.conf

# 复制supervisord配置
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# 创建nginx需要的目录
RUN mkdir -p /run/nginx

# 暴露端口
EXPOSE 80 9999

# 环境变量
ENV NACOS_SERVER_ADDR=47.99.34.251:8848
ENV NACOS_USERNAME=nacos
ENV NACOS_PASSWORD=nacos

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
