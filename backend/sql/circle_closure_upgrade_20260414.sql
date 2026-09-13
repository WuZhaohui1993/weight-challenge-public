SET NAMES utf8mb4;

SET @goal_metric_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'weight_circle_goal'
    AND COLUMN_NAME = 'metric_code'
);
SET @goal_metric_sql = IF(
  @goal_metric_column_exists = 0,
  "ALTER TABLE weight_circle_goal ADD COLUMN metric_code varchar(64) DEFAULT NULL COMMENT '业务指标编码（weight_record_count/weight_loss_kg/exercise_minutes/exercise_count/food_record_count/habit_checkin_count/circle_checkin_count/manual_circle_checkin）' AFTER description",
  "SELECT 'weight_circle_goal.metric_code already exists'"
);
PREPARE stmt_goal_metric FROM @goal_metric_sql;
EXECUTE stmt_goal_metric;
DEALLOCATE PREPARE stmt_goal_metric;

SET @task_circle_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'weight_daily_task'
    AND COLUMN_NAME = 'circle_id'
);
SET @task_circle_sql = IF(
  @task_circle_column_exists = 0,
  "ALTER TABLE weight_daily_task ADD COLUMN circle_id bigint DEFAULT NULL COMMENT '关联圈子ID（圈子任务使用）' AFTER task_date",
  "SELECT 'weight_daily_task.circle_id already exists'"
);
PREPARE stmt_task_circle FROM @task_circle_sql;
EXECUTE stmt_task_circle;
DEALLOCATE PREPARE stmt_task_circle;

SET @task_period_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'weight_daily_task'
    AND COLUMN_NAME = 'period_key'
);
SET @task_period_sql = IF(
  @task_period_column_exists = 0,
  "ALTER TABLE weight_daily_task ADD COLUMN period_key varchar(64) DEFAULT NULL COMMENT '周期幂等键（如 day:2026-04-14 / week:2026-W15）' AFTER task_name",
  "SELECT 'weight_daily_task.period_key already exists'"
);
PREPARE stmt_task_period FROM @task_period_sql;
EXECUTE stmt_task_period;
DEALLOCATE PREPARE stmt_task_period;

SET @task_index_exists = (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'weight_daily_task'
    AND INDEX_NAME = 'idx_circle_period'
);
SET @task_index_sql = IF(
  @task_index_exists = 0,
  "ALTER TABLE weight_daily_task ADD KEY idx_circle_period (circle_id, period_key)",
  "SELECT 'weight_daily_task.idx_circle_period already exists'"
);
PREPARE stmt_task_index FROM @task_index_sql;
EXECUTE stmt_task_index;
DEALLOCATE PREPARE stmt_task_index;

UPDATE weight_circle_goal
SET metric_code = CASE
  WHEN goal_type = 'weight_loss' AND LOWER(IFNULL(target_unit, '')) IN ('kg', '公斤') THEN 'weight_loss_kg'
  WHEN goal_type = 'weight_loss' THEN 'weight_record_count'
  WHEN goal_type = 'exercise' AND (
    LOWER(IFNULL(target_unit, '')) LIKE '%min%'
    OR target_unit LIKE '%分钟%'
  ) THEN 'exercise_minutes'
  WHEN goal_type = 'exercise' THEN 'exercise_count'
  WHEN goal_type = 'diet' THEN 'food_record_count'
  WHEN goal_type = 'habit' THEN 'habit_checkin_count'
  WHEN goal_type = 'checkin' AND verification_type = '1' THEN 'manual_circle_checkin'
  WHEN goal_type = 'checkin' THEN 'circle_checkin_count'
  ELSE metric_code
END
WHERE metric_code IS NULL OR metric_code = '';

UPDATE weight_daily_task task
LEFT JOIN weight_circle_goal goal ON task.source_id = goal.id
SET task.circle_id = goal.circle_id
WHERE task.task_type = 'circle'
  AND (task.circle_id IS NULL OR task.circle_id = 0);

UPDATE weight_daily_task
SET period_key = CASE
  WHEN task_type <> 'circle' THEN period_key
  WHEN period_key IS NOT NULL AND period_key <> '' THEN period_key
  ELSE CONCAT('day:', DATE_FORMAT(task_date, '%Y-%m-%d'))
END
WHERE task_type = 'circle';

SELECT
  COUNT(*) AS metric_code_backfilled
FROM weight_circle_goal
WHERE metric_code IS NOT NULL
  AND metric_code <> '';

SELECT
  COUNT(*) AS circle_task_backfilled
FROM weight_daily_task
WHERE task_type = 'circle'
  AND circle_id IS NOT NULL
  AND period_key IS NOT NULL
  AND period_key <> '';
