-- 为个人记录和习惯打卡补充图片持久化字段。
-- 可重复执行：字段已存在时只输出提示，不再重复 ALTER。

SET @schema_name = DATABASE();

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'weight_record' AND COLUMN_NAME = 'images') = 0,
  'ALTER TABLE weight_record ADD COLUMN images varchar(2000) NULL COMMENT ''记录图片URL数组(JSON)'' AFTER sync_to_circles',
  'SELECT ''weight_record.images already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'weight_food_record' AND COLUMN_NAME = 'images') = 0,
  'ALTER TABLE weight_food_record ADD COLUMN images varchar(2000) NULL COMMENT ''记录图片URL数组(JSON)'' AFTER image_url',
  'SELECT ''weight_food_record.images already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'weight_exercise_record' AND COLUMN_NAME = 'images') = 0,
  'ALTER TABLE weight_exercise_record ADD COLUMN images varchar(2000) NULL COMMENT ''记录图片URL数组(JSON)'' AFTER sync_to_circles',
  'SELECT ''weight_exercise_record.images already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'weight_water_record' AND COLUMN_NAME = 'images') = 0,
  'ALTER TABLE weight_water_record ADD COLUMN images varchar(2000) NULL COMMENT ''记录图片URL数组(JSON)'' AFTER recorded_at',
  'SELECT ''weight_water_record.images already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'weight_habit_checkin' AND COLUMN_NAME = 'images') = 0,
  'ALTER TABLE weight_habit_checkin ADD COLUMN images varchar(2000) NULL COMMENT ''打卡图片URL数组(JSON)'' AFTER note',
  'SELECT ''weight_habit_checkin.images already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
