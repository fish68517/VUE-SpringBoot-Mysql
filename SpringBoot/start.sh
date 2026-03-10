#!/usr/bin/env bash

set -e

cd "$(dirname "$0")"

APP_NAME="springboot-app"
LOG_DIR="logs"
LOG_FILE="$LOG_DIR/app.log"
PID_FILE="app.pid"

echo "=================================="
echo " SpringBoot 后台启动脚本"
echo "=================================="

mkdir -p "$LOG_DIR"

# 检查 Java
if ! command -v java >/dev/null 2>&1; then
  echo "错误：未检测到 java，请先安装 JDK 并配置环境变量。"
  exit 1
fi

# 检查 Maven
if ! command -v mvn >/dev/null 2>&1; then
  echo "错误：未检测到 mvn，请先安装 Maven 并配置环境变量。"
  exit 1
fi

# 检查是否已在运行
if [ -f "$PID_FILE" ]; then
  OLD_PID=$(cat "$PID_FILE")
  if ps -p "$OLD_PID" >/dev/null 2>&1; then
    echo "项目已经在运行中，PID=$OLD_PID"
    echo "如需重启，请先执行: bash stop.sh"
    exit 1
  else
    echo "检测到旧 PID 文件无效，已自动清理。"
    rm -f "$PID_FILE"
  fi
fi

echo "当前目录：$(pwd)"
echo "日志文件：$LOG_FILE"
echo

echo "1. 清理旧编译缓存..."
mvn clean

echo
echo "2. 清理旧日志..."
rm -f "$LOG_FILE"

echo
echo "3. 后台启动 SpringBoot..."
nohup mvn spring-boot:run > "$LOG_FILE" 2>&1 &

NEW_PID=$!
echo "$NEW_PID" > "$PID_FILE"

sleep 3

if ps -p "$NEW_PID" >/dev/null 2>&1; then
  echo "启动成功"
  echo "PID=$NEW_PID"
  echo "日志输出：$LOG_FILE"
else
  echo "启动失败，请检查日志：$LOG_FILE"
  rm -f "$PID_FILE"
  exit 1
fi