#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
. "$SCRIPT_DIR/admin-common.sh"

ensure_log_dir
require_command lsof

cleanup_stale_pid_file "$BACKEND_PID_FILE"
cleanup_stale_pid_file "$FRONTEND_PID_FILE"

stop_process_by_pid_file "$FRONTEND_PID_FILE" "管理后台前端"
stop_process_by_pid_file "$BACKEND_PID_FILE" "后端服务"

if is_port_listening "$FRONTEND_PORT"; then
    echo "警告: 端口 $FRONTEND_PORT 仍被占用，但不是本脚本管理的进程，未执行强杀。"
fi

if is_port_listening "$BACKEND_PORT"; then
    echo "警告: 端口 $BACKEND_PORT 仍被占用，但不是本脚本管理的进程，未执行强杀。"
fi

echo "管理员端全套停止流程已完成。"
