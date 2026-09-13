-- 菜单 SQL
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
values('消息通知导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:notification:export',       '#', 'admin', sysdate(), '', null, '');