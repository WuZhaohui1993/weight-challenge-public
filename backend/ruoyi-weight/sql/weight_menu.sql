-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章', '3', '1', 'achievement', 'ruoyi-weight/achievement/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:achievement:list', '#', 'admin', sysdate(), '', null, '成就徽章菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类', '3', '1', 'category', 'ruoyi-weight/category/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:category:list', '#', 'admin', sysdate(), '', null, '圈子分类菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:category:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:category:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:category:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:category:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子分类导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:category:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录', '3', '1', 'checkin', 'ruoyi-weight/checkin/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:checkin:list', '#', 'admin', sysdate(), '', null, '习惯打卡记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:checkin:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:checkin:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:checkin:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:checkin:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯打卡记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:checkin:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主', '3', '1', 'circle', 'ruoyi-weight/circle/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:circle:list', '#', 'admin', sysdate(), '', null, '圈子主菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论', '3', '1', 'comment', 'ruoyi-weight/comment/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:comment:list', '#', 'admin', sysdate(), '', null, '评论菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:comment:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:comment:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:comment:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:comment:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:comment:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录', '3', '1', 'deposit', 'ruoyi-weight/deposit/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:deposit:list', '#', 'admin', sysdate(), '', null, '押金记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:deposit:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:deposit:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:deposit:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:deposit:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('押金记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:deposit:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态', '3', '1', 'feed', 'ruoyi-weight/feed/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:feed:list', '#', 'admin', sysdate(), '', null, '圈子动态菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系', '3', '1', 'follow', 'ruoyi-weight/follow/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:follow:list', '#', 'admin', sysdate(), '', null, '用户关注关系菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:follow:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:follow:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:follow:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:follow:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户关注关系导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:follow:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标', '3', '1', 'goal', 'ruoyi-weight/goal/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:goal:list', '#', 'admin', sysdate(), '', null, '圈子目标菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子目标导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义', '3', '1', 'habit', 'ruoyi-weight/habit/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:habit:list', '#', 'admin', sysdate(), '', null, '习惯定义菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:habit:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:habit:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:habit:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:habit:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('习惯定义导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:habit:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞', '3', '1', 'like', 'ruoyi-weight/like/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:like:list', '#', 'admin', sysdate(), '', null, '评论点赞菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员', '3', '1', 'member', 'ruoyi-weight/member/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:member:list', '#', 'admin', sysdate(), '', null, '圈子成员菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:member:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:member:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:member:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:member:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子成员导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:member:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知', '3', '1', 'notification', 'ruoyi-weight/notification/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:notification:list', '#', 'admin', sysdate(), '', null, '消息通知菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息通知导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置', '3', '1', 'preference', 'ruoyi-weight/preference/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:preference:list', '#', 'admin', sysdate(), '', null, '同步偏好设置菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存', '3', '1', 'ranking', 'ruoyi-weight/ranking/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:ranking:list', '#', 'admin', sysdate(), '', null, '排行榜缓存菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录', '3', '1', 'record', 'ruoyi-weight/record/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:record:list', '#', 'admin', sysdate(), '', null, '运动记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('运动记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:record:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务', '3', '1', 'task', 'ruoyi-weight/task/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:task:list', '#', 'admin', sysdate(), '', null, '每日任务菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:task:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:task:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:task:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:task:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('每日任务导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:task:export',       '#', 'admin', sysdate(), '', null, '');-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息', '3', '1', 'user', 'ruoyi-weight/user/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:user:list', '#', 'admin', sysdate(), '', null, '用户扩展信息菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:user:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:user:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:user:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:user:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户扩展信息导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:user:export',       '#', 'admin', sysdate(), '', null, '');