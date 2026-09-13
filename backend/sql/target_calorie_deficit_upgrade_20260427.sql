-- ==============================================
-- 个人目标完成日期、建议摄入上限与每日热量缺口升级
-- 适用：已有 weight_challenge 数据库升级
-- ==============================================

SET NAMES utf8mb4;

SET @schema_name = DATABASE();

SET @add_target_completion_date = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE weight_user ADD COLUMN target_completion_date date DEFAULT NULL COMMENT ''目标完成日期'' AFTER target_weight',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'weight_user'
    AND COLUMN_NAME = 'target_completion_date'
);
PREPARE stmt FROM @add_target_completion_date;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @modify_daily_calorie_target = (
  SELECT IF(
    COUNT(*) = 1,
    'ALTER TABLE weight_user MODIFY COLUMN daily_calorie_target int DEFAULT 0 COMMENT ''建议每日摄入上限(kcal)''',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'weight_user'
    AND COLUMN_NAME = 'daily_calorie_target'
);
PREPARE stmt FROM @modify_daily_calorie_target;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_daily_deficit_target = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE weight_user ADD COLUMN daily_calorie_deficit_target int DEFAULT 0 COMMENT ''每日热量缺口目标(kcal)'' AFTER daily_calorie_target',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @schema_name
    AND TABLE_NAME = 'weight_user'
    AND COLUMN_NAME = 'daily_calorie_deficit_target'
);
PREPARE stmt FROM @add_daily_deficit_target;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE weight_user
SET daily_calorie_deficit_target = 0
WHERE daily_calorie_deficit_target IS NULL;
