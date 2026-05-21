#!/bin/bash
# ============================================================
# 本地开发一键启动脚本
# 前提：Nacos、MySQL、Redis、RabbitMQ 已在 localhost 运行
# 用法：./scripts/run-local.sh
# ============================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
PLATFORM_DIR="$PROJECT_DIR/takeout-platform"
LOG_DIR="$PROJECT_DIR/logs"

# JVM 参数
JAVA_OPTS="-Xms128m -Xmx256m"

# 服务列表：名称 端口 jar路径
SERVICES=(
  "gateway:9999:gateway/target/gateway-1.0.0-SNAPSHOT.jar"
  "user-service:8081:user-service/target/user-service-1.0.0-SNAPSHOT.jar"
  "merchant-service:8083:merchant-service/target/merchant-service-1.0.0-SNAPSHOT.jar"
  "order-service:8084:order-service/target/order-service-1.0.0-SNAPSHOT.jar"
  "payment-service:8085:payment-service/target/payment-service-1.0.0-SNAPSHOT.jar"
  "notification-service:8086:notification-service/target/notification-service-1.0.0-SNAPSHOT.jar"
  "delivery-service:8087:delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar"
  "search-service:8088:search-service/target/search-service-1.0.0-SNAPSHOT.jar"
)

mkdir -p "$LOG_DIR"

echo "=========================================="
echo "  外卖平台 - 本地开发模式"
echo "  Profile: local (连接 localhost)"
echo "=========================================="
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "[ERROR] java 未找到，请确认 JDK 21 已安装"
    exit 1
fi

# 检查基础设施
echo "[CHECK] 检查基础设施连通性..."

check_port() {
    local name=$1 host=$2 port=$3
    if command -v nc &> /dev/null; then
        if nc -z -w2 "$host" "$port" 2>/dev/null; then
            echo "  [OK]   $name ($host:$port)"
        else
            echo "  [WARN] $name ($host:$port) 未响应 - 服务可能无法正常工作"
        fi
    elif command -v curl &> /dev/null; then
        if curl -s --connect-timeout 2 "http://$host:$port" > /dev/null 2>&1; then
            echo "  [OK]   $name ($host:$port)"
        else
            echo "  [WARN] $name ($host:$port) 未响应 - 服务可能无法正常工作"
        fi
    else
        echo "  [SKIP] 无法检测 $name (nc/curl 均不可用)"
    fi
}

check_port "Nacos"  "localhost" 8848
check_port "MySQL"  "localhost" 3306
check_port "Redis"  "localhost" 6379
check_port "RabbitMQ" "localhost" 5672
echo ""

# 停止之前的进程
echo "[STOP] 停止之前启动的服务..."
for entry in "${SERVICES[@]}"; do
    IFS=':' read -r name port jar <<< "$entry"
    pid=$(lsof -ti:$port 2>/dev/null || true)
    if [ -n "$pid" ]; then
        kill $pid 2>/dev/null || true
        echo "  停止 $name (PID: $pid)"
    fi
done
sleep 2

# 启动服务
echo ""
echo "[START] 启动服务 (使用 --spring.profiles.active=local)..."
echo ""

PIDS=()

for entry in "${SERVICES[@]}"; do
    IFS=':' read -r name port jar <<< "$entry"
    jar_path="$PLATFORM_DIR/$jar"

    if [ ! -f "$jar_path" ]; then
        echo "  [SKIP] $name - JAR 不存在: $jar_path"
        echo "         请先运行: cd takeout-platform && mvn package -DskipTests"
        continue
    fi

    echo "  启动 $name (端口 $port)..."
    nohup java $JAVA_OPTS -jar "$jar_path" \
        --spring.profiles.active=local \
        > "$LOG_DIR/$name.log" 2>&1 &

    PIDS+=($!)
    echo "    PID: ${PIDS[-1]}, 日志: logs/$name.log"
done

echo ""
echo "=========================================="
echo "  所有服务已启动！"
echo ""
echo "  访问地址："
echo "    前端(开发):   http://localhost:5173"
echo "    API网关:      http://localhost:9999"
echo "    Nacos控制台:  http://localhost:8848/nacos"
echo ""
echo "  查看日志："
echo "    tail -f logs/gateway.log"
echo "    tail -f logs/user-service.log"
echo ""
echo "  停止所有服务："
echo "    ./scripts/stop-local.sh"
echo "=========================================="
