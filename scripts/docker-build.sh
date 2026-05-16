#!/bin/bash
# ============================================================
# 构建所有 Docker 镜像
# 用法：./scripts/docker-build.sh [tag]
# 默认 tag: latest
# ============================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
TAG="${1:-latest}"

echo "=========================================="
echo "  构建 Docker 镜像 (tag: $TAG)"
echo "=========================================="

cd "$PROJECT_DIR"

SERVICES=(
  "gateway"
  "user-service"
  "merchant-service"
  "order-service"
  "payment-service"
  "notification-service"
  "delivery-service"
  "search-service"
)

for svc in "${SERVICES[@]}"; do
    echo ""
    echo "--- Building takeout-${svc}:${TAG} ---"
    docker build -t "takeout-${svc}:${TAG}" "./takeout-platform/${svc}"
done

echo ""
echo "--- Building takeout-frontend:${TAG} ---"
docker build -t "takeout-frontend:${TAG}" "./takeout-frontend"

echo ""
echo "=========================================="
echo "  所有镜像构建完成！"
echo ""
echo "  启动: docker-compose up -d"
echo "  查看: docker-compose ps"
echo "=========================================="
