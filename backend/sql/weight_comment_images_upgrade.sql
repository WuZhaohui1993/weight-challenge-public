SET @weight_comment_images_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'weight_comment'
      AND COLUMN_NAME = 'images'
);

SET @weight_comment_images_ddl := IF(
    @weight_comment_images_exists = 0,
    'ALTER TABLE `weight_comment` ADD COLUMN `images` varchar(2000) DEFAULT NULL COMMENT ''评论图片URL数组(JSON)'' AFTER `content`',
    'SELECT 1'
);

PREPARE weight_comment_images_stmt FROM @weight_comment_images_ddl;
EXECUTE weight_comment_images_stmt;
DEALLOCATE PREPARE weight_comment_images_stmt;
