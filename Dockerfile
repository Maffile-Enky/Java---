version: '3.8'
services:
  gateway:
    build: ./takeout-platform/gateway
    ports:
      - "8080:8080"
    environment:
      - SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=nacos:8848
      - SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=nacos:8848
    depends_on:
      - nacos

  user-service:
    build: ./takeout-platform/user-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=nacos:8848
      - SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=nacos:8848
    depends_on:
      - nacos

  merchant-service:
    build: ./takeout-platform/merchant-service
    ports:
      - "8083:8083"
    environment:
      - SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=nacos:8848
      - SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=nacos:8848
    depends_on:
      - nacos

  nacos:
    image: nacos/nacos-server:v2.3.1
    ports:
      - "8848:8848"
    environment:
      - MODE=standalone

  frontend:
    build: ./takeout-frontend
    ports:
      - "80:80"
    depends_on:
      - gateway
