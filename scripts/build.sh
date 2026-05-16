#!/bin/bash
# ============================================================
# 构建所有服务 JAR 包
# 用法：./scripts/build.sh
# ============================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR/takeout-platform"

echo "=========================================="
echo "  构建所有服务 JAR 包"
echo "=========================================="

mvn clean package -DskipTests \
    -pl common/common-core,common/common-web,common/common-security,common/common-redis,common/common-mq,common/common-feign,\
gateway,user-service,merchant-service,order-service,payment-service,notification-service,delivery-service,search-service \
    -am

echo ""
echo "=========================================="
echo "  构建完成！"
echo ""
echo "  本地运行:  ./scripts/run-local.sh"
echo "  Docker:    ./scripts/docker-build.sh && docker-compose up -d"
echo "=========================================="
