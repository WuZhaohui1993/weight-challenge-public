-- 后台管理界面重构 - Phase 1: 菜单结构调整
-- 将21个扁平菜单重组为6个业务模块

-- ============================================
-- 1. 创建新的分组菜单
-- ============================================

-- 用户中心 (parent_id = 2000)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('用户中心', 2000, 2, 'user-center', NULL, 'M', '0', '0', NULL, 'peoples', 'admin', NOW());
SET @user_center_id = LAST_INSERT_ID();

-- 圈子中心 (parent_id = 2000)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('圈子中心', 2000, 3, 'circle-center', NULL, 'M', '0', '0', NULL, 'component', 'admin', NOW());
SET @circle_center_id = LAST_INSERT_ID();

-- 押金中心 (parent_id = 2000)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('押金中心', 2000, 4, 'deposit-center', NULL, 'M', '0', '0', NULL, 'money', 'admin', NOW());
SET @deposit_center_id = LAST_INSERT_ID();

-- 社区中心 (parent_id = 2000)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('社区中心', 2000, 6, 'community-center', NULL, 'M', '0', '0', NULL, 'message', 'admin', NOW());
SET @community_center_id = LAST_INSERT_ID();

-- ============================================
-- 2. 调整现有菜单到新分组
-- ============================================

-- 数据概览保持在第一位
UPDATE sys_menu SET order_num = 1 WHERE menu_id = 2109;

-- 记录管理放在第5位（保留独立入口）
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 2110;

-- ============================================
-- 2.1 用户中心子菜单
-- ============================================

-- 用户管理 -> 移到用户中心下
UPDATE sys_menu SET parent_id = @user_center_id, order_num = 1, menu_name = '用户管理' WHERE menu_id = 2103;

-- ============================================
-- 2.2 圈子中心子菜单  
-- ============================================

-- 圈子管理 -> 移到圈子中心下
UPDATE sys_menu SET parent_id = @circle_center_id, order_num = 1 WHERE menu_id = 2019;

-- 分类管理 -> 移到圈子中心下
UPDATE sys_menu SET parent_id = @circle_center_id, order_num = 2 WHERE menu_id = 2007;

-- ============================================
-- 2.3 押金中心子菜单
-- ============================================

-- 押金管理 -> 移到押金中心下，改名押金流水
UPDATE sys_menu SET parent_id = @deposit_center_id, order_num = 1, menu_name = '押金流水' WHERE menu_id = 2031;

-- ============================================
-- 2.4 社区中心子菜单
-- ============================================

-- 动态审核 -> 移到社区中心下
UPDATE sys_menu SET parent_id = @community_center_id, order_num = 1 WHERE menu_id = 2037;

-- 评论审核 -> 移到社区中心下，改名评论管理
UPDATE sys_menu SET parent_id = @community_center_id, order_num = 2, menu_name = '评论管理' WHERE menu_id = 2025;

-- 成就徽章 -> 移到社区中心下
UPDATE sys_menu SET parent_id = @community_center_id, order_num = 3 WHERE menu_id = 2001;

-- 系统通知 -> 移到社区中心下
UPDATE sys_menu SET parent_id = @community_center_id, order_num = 4 WHERE menu_id = 2073;

-- ============================================
-- 3. 隐藏/删除重复功能菜单
-- ============================================

-- 隐藏已整合到360°画像的菜单 (visible = '1' 表示隐藏)
-- 用户相关
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2043; -- 用户关注关系
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2055; -- 习惯管理
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2013; -- 习惯打卡记录
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2079; -- 同步偏好设置

-- 圈子相关
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2049; -- 圈子目标
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2067; -- 圈子成员
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2085; -- 排行榜缓存

-- 其他
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2091; -- 运动记录 (已整合到记录管理)
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2097; -- 每日任务 (暂时隐藏)
UPDATE sys_menu SET visible = '1' WHERE menu_id = 2061; -- 评论点赞 (已整合到评论管理)

-- ============================================
-- 4. 验证结构
-- ============================================
SELECT menu_id, parent_id, menu_name, path, order_num, visible
FROM sys_menu 
WHERE parent_id = 2000 OR menu_id = 2000
ORDER BY order_num, menu_id;
