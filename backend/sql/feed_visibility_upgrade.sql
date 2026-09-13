ALTER TABLE `weight_circle_feed`
    MODIFY COLUMN `circle_id` bigint DEFAULT NULL COMMENT '兼容字段，默认记录来源圈子ID',
    ADD COLUMN `visibility_scope` varchar(16) NOT NULL DEFAULT 'circle' COMMENT '可见范围（public/circle）' AFTER `feed_type`,
    ADD COLUMN `origin_circle_id` bigint DEFAULT NULL COMMENT '来源圈子ID' AFTER `visibility_scope`,
    ADD KEY `idx_visibility_time` (`visibility_scope`, `create_time`),
    ADD KEY `idx_origin_circle_time` (`origin_circle_id`, `create_time`);

UPDATE `weight_circle_feed`
SET `visibility_scope` = 'circle',
    `origin_circle_id` = `circle_id`
WHERE `visibility_scope` IS NULL
   OR `visibility_scope` = '';

CREATE TABLE IF NOT EXISTS `weight_feed_circle_sync` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `feed_id` bigint NOT NULL COMMENT '动态ID',
    `circle_id` bigint NOT NULL COMMENT '同步圈子ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_feed_circle` (`feed_id`, `circle_id`),
    KEY `idx_circle` (`circle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态同步圈子关系表';
