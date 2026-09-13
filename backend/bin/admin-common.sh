#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BACKEND_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
FRONTEND_DIR="$BACKEND_DIR/ruoyi-ui"
LOG_DIR="$BACKEND_DIR/logs"

BACKEND_JAR="$BACKEND_DIR/ruoyi-admin/target/ruoyi-admin.jar"
BACKEND_PID_FILE="$LOG_DIR/admin-backend.pid"
FRONTEND_PID_FILE="$LOG_DIR/admin-frontend.pid"
BACKEND_LOG_FILE="$LOG_DIR/admin-backend.log"
FRONTEND_LOG_FILE="$LOG_DIR/admin-frontend.log"

BACKEND_PORT=8080
FRONTEND_PORT=1024

ensure_log_dir() {
    mkdir -p "$LOG_DIR"
}

require_command() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "缺少必要命令: $1"
        exit 1
    fi
}

read_pid_file() {
    if [ ! -f "$1" ]; then
        return 0
    fi
    tr -d '[:space:]' < "$1"
}

is_pid_running() {
    pid="$1"
    if [ -z "$pid" ]; then
        return 1
    fi
    kill -0 "$pid" 2>/dev/null
}

cleanup_stale_pid_file() {
    pid_file="$1"
    pid=$(read_pid_file "$pid_file")
    if [ -n "$pid" ] && ! is_pid_running "$pid"; then
        rm -f "$pid_file"
    fi
}

is_port_listening() {
    port="$1"
    lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
}

check_port_conflict() {
    port="$1"
    pid_file="$2"
    service_name="$3"
    pid=$(read_pid_file "$pid_file")
    if is_port_listening "$port"; then
        if [ -n "$pid" ] && is_pid_running "$pid"; then
            return 0
        fi
        echo "$service_name 需要的端口 $port 已被其他进程占用，请先释放后再启动。"
        return 1
    fi
    return 0
}

wait_for_port() {
    port="$1"
    timeout_seconds="$2"
    elapsed=0
    while [ "$elapsed" -lt "$timeout_seconds" ]; do
        if is_port_listening "$port"; then
            return 0
        fi
        sleep 1
        elapsed=$((elapsed + 1))
    done
    return 1
}

stop_process_by_pid_file() {
    pid_file="$1"
    service_name="$2"
    quiet="${3:-0}"
    pid=$(read_pid_file "$pid_file")

    if [ -z "$pid" ]; then
        if [ "$quiet" -ne 1 ]; then
            echo "$service_name 没有脚本管理的运行实例。"
        fi
        return 0
    fi

    if ! is_pid_running "$pid"; then
        rm -f "$pid_file"
        if [ "$quiet" -ne 1 ]; then
            echo "$service_name 的 PID 文件已过期，已清理。"
        fi
        return 0
    fi

    if [ "$quiet" -ne 1 ]; then
        echo "停止 $service_name (PID: $pid)..."
    fi
    kill -TERM "$pid" 2>/dev/null || true

    attempts=0
    while [ "$attempts" -lt 10 ]; do
        if ! is_pid_running "$pid"; then
            rm -f "$pid_file"
            if [ "$quiet" -ne 1 ]; then
                echo "$service_name 已停止。"
            fi
            return 0
        fi
        sleep 1
        attempts=$((attempts + 1))
    done

    kill -KILL "$pid" 2>/dev/null || true
    sleep 1
    rm -f "$pid_file"

    if [ "$quiet" -ne 1 ]; then
        echo "$service_name 已强制停止。"
    fi
    return 0
}
