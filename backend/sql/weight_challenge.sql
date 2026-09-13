-- ==============================================
-- Weight Challenge 业务表
-- 基于 docs/backend_design.md 数据模型设计
-- ==============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户扩展信息表 weight_user
-- ----------------------------
DROP TABLE IF EXISTS `weight_user`;
CREATE TABLE `weight_user` (
  `user_id` bigint NOT NULL COMMENT '用户ID，关联sys_user.user_id',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `gender` char(1) DEFAULT '0' COMMENT '性别（0未知 1男 2女）',
  `height` decimal(5,1) DEFAULT NULL COMMENT '身高(cm)',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `target_weight` decimal(5,2) DEFAULT NULL COMMENT '目标体重(kg)',
  `target_completion_date` date DEFAULT NULL COMMENT '目标完成日期',
  `current_weight` decimal(5,2) DEFAULT NULL COMMENT '当前体重(kg)',
  `bmi` decimal(4,1) DEFAULT NULL COMMENT 'BMI指数',
  `daily_calorie_target` int DEFAULT 0 COMMENT '建议每日摄入上限(kcal)',
  `daily_calorie_deficit_target` int DEFAULT 0 COMMENT '每日热量缺口目标(kcal)',
  `daily_water_target` int DEFAULT 8 COMMENT '每日饮水目标(杯)',
  `streak_days` int DEFAULT 0 COMMENT '连续打卡天数',
  `achievement_points` int DEFAULT 0 COMMENT '成就积分',
  `main_circle_id` bigint DEFAULT NULL COMMENT '首页展示的主圈子ID',
  `circle_feature_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用圈子功能',
  `sync_mode` char(1) DEFAULT '0' COMMENT '同步模式（0实时 1手动）',
  `source` char(1) DEFAULT '0' COMMENT '注册来源（0微信小程序 1iOS 2Android 3H5 4后台创建）',
  `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信unionid（用于多应用打通）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息表';

-- ----------------------------
-- 2. 用户关注关系表 weight_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `weight_user_follow`;
CREATE TABLE `weight_user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `follower_id` bigint NOT NULL COMMENT '关注者用户ID',
  `following_id` bigint NOT NULL COMMENT '被关注者用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`,`following_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注关系表';

-- ----------------------------
-- 3. 同步偏好设置表 weight_sync_preference
-- ----------------------------
DROP TABLE IF EXISTS `weight_sync_preference`;
CREATE TABLE `weight_sync_preference` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `circle_id` bigint NOT NULL COMMENT '圈子ID',
  `auto_sync` tinyint(1) DEFAULT 1 COMMENT '是否自动同步',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_circle` (`user_id`,`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步偏好设置表';

-- ----------------------------
-- 4. 体重记录表 weight_record
-- ----------------------------
DROP TABLE IF EXISTS `weight_record`;
CREATE TABLE `weight_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `weight` decimal(5,2) NOT NULL COMMENT '体重(kg)',
  `bmi` decimal(4,1) DEFAULT NULL COMMENT 'BMI快照',
  `body_fat_rate` decimal(4,1) DEFAULT NULL COMMENT '体脂率(%)',
  `recorded_at` datetime NOT NULL COMMENT '记录时间',
  `sync_to_circles` varchar(500) DEFAULT NULL COMMENT '同步到的圈子ID列表(JSON)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体重记录表';

-- ----------------------------
-- 5. 饮食记录表 weight_food_record
-- ----------------------------
DROP TABLE IF EXISTS `weight_food_record`;
CREATE TABLE `weight_food_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `meal_type` char(1) NOT NULL COMMENT '餐类型（0早餐 1午餐 2晚餐 3加餐）',
  `food_name` varchar(128) NOT NULL COMMENT '食物名称',
  `calories` int DEFAULT 0 COMMENT '卡路里',
  `protein` decimal(6,2) DEFAULT NULL COMMENT '蛋白质(g)',
  `fat` decimal(6,2) DEFAULT NULL COMMENT '脂肪(g)',
  `carbs` decimal(6,2) DEFAULT NULL COMMENT '碳水化合物(g)',
  `image_url` varchar(512) DEFAULT NULL COMMENT 'AI识别图片URL',
  `recorded_at` datetime NOT NULL COMMENT '记录时间',
  `sync_to_circles` varchar(500) DEFAULT NULL COMMENT '同步到的圈子ID列表(JSON)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录表';

-- ----------------------------
-- 6. 运动记录表 weight_exercise_record
-- ----------------------------
DROP TABLE IF EXISTS `weight_exercise_record`;
CREATE TABLE `weight_exercise_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exercise_type` varchar(64) NOT NULL COMMENT '运动类型',
  `duration_minutes` int DEFAULT 0 COMMENT '时长(分钟)',
  `calories_burned` int DEFAULT 0 COMMENT '消耗卡路里',
  `distance` decimal(8,2) DEFAULT NULL COMMENT '距离(km)',
  `recorded_at` datetime NOT NULL COMMENT '记录时间',
  `sync_to_circles` varchar(500) DEFAULT NULL COMMENT '同步到的圈子ID列表(JSON)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动记录表';

-- ----------------------------
-- 7. 饮水记录表 weight_water_record
-- ----------------------------
DROP TABLE IF EXISTS `weight_water_record`;
CREATE TABLE `weight_water_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `cups` int DEFAULT 1 COMMENT '杯数',
  `ml` int DEFAULT NULL COMMENT '毫升数',
  `recorded_at` datetime NOT NULL COMMENT '记录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮水记录表';

-- ----------------------------
-- 8. 习惯定义表 weight_habit
-- ----------------------------
DROP TABLE IF EXISTS `weight_habit`;
CREATE TABLE `weight_habit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) NOT NULL COMMENT '习惯名称',
  `icon` varchar(32) DEFAULT '✅' COMMENT '图标emoji',
  `period` char(1) DEFAULT '1' COMMENT '时段（0早晨 1任意 2晚间）',
  `frequency` char(1) DEFAULT '0' COMMENT '频率（0每日 1每周 2自定义）',
  `reminder_time` time DEFAULT NULL COMMENT '提醒时间',
  `is_active` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `current_streak` int DEFAULT 0 COMMENT '当前连续天数',
  `longest_streak` int DEFAULT 0 COMMENT '最长连续天数',
  `total_checkins` int DEFAULT 0 COMMENT '总打卡次数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='习惯定义表';

-- ----------------------------
-- 9. 习惯打卡记录表 weight_habit_checkin
-- ----------------------------
DROP TABLE IF EXISTS `weight_habit_checkin`;
CREATE TABLE `weight_habit_checkin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `habit_id` bigint NOT NULL COMMENT '习惯ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `checked_at` datetime NOT NULL COMMENT '打卡时间',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_habit_date` (`habit_id`,`checked_at`),
  KEY `idx_user_time` (`user_id`,`checked_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='习惯打卡记录表';

-- ----------------------------
-- 10. 圈子分类表 weight_circle_category
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_category`;
CREATE TABLE `weight_circle_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(32) NOT NULL COMMENT '分类代码',
  `name` varchar(32) NOT NULL COMMENT '分类名称',
  `icon` varchar(32) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='圈子分类表';

-- 预置圈子分类数据
INSERT INTO `weight_circle_category` (`code`, `name`, `icon`, `sort_order`, `create_time`) VALUES
('weight', '减重', '🏋️', 1, NOW()),
('exercise', '运动', '🏃', 2, NOW()),
('morning', '早起', '🌅', 3, NOW()),
('diet', '饮食', '🥗', 4, NOW()),
('habit', '习惯', '✅', 5, NOW()),
('challenge', '挑战', '🔥', 6, NOW());

-- ----------------------------
-- 11. 圈子主表 weight_circle
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle`;
CREATE TABLE `weight_circle` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '圈子名称',
  `type` char(1) DEFAULT '0' COMMENT '类型（0公开 1私密）',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `icon` varchar(32) DEFAULT '⚖️' COMMENT '圈子图标emoji',
  `cover_url` varchar(512) DEFAULT NULL COMMENT '封面图URL',
  `description` varchar(500) DEFAULT NULL COMMENT '简介',
  `creator_id` bigint NOT NULL COMMENT '创建者用户ID',
  `member_count` int DEFAULT 1 COMMENT '成员数',
  `deposit_required` decimal(10,2) DEFAULT 0 COMMENT '押金金额',
  `penalty_rule` char(1) DEFAULT '0' COMMENT '惩罚规则（0无 1提醒 2扣押金 3踢出）',
  `duration_days` int DEFAULT 0 COMMENT '持续天数（0永久）',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_creator` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='圈子主表';

-- ----------------------------
-- 12. 圈子成员表 weight_circle_member
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_member`;
CREATE TABLE `weight_circle_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `circle_id` bigint NOT NULL COMMENT '圈子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` char(1) DEFAULT '1' COMMENT '角色（0管理员 1成员）',
  `joined_at` datetime DEFAULT NULL COMMENT '加入时间',
  `is_pinned` tinyint(1) DEFAULT 0 COMMENT '是否置顶',
  `streak_days` int DEFAULT 0 COMMENT '连续打卡天数',
  `deposit_status` char(1) DEFAULT '0' COMMENT '押金状态（0未缴 1已缴 2已退 3已扣）',
  `total_checkins` int DEFAULT 0 COMMENT '累计打卡次数',
  `weight_change` decimal(5,2) DEFAULT 0 COMMENT '体重变化量',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1退出）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_circle_user` (`circle_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='圈子成员表';

-- ----------------------------
-- 13. 圈子目标表 weight_circle_goal
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_goal`;
CREATE TABLE `weight_circle_goal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `circle_id` bigint NOT NULL COMMENT '圈子ID',
  `goal_type` varchar(32) NOT NULL COMMENT '目标类型（weight_loss/checkin/exercise/diet/habit/custom）',
  `goal_name` varchar(128) NOT NULL COMMENT '目标名称',
  `description` varchar(500) DEFAULT NULL COMMENT '目标描述',
  `metric_code` varchar(64) DEFAULT NULL COMMENT '业务指标编码（weight_record_count/weight_loss_kg/exercise_minutes/exercise_count/food_record_count/habit_checkin_count/circle_checkin_count/manual_circle_checkin）',
  `target_value` decimal(10,2) DEFAULT NULL COMMENT '目标值',
  `target_unit` varchar(32) DEFAULT NULL COMMENT '目标单位',
  `period` char(1) DEFAULT '0' COMMENT '周期（0每日 1每周 2每月 3总计）',
  `verification_type` char(1) DEFAULT '0' COMMENT '验证方式（0自动 1手动 2拍照 3审核）',
  `penalty_for_failure` varchar(256) DEFAULT NULL COMMENT '未完成惩罚描述',
  `is_required` tinyint(1) DEFAULT 1 COMMENT '是否必选',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_circle` (`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='圈子目标表';

-- ----------------------------
-- 14. 押金记录表 weight_circle_deposit
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_deposit`;
CREATE TABLE `weight_circle_deposit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `circle_id` bigint NOT NULL COMMENT '圈子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '押金金额',
  `status` char(1) DEFAULT '0' COMMENT '状态（0待支付 1已支付 2已退还 3已扣除）',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `refunded_at` datetime DEFAULT NULL COMMENT '退还时间',
  `deduct_reason` varchar(256) DEFAULT NULL COMMENT '扣除原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_circle_user` (`circle_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='押金记录表';

-- ----------------------------
-- 15. 排行榜缓存表 weight_circle_ranking
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_ranking`;
CREATE TABLE `weight_circle_ranking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `circle_id` bigint NOT NULL COMMENT '圈子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `period` char(1) NOT NULL COMMENT '周期（0日榜 1周榜 2月榜）',
  `rank_num` int DEFAULT 0 COMMENT '排名',
  `score` decimal(10,2) DEFAULT 0 COMMENT '得分',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_circle_user_period` (`circle_id`,`user_id`,`period`),
  KEY `idx_circle_period` (`circle_id`,`period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜缓存表';

-- ----------------------------
-- 16. 圈子动态表 weight_circle_feed
-- ----------------------------
DROP TABLE IF EXISTS `weight_circle_feed`;
CREATE TABLE `weight_circle_feed` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `circle_id` bigint DEFAULT NULL COMMENT '兼容字段，默认记录来源圈子ID',
  `user_id` bigint NOT NULL COMMENT '发布者用户ID',
  `feed_type` varchar(32) NOT NULL COMMENT '动态类型（weight/food/exercise/habit/text）',
  `visibility_scope` varchar(16) NOT NULL DEFAULT 'circle' COMMENT '可见范围（public/circle）',
  `origin_circle_id` bigint DEFAULT NULL COMMENT '来源圈子ID',
  `content` text COMMENT '动态文本内容',
  `images` varchar(2000) DEFAULT NULL COMMENT '图片URL数组(JSON)',
  `source_record_id` bigint DEFAULT NULL COMMENT '关联的原始记录ID',
  `source_record_type` varchar(32) DEFAULT NULL COMMENT '关联记录类型',
  `likes_count` int DEFAULT 0 COMMENT '点赞数',
  `comments_count` int DEFAULT 0 COMMENT '评论数',
  `is_featured` tinyint(1) DEFAULT 0 COMMENT '是否精选',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1待审核 2已屏蔽）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_circle_time` (`circle_id`,`create_time`),
  KEY `idx_visibility_time` (`visibility_scope`,`create_time`),
  KEY `idx_origin_circle_time` (`origin_circle_id`,`create_time`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='圈子动态表';

-- ----------------------------
-- 17. 动态同步圈子关系表 weight_feed_circle_sync
-- ----------------------------
DROP TABLE IF EXISTS `weight_feed_circle_sync`;
CREATE TABLE `weight_feed_circle_sync` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `feed_id` bigint NOT NULL COMMENT '动态ID',
  `circle_id` bigint NOT NULL COMMENT '同步圈子ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_feed_circle` (`feed_id`,`circle_id`),
  KEY `idx_circle` (`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态同步圈子关系表';

-- ----------------------------
-- 18. 动态点赞表 weight_feed_like
-- ----------------------------
DROP TABLE IF EXISTS `weight_feed_like`;
CREATE TABLE `weight_feed_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `feed_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_feed_user` (`feed_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- ----------------------------
-- 19. 评论表 weight_comment
-- ----------------------------
DROP TABLE IF EXISTS `weight_comment`;
CREATE TABLE `weight_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `feed_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '评论者用户ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `images` varchar(2000) DEFAULT NULL COMMENT '评论图片URL数组(JSON)',
  `reply_to_comment_id` bigint DEFAULT NULL COMMENT '回复的评论ID',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '回复的用户ID',
  `likes_count` int DEFAULT 0 COMMENT '点赞数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1待审核 2已屏蔽）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_feed` (`feed_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ----------------------------
-- 20. 评论点赞表 weight_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `weight_comment_like`;
CREATE TABLE `weight_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- ----------------------------
-- 21. 关注动态流表 weight_social_feed
-- ----------------------------
DROP TABLE IF EXISTS `weight_social_feed`;
CREATE TABLE `weight_social_feed` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID（接收者）',
  `feed_id` bigint NOT NULL COMMENT '动态ID',
  `from_user_id` bigint NOT NULL COMMENT '动态发布者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`create_time`),
  KEY `idx_feed` (`feed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注动态流表';

-- ----------------------------
-- 22. 每日任务表 weight_daily_task
-- ----------------------------
DROP TABLE IF EXISTS `weight_daily_task`;
CREATE TABLE `weight_daily_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `task_date` date NOT NULL COMMENT '任务日期',
  `circle_id` bigint DEFAULT NULL COMMENT '关联圈子ID（圈子任务使用）',
  `task_type` varchar(32) NOT NULL COMMENT '任务类型（circle/habit/system）',
  `task_name` varchar(128) NOT NULL COMMENT '任务名称',
  `period_key` varchar(64) DEFAULT NULL COMMENT '周期幂等键（如 day:2026-04-14 / week:2026-W15）',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID（圈子目标ID/习惯ID/系统任务ID）',
  `target_value` decimal(10,2) DEFAULT NULL COMMENT '目标值',
  `current_value` decimal(10,2) DEFAULT 0 COMMENT '当前值',
  `is_completed` tinyint(1) DEFAULT 0 COMMENT '是否完成',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `points_reward` int DEFAULT 0 COMMENT '积分奖励',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`task_date`),
  KEY `idx_circle_period` (`circle_id`,`period_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日任务表';

-- ----------------------------
-- 23. 成就徽章表 weight_achievement
-- ----------------------------
DROP TABLE IF EXISTS `weight_achievement`;
CREATE TABLE `weight_achievement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `badge_type` varchar(64) NOT NULL COMMENT '徽章类型',
  `badge_name` varchar(64) NOT NULL COMMENT '徽章名称',
  `badge_icon` varchar(32) DEFAULT '🏆' COMMENT '徽章图标',
  `badge_description` varchar(256) DEFAULT NULL COMMENT '徽章描述',
  `unlocked_at` datetime DEFAULT NULL COMMENT '解锁时间',
  `progress` decimal(5,2) DEFAULT 0 COMMENT '进度(0-100)',
  `is_unlocked` tinyint(1) DEFAULT 0 COMMENT '是否已解锁',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_badge` (`user_id`,`badge_type`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就徽章表';

-- ----------------------------
-- 24. 消息通知表 weight_notification
-- ----------------------------
DROP TABLE IF EXISTS `weight_notification`;
CREATE TABLE `weight_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `type` varchar(32) NOT NULL COMMENT '通知类型（like/comment/reply/follow/circle_join/circle_nudge/achievement/system）',
  `from_user_id` bigint DEFAULT NULL COMMENT '触发通知的用户ID',
  `target_type` varchar(32) DEFAULT NULL COMMENT '目标类型（feed/comment/circle/achievement）',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `content` varchar(500) NOT NULL COMMENT '通知内容',
  `preview` varchar(256) DEFAULT NULL COMMENT '预览内容',
  `is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`,`is_read`),
  KEY `idx_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- ----------------------------
-- 25. 用户反馈表 weight_feedback
-- ----------------------------
DROP TABLE IF EXISTS `weight_feedback`;
CREATE TABLE `weight_feedback` (
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

-- ----------------------------
-- 26. HTTPS证书管理表 weight_certificate
-- ----------------------------
DROP TABLE IF EXISTS `weight_certificate`;
CREATE TABLE `weight_certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(80) NOT NULL COMMENT '证书名称',
  `primary_domain` varchar(255) NOT NULL COMMENT '主域名',
  `domains` varchar(1000) DEFAULT NULL COMMENT '域名列表，多个逗号分隔',
  `provider` varchar(64) NOT NULL DEFAULT 'letsencrypt' COMMENT '证书服务商（letsencrypt/zerossl/aliyun/tencent/custom）',
  `ca_directory_url` varchar(500) DEFAULT NULL COMMENT 'ACME目录地址',
  `acme_mode` varchar(64) NOT NULL DEFAULT 'certbot_webroot' COMMENT '签发方式（certbot_webroot/custom_command/manual）',
  `challenge_type` varchar(32) NOT NULL DEFAULT 'http-01' COMMENT '验证方式（http-01/dns-01/manual）',
  `dns_provider` varchar(64) DEFAULT NULL COMMENT 'DNS服务商',
  `webroot_path` varchar(500) DEFAULT NULL COMMENT 'webroot目录',
  `email` varchar(160) DEFAULT NULL COMMENT '申请邮箱',
  `cert_path` varchar(500) DEFAULT NULL COMMENT '证书文件路径',
  `key_path` varchar(500) DEFAULT NULL COMMENT '私钥文件路径',
  `fullchain_path` varchar(500) DEFAULT NULL COMMENT '完整链证书路径',
  `issuer` varchar(500) DEFAULT NULL COMMENT '证书颁发者',
  `serial_number` varchar(128) DEFAULT NULL COMMENT '证书序列号',
  `not_before` datetime DEFAULT NULL COMMENT '证书生效时间',
  `not_after` datetime DEFAULT NULL COMMENT '证书到期时间',
  `days_remaining` int DEFAULT NULL COMMENT '剩余天数',
  `renew_before_days` int NOT NULL DEFAULT 20 COMMENT '提前续签天数',
  `auto_renew` tinyint NOT NULL DEFAULT 1 COMMENT '是否自动续签（0否 1是）',
  `deploy_command` varchar(1000) DEFAULT NULL COMMENT '续签后执行命令',
  `issue_command` varchar(2000) DEFAULT NULL COMMENT '自定义签发命令',
  `renew_command` varchar(2000) DEFAULT NULL COMMENT '自定义续签命令',
  `status` varchar(32) NOT NULL DEFAULT 'unknown' COMMENT '当前状态（unknown/valid/expiring/expired/failed）',
  `last_checked_at` datetime DEFAULT NULL COMMENT '最近检测时间',
  `last_renewed_at` datetime DEFAULT NULL COMMENT '最近续签时间',
  `last_error` varchar(1000) DEFAULT NULL COMMENT '最近错误',
  `last_log` text DEFAULT NULL COMMENT '最近执行日志',
  `enabled` char(1) NOT NULL DEFAULT '0' COMMENT '启用状态（0启用 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_certificate_primary_domain` (`primary_domain`),
  KEY `idx_certificate_status_days` (`status`,`days_remaining`),
  KEY `idx_certificate_auto_renew` (`enabled`,`auto_renew`,`days_remaining`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HTTPS证书管理表';

SET FOREIGN_KEY_CHECKS = 1;
