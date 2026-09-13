-- 菜单 SQL
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
values('圈子目标导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:goal:export',       '#', 'admin', sysdate(), '', null, '');