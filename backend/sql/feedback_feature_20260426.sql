-- 建议 / Bug 反馈功能迁移脚本
-- 适用：已有 weight_challenge 数据库升级

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `weight_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category` varchar(32) NOT NULL COMMENT '反馈类型（bug/suggestion/experience/account/other）',
  `content` varchar(500) NOT NULL COMMENT '反馈内容',
  `images` varchar(2000) DEFAULT NULL COMMENT '图片URL数组(JSON)',
  `contact` varchar(80) DEFAULT NULL COMMENT '联系方式',
  `source_page` varchar(160) DEFAULT NULL COMMENT '来源页面',
  `environment_json` varchar(2000) DEFAULT NULL COMMENT '环境信息(JSON)',
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT '处理状态（pending/processing/resolved/closed）',
  `priority` varchar(32) NOT NULL DEFAULT 'normal' COMMENT '优先级（low/normal/high/urgent）',
  `reply_content` varchar(500) DEFAULT NULL COMMENT '管理员回复',
  `reply_by` varchar(64) DEFAULT NULL COMMENT '回复人',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_user_time` (`user_id`,`create_time`),
  KEY `idx_feedback_status_time` (`status`,`create_time`),
  KEY `idx_feedback_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

INSERT INTO sys_menu (
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  query,
  route_name,
  is_frame,
  is_cache,
  menu_type,
  visible,
  status,
  perms,
  icon,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
) VALUES
  (2115, '运营支持', 2000, 7, 'operation-support', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'service', 'admin', NOW(), '', NULL, ''),
  (2120, '用户反馈', 2115, 2, 'feedback', 'weight/feedback/index', NULL, '', 1, 0, 'C', '0', '0', 'ruoyi-weight:feedback:list', 'bug', 'admin', NOW(), '', NULL, '用户反馈菜单'),
  (2121, '用户反馈查询', 2120, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feedback:query', '#', 'admin', NOW(), '', NULL, ''),
  (2122, '用户反馈修改', 2120, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feedback:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2123, '用户反馈删除', 2120, 3, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feedback:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2124, '用户反馈导出', 2120, 4, '', '', NULL, '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feedback:export', '#', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  route_name = VALUES(route_name),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  update_by = VALUES(update_by),
  update_time = NOW(),
  remark = VALUES(remark);
