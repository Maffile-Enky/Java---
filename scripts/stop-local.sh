#!/bin/bash
# 停止所有本地运行的服务
PORTS=(9999 8081 8083 8084 8085 8086 8087 8088)

echo "停止本地服务..."
for port in "${PORTS[@]}"; do
    pid=$(lsof -ti:$port 2>/dev/null || true)
    if [ -n "$pid" ]; then
        kill $pid 2>/dev/null && echo "  已停止端口 $port (PID: $pid)" || true
    fi
done
echo "完成。"
