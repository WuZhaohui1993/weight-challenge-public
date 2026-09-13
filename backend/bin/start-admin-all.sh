#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
. "$SCRIPT_DIR/admin-common.sh"

ensure_log_dir
require_command lsof
require_command nohup
require_command java
require_command npm

cleanup_stale_pid_file "$BACKEND_PID_FILE"
cleanup_stale_pid_file "$FRONTEND_PID_FILE"

if [ ! -f "$BACKEND_JAR" ]; then
    echo "未找到后端 jar: $BACKEND_JAR"
    echo "请先执行后端构建，再运行本脚本。"
    exit 1
fi

check_port_conflict "$BACKEND_PORT" "$BACKEND_PID_FILE" "后端服务"
check_port_conflict "$FRONTEND_PORT" "$FRONTEND_PID_FILE" "管理后台前端"

backend_pid=$(read_pid_file "$BACKEND_PID_FILE")
frontend_pid=$(read_pid_file "$FRONTEND_PID_FILE")
backend_running=0
frontend_running=0
started_backend_this_run=0
started_frontend_this_run=0

if [ -n "$backend_pid" ] && is_pid_running "$backend_pid"; then
    backend_running=1
fi

if [ -n "$frontend_pid" ] && is_pid_running "$frontend_pid"; then
    frontend_running=1
fi

if [ "$backend_running" -eq 1 ] && [ "$frontend_running" -eq 1 ]; then
    echo "管理员端前后端已在运行。"
    echo "后端: http://localhost:$BACKEND_PORT"
    echo "前端: http://localhost:$FRONTEND_PORT"
    exit 0
fi

if [ "$backend_running" -eq 0 ]; then
    echo "启动后端服务..."
    nohup sh -c 'cd "$1" && exec java -jar "$2"' _ "$BACKEND_DIR" "$BACKEND_JAR" > "$BACKEND_LOG_FILE" 2>&1 &
    echo $! > "$BACKEND_PID_FILE"
    started_backend_this_run=1
else
    echo "后端服务已在运行。"
fi

if ! wait_for_port "$BACKEND_PORT" 30; then
    echo "后端服务未能在 30 秒内监听端口 $BACKEND_PORT。"
    if [ "$started_backend_this_run" -eq 1 ]; then
        stop_process_by_pid_file "$BACKEND_PID_FILE" "后端服务" 1
    fi
    exit 1
fi

if [ "$frontend_running" -eq 0 ]; then
    echo "启动管理后台前端..."
    nohup sh -c 'cd "$1" && npm run dev' _ "$FRONTEND_DIR" > "$FRONTEND_LOG_FILE" 2>&1 &
    echo $! > "$FRONTEND_PID_FILE"
    started_frontend_this_run=1
else
    echo "管理后台前端已在运行。"
fi

if ! wait_for_port "$FRONTEND_PORT" 60; then
    echo "管理后台前端未能在 60 秒内监听端口 $FRONTEND_PORT。"
    if [ "$started_frontend_this_run" -eq 1 ]; then
        stop_process_by_pid_file "$FRONTEND_PID_FILE" "管理后台前端" 1
    fi
    if [ "$started_backend_this_run" -eq 1 ]; then
        stop_process_by_pid_file "$BACKEND_PID_FILE" "后端服务" 1
    fi
    exit 1
fi

echo "管理员端全套启动完成。"
echo "后端: http://localhost:$BACKEND_PORT"
echo "前端: http://localhost:$FRONTEND_PORT"
echo "日志目录: $LOG_DIR"
