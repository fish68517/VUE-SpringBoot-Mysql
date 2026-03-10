#!/usr/bin/env bash

cd "$(dirname "$0")"

PID_FILE="app.pid"

echo "=================================="
echo " SpringBoot 停止脚本"
echo "=================================="

if [ ! -f "$PID_FILE" ]; then
  echo "未找到 PID 文件，项目可能没有运行。"
  exit 0
fi

PID=$(cat "$PID_FILE")

if ps -p "$PID" >/dev/null 2>&1; then
  echo "正在停止进程，PID=$PID ..."
  kill "$PID"

  for i in {1..10}; do
    if ps -p "$PID" >/dev/null 2>&1; then
      sleep 1
    else
      break
    fi
  done

  if ps -p "$PID" >/dev/null 2>&1; then
    echo "普通停止失败，执行强制停止..."
    kill -9 "$PID"
  fi

  echo "项目已停止。"
else
  echo "PID=$PID 对应进程不存在，已清理 PID 文件。"
fi

rm -f "$PID_FILE"