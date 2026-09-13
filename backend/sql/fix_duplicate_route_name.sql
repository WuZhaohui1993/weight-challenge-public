-- 修复重复路由名称问题
-- weight模块的 "user" 路由与 system模块的 "user" 路由名称冲突
-- 将 weight/user 的 path 改为 "weight-user" 以避免生成相同的路由名称

-- 1. 查看当前的菜单配置
SELECT menu_id, menu_name, path, component 
FROM sys_menu 
WHERE path = 'user' AND component LIKE '%weight%';

-- 2. 更新 weight 模块的用户管理菜单路径
UPDATE sys_menu 
SET path = 'weight-user' 
WHERE component = 'weight/user/index' OR component = 'ruoyi-weight/user/index';

-- 验证更新结果
SELECT menu_id, menu_name, path, component 
FROM sys_menu 
WHERE path IN ('user', 'weight-user');
